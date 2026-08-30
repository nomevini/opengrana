# OpenGrana

Projeto **open source** de finanças pessoais, atualmente em desenvolvimento.

O backend já conta com persistência, migrações via Flyway e o fluxo completo de autenticação (cadastro e login com JWT). A integração com **Open Finance**, a agregação de contas, cartões e investimentos, e as demais telas do frontend ainda estão em desenvolvimento.

## Stack atual

**Backend**

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Security + JWT (OAuth2 Resource Server)
- Spring Validation
- Flyway
- PostgreSQL
- Lombok
- Maven

**Frontend**

- Vite
- React + TypeScript
- Tailwind CSS
- shadcn/ui
- TanStack Query

**Testes**

- JUnit 5 + Mockito (testes unitários)
- Testcontainers (testes de integração com banco real)

**CI/CD**

- GitHub Actions (pipelines separados para backend e frontend)

## Pré-requisitos

- Java 21
- Node.js 22
- PostgreSQL em execução
- Docker (necessário para rodar os testes de integração com Testcontainers)

## Configuração

### Backend

O backend utiliza as seguintes variáveis de ambiente (arquivo `.env` na raiz de `backend/opengrana`):

```text
DB_URL=jdbc:postgresql://localhost:5432/opengrana
DB_USERNAME=postgres
DB_PASSWORD=sua-senha

JWT_SECRET_KEY=uma-chave-secreta-com-no-minimo-32-caracteres
```

O perfil `dev` é usado por padrão. Para selecionar outro perfil, defina `SPRING_PROFILES_ACTIVE`.

Um arquivo `.env.example` está disponível como referência.

### Frontend

O frontend utiliza a seguinte variável de ambiente (arquivo `.env` na raiz de `frontend/opengrana`):

```text
VITE_API_URL=http://localhost:8080
```

## Executando o backend

```bash
cd backend/opengrana
./mvnw spring-boot:run
```

As migrações do Flyway são executadas na inicialização da aplicação.

## Executando o frontend

```bash
cd frontend/opengrana
npm install
npm run dev
```

## Testes

### Backend

```bash
cd backend/opengrana
./mvnw test
```

Alguns testes utilizam **Testcontainers** e exigem o Docker em execução na máquina.

### Frontend

```bash
cd frontend/opengrana
npm run test:run
```

## Funcionalidades implementadas

- [x] Cadastro de usuário (`POST /users`)
- [x] Login com autenticação JWT (`POST /auth/login`)
- [x] Senha criptografada com BCrypt
- [ ] Tratamento padronizado de erros da API
- [ ] Integração com Open Finance
- [ ] Cadastro e gestão de contas
- [ ] Cadastro e gestão de transações

## Estrutura atual

```
opengrana/
├── backend/opengrana    → aplicação Spring Boot
├── frontend/opengrana   → aplicação React (Vite)
└── .github/workflows    → pipelines de CI
```

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para abrir uma _issue_ ou enviar um _pull request_.

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Envie para o repositório (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT — veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 📬 Contato

Dúvidas ou sugestões? Abra uma _issue_ no repositório.

---

Principais mudanças em relação à versão anterior: adicionei a stack de frontend e testes que já estão em uso, a seção de "Funcionalidades implementadas" (bom pra visitantes verem o progresso real de cara), a variável `JWT_SECRET_KEY`, o pré-requisito do Docker (por causa do Testcontainers), e o comando de execução/teste do frontend. Quer que eu ajuste algo, como a lista de funcionalidades pendentes, ou incluir os badges de status do CI que configuramos?
