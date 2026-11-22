# OpenGrana frontend

Base do frontend em React, Vite, Tailwind CSS v4 e shadcn/ui. A arquitetura das pastas e as decisões estão em [`docs/architecture/frontend-structure.md`](../../docs/architecture/frontend-structure.md).

## Comandos

```bash
npm run dev        # servidor de desenvolvimento
npm run build      # tsc -b + vite build
npm run lint       # eslint
npm run test       # vitest em modo watch
npm run test:run   # vitest uma vez (usado no CI)
npm run preview    # serve o build localmente
```

Os componentes shadcn/ui ficam em `src/shared/components/ui/` (caminho configurado em `components.json`). Adicione novos componentes com o CLI do shadcn, sem alterar esse caminho.

## Integração com a API

A API do backend e seus fluxos estão documentados em [`docs/`](../../docs/README.md). Em particular: [autenticação](../../docs/flows/authentication.md), [contas manuais](../../docs/flows/accounts.md) e [erros](../../docs/flows/error-handling.md). O cliente HTTP base fica em `src/services/api.ts` e usa `credentials: 'include'` para enviar o cookie de sessão.
