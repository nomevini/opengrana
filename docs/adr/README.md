# Decisões de arquitetura (ADRs)

Cada ADR registra uma decisão difícil de reverter, com contexto, decisão, consequências e gatilho de revisão.

| ADR | Título | Status |
|---|---|---|
| [0001](0001-flyway-migrations.md) | Migrations Flyway como única fonte do schema | Aceito |
| [0002](0002-jwt-in-httponly-cookie.md) | JWT em cookie httpOnly | Aceito |
| [0003](0003-problem-details-for-errors.md) | Problem Details (RFC 9457) para erros | Aceito |
| [0004](0004-representacao-de-dinheiro.md) | Representação de dinheiro | Aceito |
| [0005](0005-saldo-derivado.md) | Saldo derivado | Aceito |
| [0006](0006-transacoes.md) | Ciclo de vida e correção de transações | Reservado (fase seguinte) |
| [0007](0007-identidade-publica-e-isolamento.md) | Identidade pública e isolamento | Aceito |
| [0008](0008-ciclo-de-vida-e-concorrencia.md) | Ciclo de vida de conta e concorrência | Aceito |

## Template

```markdown
# NNNN — Título

- Status: Aceito
- Data: AAAA-MM-DD

## Contexto
O que motivou a decisão e quais opções foram consideradas.

## Decisão
O que foi escolhido.

## Consequências
O que ganha e o que custa.

## Revisar quando
Condição que tornaria a decisão obsoleta.
```
