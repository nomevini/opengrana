# OpenGrana

Projeto **open source** de finanças pessoais, atualmente em desenvolvimento.

O repositório contém a estrutura inicial do backend, incluindo persistência e migração da tabela de usuários. A integração com **Open Finance**, a agregação de contas, cartões e investimentos e o frontend ainda estão em desenvolvimento.

## Stack atual

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- PostgreSQL
- Maven

## Pré-requisitos

- Java 21
- PostgreSQL em execução

## Configuração

O backend utiliza as seguintes variáveis de ambiente:

```text
DB_URL=jdbc:postgresql://localhost:5432/opengrana
DB_USERNAME=postgres
DB_PASSWORD=sua-senha
```

O perfil `dev` é usado por padrão. Para selecionar outro perfil, defina `SPRING_PROFILES_ACTIVE`.

## Executando o backend

```bash
cd backend/opengrana
./mvnw spring-boot:run
```

As migrações do Flyway são executadas na inicialização da aplicação. A migração atual cria a tabela `users`.

## Testes

```bash
cd backend/opengrana
./mvnw test
```

## Estrutura atual

- `backend/opengrana`: aplicação Spring Boot
- `frontend`: reservado para a interface da aplicação; ainda em desenvolvimento

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
