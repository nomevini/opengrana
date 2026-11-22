# 0003 — Problem Details (RFC 9457) para erros

- Status: Aceito
- Data: 2026-11-22

## Contexto

A API precisava de um formato de erro consistente entre domínios, extensível sem quebrar clientes e com correlação entre logs e resposta.

## Decisão

Usar `ProblemDetail` do Spring (RFC 9457) em todas as respostas de erro, com `type` como URI estável, `title`, `status`, `detail`, campos extras quando necessários (`errors`, `traceId`) e `X-Request-Id` gerado por `CorrelationIdFilter`.

## Consequências

Ganha-se contrato uniforme e programável pelo `type`. O custo é um vocabulário de `type` que precisa ser versionado junto com a API.

## Revisar quando

Surgir um cliente que não consiga interpretar `application/problem+json` ou uma necessidade de i18n de `title`/`detail`.
