# 0007 — Identidade pública e isolamento

- Status: Aceito
- Data: 2026-11-22

## Contexto

IDs sequenciais vazam volume e permitem enumeração. Além disso, o modelo de acesso vai evoluir para membership e consentimentos do Open Finance.

## Decisão

`BIGINT` interno + `public_id` UUIDv7 exposto na API. `external_id` é reservado para o id da conta na Pluggy e `item_id` para a conexão. Toda consulta filtra pelo dono; recurso de outro usuário responde `404`.

## Consequências

Uma coluna e índices a mais, mas o contrato de API fica desacoplado da persistência e o vínculo externo é explícito.

## Revisar quando

Surgirem contas conjuntas ou consentimentos Open Finance que permitam acesso por múltiplos usuários.
