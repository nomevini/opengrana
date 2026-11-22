# 0008 — Ciclo de vida de conta e concorrência

- Status: Aceito
- Data: 2026-11-22

## Contexto

Registros financeiros não devem ser apagados. Além disso, duas abas podem editar a mesma conta e sobrescrever mudanças.

## Decisão

`ACTIVE`, `ARCHIVED` e `CLOSED`; `DELETE` encerra a conta. `@Version` é exposto como `ETag` no `GET`, `POST` e `PATCH`; `PATCH`/`DELETE` exigem `If-Match`. Header ausente → `428`; inválido → `400`; divergente → `412`.

## Consequências

O cliente precisa reenviar o `ETag` recebido. A auditoria é preservada porque nunca há delete físico.

## Revisar quando

For necessário reabrir uma conta encerrada ou manter histórico imutável por período.
