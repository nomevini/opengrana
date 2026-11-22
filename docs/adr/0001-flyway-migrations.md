# 0001 — Migrations Flyway como única fonte do schema

- Status: Aceito
- Data: 2026-11-22

## Contexto

O schema do banco precisa ser reproduzível em qualquer ambiente (local, CI e produção) e auditável por Git. Gerar tabelas a partir das entidades (`ddl-auto: update/create`) hiding diferenças entre ambientes e não deixa histórico das mudanças.

## Decisão

O schema é gerenciado exclusivamente por migrations SQL do Flyway em `backend/opengrana/src/main/resources/db/migration`, com nomes versionados (`V1__...`, `V2__...`). O Hibernate roda com `spring.jpa.hibernate.ddl-auto: validate` e `spring.jpa.open-in-view: false`: ele apenas confere se as entidades batem com o banco, sem criar ou alterar nada.

Migrations já aplicadas **nunca** são editadas. Correções e mudanças viram uma nova migration.

## Consequências

Ganha-se reprodutibilidade e histórico das mudanças. O custo é manter o SQL e o mapeamento JPA sincronizados à mão — uma divergência aparece como falha de `validate` no boot, o que é desejável.

## Revisar quando

Surgir necessidade de schemas dinâmicos por tenant, ou uma ferramenta de diff de schema que substitua a escrita manual das migrations.
