# 0004 — Representação de dinheiro

- Status: Aceito
- Data: 2026-11-22

## Contexto

Valores monetários precisam ser exatos, auditáveis e interoperáveis com o Open Finance Brasil, que trabalha com decimais em string.

## Decisão

`NUMERIC(19,4)` no banco, `BigDecimal` + value object `Money` em Java, `currency` ISO 4217 por conta e dinheiro serializado como string na API. Escala de negócio por moeda via `Currency.getDefaultFractionDigits()` e arredondamento `HALF_UP` explícito na borda.

## Consequências

Sem erro binário e com formato estável na API. Custo de um value object e da normalização na entrada. `19,4` cobre as moedas ISO 4217 e os padrões do Open Finance.

## Revisar quando

Surgir cripto (mais casas decimais), câmbio real ou necessidade extrema de performance em agregações.
