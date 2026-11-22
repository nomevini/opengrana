# 0006 — Ciclo de vida e correção de transações

- Status: Reservado (fase seguinte)
- Data: 2026-11-22

> **Aviso:** decisão de projeto para o próximo bloco de implementação. Nada descrito aqui existe na API hoje: este módulo ainda só implementa contas manuais.

## Contexto

Transações financeiras precisam de auditoria, correção controlada e convivência entre lançamentos manuais e dados importados do Open Finance. Um delete físico apagaria o histórico; editar um fato importado quebraria a reconciliação; re-syncs duplicados criariam lançamentos fantasmas.

## Decisão (planejada)

- **Saldo derivado** de `POSTED`, com `opening_balance` como checkpoint. `VOIDED`, `REVERSED`, soft-deletadas e `PENDING` não entram no saldo consolidado.
- `posted_at` é a data de competência; nunca ordenar saldo por `created_at` (data de ingestão).
- `amount` é assinado pela origem; não aplicar sinal duas vezes.
- **Edição com revisão:** alterar um fato manual gera um registro de `before`/`after` e novo `ETag`. Fato de transação importada é imputável ao provedor e responde `409`.
- **Delete é lógico:** `deleted_at` com janela de 30 dias e purga posterior idempotente, que não altera o saldo já derivado sem a transação.
- `ignored` remove a transação apenas do orçamento, nunca do saldo.
- Provedor que desfaz uma transação gera `REVERSED`, nunca delete.
- **Idempotência de ingestão:** `UNIQUE (connection_id, external_id)` com upsert. `external_id` ignorado ou purgado vira tombstone para não ressuscitar no re-sync.
- Duplicata manual × Open Finance só é conciliada com confirmação do usuário (`transaction_matches`); cada fato conta uma vez.
- Edições usam `@Version` exposto como `ETag`, com `If-Match`, `428` para header ausente e `412` para versão obsoleta.

## Consequências

Histórico preservado e correções rastreáveis. O custo é um modelo mais complexo (revisões, tombstones, janela de purga) e a necessidade de regras explícitas por origem da transação.

## Revisar quando

O bloco de transações começar, ou surgirem requisitos de retenção/LGPD que mudem a janela e a política de purga.
