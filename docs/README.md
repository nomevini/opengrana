# Documentação do OpenGrana

Documentação técnica do projeto: arquitetura, fluxos principais, pipeline de CI e decisões de arquitetura (ADRs).

Os diagramas usam [Mermaid](https://mermaid.js.org/) e são renderizados automaticamente pelo GitHub e pelo preview de Markdown do VS Code (1.121 ou superior).

## Arquitetura

- [Visão geral](architecture/overview.md)
- [Pacotes do backend](architecture/backend-packages.md)
- [Estrutura do frontend](architecture/frontend-structure.md)
- [Modelo de dados](architecture/data-model.md)

## Fluxos

- [Cadastro de usuário](flows/registration.md)
- [Autenticação e sessão](flows/authentication.md)
- [Contas manuais](flows/accounts.md)
- [Tratamento de erros](flows/error-handling.md)

## CI/CD

- [Pipeline de CI](ci/pipeline.md)

## Decisões de arquitetura (ADRs)

- [Índice e template](adr/README.md)

## Mantendo a documentação atualizada

- Altere o diagrama no mesmo PR que muda o fluxo descrito.
- Decisões relevantes e difíceis de reverter viram um ADR em `adr/`.
- Se um diagrama descreve algo ainda em desenvolvimento, sinalize isso no próprio arquivo.
