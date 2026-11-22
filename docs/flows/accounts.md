# Fluxo de contas manuais

Este bloco implementa apenas **contas manuais**. Transações, snapshots e Open Finance virão depois.

## Regras

- O dono da conta vem sempre de `jwt.getSubject()`. O corpo da requisição **nunca** aceita `userId`.
- `opening_balance` é imutável e é o saldo de abertura; o saldo atual será derivado das transações no futuro.
- `DELETE` encerra a conta (`CLOSED`) e preenche `closedAt`. Nunca há delete físico.
- `publicId` é UUIDv7 exposto na API. `externalId` (Pluggy) é outro conceito e hoje é sempre nulo.
- `balance.asOf` vem de `balance_as_of`, não de `created_at` nem de `updated_at`.
- Conta de outro usuário responde `404`, a mesma resposta de conta inexistente.
- `GET` devolve `ETag`; `PATCH` e `DELETE` exigem `If-Match`. Divergência → `412`; header ausente → `428`.

## Endpoints

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `POST` | `/accounts` | Cria conta para o usuário autenticado | `201` + `Location` + `ETag` |
| `GET` | `/accounts` | Lista contas do usuário, ordenadas por nome | `200` |
| `GET` | `/accounts/{publicId}` | Detalhe da conta | `200` + `ETag` |
| `PATCH` | `/accounts/{publicId}` | Edita apenas `name` e `type` | `200` + `ETag` |
| `DELETE` | `/accounts/{publicId}` | Encerra a conta | `204` |

## Exemplo

Request:

```json
POST /accounts
{
  "name": "Nubank",
  "type": "CHECKING",
  "initialBalance": "1500.00",
  "currency": "BRL"
}
```

Response:

```json
{
  "publicId": "0191a6f4-1c7e-7d1a-8e0e-2c9b1f6a4d10",
  "name": "Nubank",
  "type": "CHECKING",
  "status": "ACTIVE",
  "source": "MANUAL",
  "balance": {
    "amount": "1500.00",
    "currency": "BRL",
    "asOf": "2026-11-22T10:00:00",
    "source": "MANUAL",
    "stale": false
  },
  "createdAt": "2026-11-22T10:00:00",
  "version": 0
}
```

Em seguida, o cliente envia `If-Match: "0"` no `PATCH`/`DELETE` e passa a usar o novo `ETag` devolvido.

## Preparação para Open Finance

O MCP da Pluggy mostrou que:

- `GET /accounts?itemId=...` devolve `type` (`BANK`/`CREDIT`) e `subtype` (`CHECKING_ACCOUNT`, `SAVINGS_ACCOUNT`, `CREDIT_CARD`). Mapeamento: `BANK/CHECKING_ACCOUNT → CHECKING`, `BANK/SAVINGS_ACCOUNT → SAVINGS`, `CREDIT/CREDIT_CARD → CREDIT_CARD`. `INVESTMENT` e `CASH` seguem manuais.
- Eventos `transactions/*` **não** trazem `clientUserId`; por isso `accounts.item_id` é necessário para rotear o webhook até o usuário.
- Não existe evento `accounts/*`. Contas e saldos chegam via `item/updated`, com posterior consulta a `/accounts`.
- `status` de cartão da Pluggy (`ACTIVE`, `BLOCKED`, `CANCELLED`) não deve ser mapeado para `AccountStatus`: são conceitos diferentes.
- Valores da API da Pluggy vêm como número JSON; converter pela representação textual para `BigDecimal`, nunca via `double`.
