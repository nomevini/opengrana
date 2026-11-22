# Pacotes do backend

O backend segue organização por domínio (feature) em vez de por camada técnica. Cada pacote carrega seu próprio controller, service, repository e DTOs.

```mermaid
flowchart TB
    subgraph config
        SC["SecurityConfig<br/>PasswordEncoder, JwtEncoder,<br/>JwtDecoder, CORS, FilterChain"]
        CIF[CorrelationIdFilter]
    end

    subgraph exception
        GEH[GlobalExceptionHandler]
    end

    subgraph auth
        AC[AuthController] --> JS[JwtService]
        AC --> AM[AuthenticationManager]
        AM -.-> UDS[UserDetailsServiceImpl]
        UDS --> UP[UserPrincipal]
        CBR[CookieBearerTokenResolver]
    end

    subgraph user
        UCtl[UserController] --> USvc[UserService]
        USvc --> URepo[UserRepository]
        URepo --> UEnt["User<br/>Role"]
    end

    subgraph account
        ACtl[AccountController] --> ASvc[AccountService]
        ASvc --> ARepo[AccountRepository]
        ASvc --> USvc
        ARepo --> AEnt["Account<br/>AccountType, AccountStatus,<br/>AccountSource, Money"]
        AExc["exception/<br/>AccountNotfoundException, StaleAccountException,<br/>MissingIfMatchException, InvalidIfMatchException,<br/>InvalidCurrencyException"]
        AEnt -.-> AExc
    end

    UDS --> URepo
    USvc -.-> SC
    SC -.-> CBR
    CIF -.-> GEH
```

## Responsabilidades

| Pacote | Responsabilidade |
|---|---|
| `config` | Configuração de segurança: encoder de senha, chaves e beans JWT, CORS (incluindo `PATCH`, `If-Match` e `ETag` exposto), `SecurityFilterChain` e correlation id |
| `exception` | `GlobalExceptionHandler`, que traduz exceptions em respostas `ProblemDetail` (RFC 9457) |
| `auth` | Login/logout, geração do JWT, leitura do token a partir do cookie e integração com o Spring Security (`UserPrincipal`, `UserDetailsServiceImpl`) |
| `user` | Entidade `User`, cadastro e consulta do usuário autenticado (`/users/me`) |
| `account` | Contas manuais, saldo de abertura, origem da conta e isolamento por usuário |

## Convenções

- `UserPrincipal` é um adapter: a entidade `User` não implementa `UserDetails`, mantendo o domínio livre de dependências do Spring Security.
- Entidades usam Lombok apenas onde é seguro: `@Getter` e `@NoArgsConstructor(access = PROTECTED)`. Não há `@Setter`, `@AllArgsConstructor` nem `@Builder` em `Account`/`Money`, para preservar invariantes financeiras.
- DTOs são `record` (`UserResponse`, `AccountResponse`, etc.).
- Services e controllers usam injeção por construtor explícito.
- Nomes de classes, campos, tabelas e colunas são sempre em inglês.
- Testes espelham a estrutura de pacotes em `src/test/java`; o helper de JWT fica em `support/SecurityTestSupport`.
