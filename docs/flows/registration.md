# Fluxo de cadastro

`POST /users` é público (não exige autenticação). A senha é criptografada com BCrypt antes de persistir e o usuário nasce com o papel `USER`.

```mermaid
sequenceDiagram
    participant F as Frontend
    participant UC as UserController
    participant US as UserService
    participant UR as UserRepository
    participant EH as GlobalExceptionHandler

    F->>UC: POST /users (CreateUserRequest)
    UC->>UC: Valida DTO com Bean Validation
    alt DTO inválido
        UC-->>EH: MethodArgumentNotValidException
        EH-->>F: 400 ProblemDetail com lista de campos
    else DTO válido
        UC->>US: create(request)
        US->>UR: existsByEmail(email)
        alt email já existe
            US-->>EH: EmailAlreadyExistsException
            EH-->>F: 409 ProblemDetail
        else email livre
            US->>US: BCrypt encode da senha e role USER
            US->>UR: save(user)
            UR-->>US: User salvo
            US-->>UC: UserResponse
            UC-->>F: 201 Created
        end
    end
```

## Regras de validação (`CreateUserRequest`)

| Campo | Regra |
|---|---|
| `name` | obrigatório, 2 a 150 caracteres |
| `email` | obrigatório, formato de email válido |
| `password` | obrigatória, 8 a 100 caracteres |

## Respostas

| Situação | Status |
|---|---|
| Usuário criado | `201 Created` com `UserResponse` (id, name, email) |
| Campos inválidos | `400 Bad Request` com a lista de campos que falharam |
| Email já cadastrado | `409 Conflict` |

A resposta nunca inclui a senha, nem em hash.
