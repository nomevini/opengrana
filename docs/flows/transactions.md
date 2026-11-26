# Fluxo de transações

Este documento consolida as decisões do bloco de transações. As regras abaixo **ainda não estão implementadas**: a API virá com as issues [#34](https://github.com/nomevini/opengrana/issues/34), [#35](https://github.com/nomevini/opengrana/issues/35) e [#36](https://github.com/nomevini/opengrana/issues/36), sub-issues de [#7](https://github.com/nomevini/opengrana/issues/7).

> **Estado em 2026-11-26:** decisões fechadas na issue [#33](https://github.com/nomevini/opengrana/issues/33) e ADR [0006](../adr/0006-transacoes.md) aceito. A implementação ainda não começou.

## Decisões (confirmadas em 2026-11-26)

### 1. Soft delete apenas para transações manuais

- A janela é de **30 dias**, configurável por `app.transactions.purge-after: P30D`, com purga diária.
- `deleted_at` só pode ser preenchido quando `source = MANUAL`. Transações importadas do Pluggy/Open Finance **nunca são excluídas**.
- `DELETE /transactions/{id}` responde `204` para transação manual; para importada responde `409` com `type .../transactions/immutable`.
- `POST /transactions/{id}/restore` responde `200` dentro da janela e `409` com `type .../transactions/not-restorable` fora dela.
- A purga filtra `source = 'MANUAL' AND deleted_at < now() - :purgeAfter`, em lotes de 1000, com `Clock` injetado, idempotente e com lock quando houver mais de uma instância. Nunca toca importadas.
- Proteção sugerida no schema da V3: `CHECK (source = 'MANUAL' OR deleted_at IS NULL)` e índice parcial `(deleted_at) WHERE deleted_at IS NOT NULL AND source = 'MANUAL'`.
- Para a transação importada, o usuário usa `ignored` (fora do orçamento, continua no saldo) ou o provedor marca `REVERSED`.

### 2. Manual conciliada

Uma **transação manual conciliada** é um lançamento criado pelo usuário (`source = MANUAL`) pareado com uma transação importada e confirmado por ele. O vínculo vive em `transaction_matches`, com `manual_transaction_id`, `external_transaction_id`, `confirmed_by` e `confirmed_at`.

O pareamento evita que o mesmo fato financeiro seja contado duas vezes: **cada fato conta uma vez nos cálculos**.

Depois da confirmação:

- alterar o **fato** (valor, data de competência, descrição) responde `409` com `type .../transactions/reconciled`;
- alterar **anotações** (categoria, tags, nota, `ignored`) continua permitido;
- excluir a transação responde `409`.

Desfazer a conciliação para corrigir o fato não faz parte deste bloco; precisa de um fluxo próprio na fase de conciliação.

### 3. Revisões

Toda alteração do **fato** de uma transação manual gera uma linha em `transaction_revisions`:

```text
transaction_revisions
  id, transaction_id, changed_by (USER | PROVIDER | SYSTEM),
  changed_at, before JSONB, after JSONB
```

Exemplo de `before`/`after`:

```json
{
  "changed_by": "USER",
  "changed_at": "2026-11-26T14:03:12Z",
  "before": { "amount": "45.0000", "currency": "BRL", "posted_at": "2026-11-10", "description": "Almoço" },
  "after":  { "amount": "42.0000", "currency": "BRL", "posted_at": "2026-11-11", "description": "Almoço" }
}
```

- Dinheiro é serializado como string para preservar a precisão de `NUMERIC(19,4)`.
- Anotações não geram revisão — apenas o fato financeiro.
- Revisão e `ETag` são conceitos distintos: o `ETag` (`@Version`) controla concorrência (`428`/`412`); a revisão registra auditoria (o que mudou, quando e por quem).
- Foi escolhida tabela própria com JSONB, sem Hibernate Envers, para controlar o formato do snapshot e registrar a origem da mudança.

### 4. Transação importada: `ignored` ou `REVERSED`

- `DELETE` em importada responde `409 .../transactions/immutable`.
- `ignored` tira a linha do orçamento, mas a mantém no saldo; `REVERSED` é usado quando o provedor desfaz o lançamento.
- Anotações (`category`, `tags`, `notes`) continuam editáveis.
- Motivo: apagar faria o saldo divergir do extrato e o próximo sync reimportaria a linha.

## Matriz de editabilidade

| Ação | Manual livre | Manual conciliada | Importada |
|---|---|---|---|
| Editar fato | `200` + revisão + novo `ETag` | `409 .../transactions/reconciled` | `409 .../transactions/immutable` |
| Editar anotação | `200` | `200` | `200` |
| Excluir | `204` (soft) | `409 .../transactions/reconciled` | `409 .../transactions/immutable` |
| Restaurar | `200` na janela | — | — |

## Revisões futuras e gatilhos

Estes itens estão **decididos**, mas só serão implementados quando o gatilho acontecer:

| Item | Decisão | Gatilho de revisão |
|---|---|---|
| Trigger de proteção no banco | Não implementar agora; manter regras na aplicação e o `CHECK` declarativo na V3 | Mais de um gravador no banco, acesso administrativo direto ou requisito de auditoria |
| `/v1` e `Idempotency-Key` | Adiar | Primeiro cliente externo, webhook de provedor ou operação de pagamento |
| Registro mínimo da purga | Não manter no MVP; apagar a linha junto com as revisões | Política de retenção/LGPD ou necessidade de provar exclusões passadas |

A idempotência de **ingestão** não está adiada: `UNIQUE (connection_id, external_id)` com upsert e tombstone para `external_id` ignorado ou purgado continuam obrigatórios na #34.

## Próximos passos

1. Implementar a V3 (#34), com migration, entidades e saldo derivado.
2. Expor a API de criação e edição com revisão (#35).
3. Implementar soft delete, restore e purga (#36).
