# Autenticação e sessão

Login com email e senha gera um JWT (HS256, 60 minutos) entregue em um cookie `httpOnly`. O frontend nunca lê o token: o navegador o envia automaticamente em cada requisição. Contexto da decisão em [ADR 0002](../adr/0002-jwt-in-httponly-cookie.md).

```mermaid
sequenceDiagram
    actor U as Usuário
    participant F as Frontend
    participant CIF as CorrelationIdFilter
    participant AC as AuthController
    participant AM as AuthenticationManager
    participant J as JwtService
    participant R as CookieBearerTokenResolver
    participant UC as UserController

    Note over F,CIF: CorrelationIdFilter usa X-Request-Id ou gera um UUID,<br/>devolve o header e grava traceId no MDC

    Note over U,UC: 1. Login
    U->>F: Informa email e senha
    F->>CIF: POST /auth/login
    CIF->>AC: request com traceId
    AC->>AM: authenticate(email, senha)
    Note right of AM: UserDetailsServiceImpl busca o usuário e BCrypt compara a senha
    alt credenciais inválidas
        AM-->>AC: BadCredentialsException
        AC-->>F: 401 ProblemDetail
    else credenciais válidas
        AC->>J: generateToken(email)
        J-->>AC: JWT HS256 (60 min)
        AC-->>F: 200 + Set-Cookie access_token (HttpOnly, Secure, SameSite=Strict)
    end

    Note over U,UC: 2. Requisição autenticada
    F->>CIF: GET /users/me com cookie access_token
    CIF->>R: request com traceId
    R->>R: Extrai e valida o JWT
    alt token ausente, inválido ou expirado
        R-->>F: 401
    else token válido
        R->>UC: requisição autenticada
        alt usuário do token não existe mais
            UC-->>F: 401 ProblemDetail
        else usuário existe
            UC-->>F: 200 UserResponse
        end
    end

    Note over U,UC: 3. Logout
    F->>CIF: POST /auth/logout
    CIF->>AC: request com traceId
    AC-->>F: 200 + Set-Cookie access_token (Max-Age=0)
```

## Atributos do cookie

| Atributo | Valor | Motivo |
|---|---|---|
| Nome | `access_token` | Nome padrão, reconhecido por ferramentas de debug |
| `HttpOnly` | sim | JavaScript não consegue ler o token (mitiga roubo via XSS) |
| `Secure` | sim | Só trafega por HTTPS (navegadores tratam `localhost` como contexto seguro) |
| `SameSite` | `Strict` | O cookie não é enviado em requisições originadas de outros sites |
| `Path` | `/` | Mesmo valor no login e no logout, para o logout sobrescrever o cookie |
| `Max-Age` | 60 minutos | Alinhado à expiração do JWT |

## Como o frontend sabe se há sessão

Como o token é inacessível ao JavaScript, o `AuthProvider` chama `GET /users/me` ao montar. Se a chamada responde `200`, há sessão; se responde `401`, não há. Isso inclui o caso de um token ainda válido cujo usuário foi removido: a API responde `401` e o frontend volta ao estado deslogado.

## Rotas públicas

`POST /users`, `POST /auth/login` e `POST /auth/logout`. Todas as demais exigem autenticação.

## CORS

Origens permitidas vêm de `cors.allowed-origins` (variável `CORS_ALLOWED_ORIGINS`), nunca `*`, porque a API usa credenciais. Em desenvolvimento o padrão é `http://localhost:5173`; em produção a variável é obrigatória.

A configuração cobre os métodos `GET`, `POST`, `PUT`, `PATCH`, `DELETE` e `OPTIONS`. Além de `Authorization` e `Content-Type`, o navegador pode enviar `If-Match` e `X-Request-Id`; `ETag` e `X-Request-Id` são expostos ao JavaScript. Isso é necessário para o fluxo de concorrência otimista das contas (ver [Fluxo de contas](accounts.md)).

## Correlação de requisições

`CorrelationIdFilter` aceita o header `X-Request-Id` enviado pelo cliente ou gera um UUID quando ele não vem. O mesmo valor é devolvido na resposta e gravado no `MDC` como `traceId`; quando a requisição termina com erro, o `ProblemDetail` inclui esse `traceId`. Assim, um erro reportado pelo usuário pode ser localizado nos logs sem expor detalhes internos.

## Limitações conhecidas

- **Token não revogável:** o logout apenas instrui o navegador a descartar o cookie. Um token já emitido continua válido até expirar, e desativar um usuário (`active = false`) não derruba tokens existentes. O `GET /users/me` só responde `401` quando o registro deixa de existir; um usuário desativado com token válido ainda recebe `200` nesse endpoint.
- **Sem rate limiting** nem bloqueio de conta após tentativas falhas de login.
- **Sem verificação de email** no cadastro.

Todos esses pontos estão registrados como issues de segurança no repositório.
