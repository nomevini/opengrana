# 0005 — Saldo derivado

- Status: Aceito
- Data: 2026-11-22

## Contexto

Saldo mutável gera lost update e perde auditabilidade. As transações virão primeiro de lançamento manual e depois da Pluggy.

## Decisão

O saldo é derivado. `opening_balance` é imutável e representa o primeiro checkpoint. `balance_as_of` guarda a data daquele saldo e não muda quando a conta é editada. Snapshots com `asOf`/`source` entram na fase Open Finance. O DTO já expõe `asOf`, `source` e `stale`.

## Consequências

Correção de saldo vira lançamento de ajuste, não edição de saldo. Divergência entre saldo derivado e saldo reportado gera alerta, nunca auto-correção silenciosa.

## Revisar quando

O volume exigir materialização do saldo, ou o provedor se tornar a única fonte aceita.
