# 0006 — Ciclo de vida e correção de transações

- Status: Aceito
- Data: 2026-11-22
- Atualizado em: 2026-11-26

> Decisão fechada na issue [#33](https://github.com/nomevini/opengrana/issues/33). A implementação ainda não existe: hoje só há contas manuais. As regras entram com as issues [#34](https://github.com/nomevini/opengrana/issues/34), [#35](https://github.com/nomevini/opengrana/issues/35) e [#36](https://github.com/nomevini/opengrana/issues/36). Detalhes em [flows/transactions.md](../flows/transactions.md).

## Contexto

Transações financeiras precisam de auditoria, correção controlada e convivência entre lançamentos manuais e dados importados do Open Finance. Um delete físico apagaria o histórico; editar um fato importado quebraria a reconciliação; re-syncs duplicados criariam lançamentos fantasmas.

## Decisão

- **Saldo derivado** de `POSTED`, com `opening_balance` como checkpoint. `VOIDED`, `REVERSED`, soft-deletadas e `PENDING` não entram no saldo consolidado.
- `posted_at` é a data de competência; nunca ordenar saldo por `created_at` (data de ingestão).
- `amount` é assinado pela origem; não aplicar sinal duas vezes.
- **Edição com revisão:** alterar um fato manual gera uma linha em `transaction_revisions` com `before`/`after` JSONB, autor e horário, e novo `ETag`. Não usar Hibernate Envers. Fato de transação importada responde `409 .../transactions/immutable`.
- **Manual conciliada:** pareada com uma importada via `transaction_matches`; editar o fato responde `409 .../transactions/reconciled`, enquanto anotações continuam editáveis. Desconciliar fica para a fase de conciliação.
- **Delete é lógico e só existe para transação manual:** `deleted_at` com janela de 30 dias configurável (`app.transactions.purge-after: P30D`) e purga diária idempotente, que não altera o saldo já derivado sem a transação. Transação importada nunca é excluída; usa `ignored` ou `REVERSED`.
- A purga apaga a transação junto com suas revisões; não há registro paralelo no MVP.
- **Proteção no banco:** triggers ficam para evolução futura. A V3 usa apenas constraints declarativas, como `CHECK (source = 'MANUAL' OR deleted_at IS NULL)`.
- **Versionamento e idempotência de escrita:** `/v1` e `Idempotency-Key` adiados; `type` de erro e `traceId` permanecem estáveis. Idempotência de **ingestão** continua obrigatória: `UNIQUE (connection_id, external_id)` com upsert e tombstone para `external_id` ignorado ou purgado.
- `ignored` remove a transação apenas do orçamento, nunca do saldo.
- Provedor que desfaz uma transação gera `REVERSED`, nunca delete.
- Duplicata manual × Open Finance só é conciliada com confirmação do usuário (`transaction_matches`); cada fato conta uma vez.
- Edições usam `@Version` exposto como `ETag`, com `If-Match`, `428` para header ausente e `412` para versão obsoleta.

## Consequências

Histórico preservado e correções rastreáveis. O custo é um modelo mais complexo (revisões, tombstones, janela de purga) e a necessidade de regras explícitas por origem da transação. Regras de proteção ficam na aplicação até que exista mais de um gravador no banco.

## Revisar quando

- Primeiro cliente externo, webhooks de provedor ou operação de pagamento (versionamento e idempotência de escrita).
- Requisito de retenção/LGPD ou auditoria que mude a janela, a auditoria da purga ou exija trigger no banco.
- Mais de uma aplicação/escritor acessando o banco diretamente.
