# 0002 — JWT em cookie httpOnly

- Status: Aceito
- Data: 2026-11-22

## Contexto

A aplicação é uma SPA que precisa autenticar requisições sem expor o token ao JavaScript. Guardar o JWT em `localStorage` ou `sessionStorage` o torna legível por qualquer script da página, inclusive em caso de XSS.

## Decisão

O login gera um JWT HS256 com 60 minutos de validade e o devolve em um cookie `access_token` com `HttpOnly`, `Secure`, `SameSite=Strict` e `Path=/`. O resource server do Spring Security usa um `CookieBearerTokenResolver` para ler o token do cookie, e o frontend descobre se há sessão chamando `GET /users/me`.

O cookie é configurado com o mesmo `Path` no login e no logout, para que o logout sobrescreva o valor com `Max-Age=0`.

## Consequências

O token fica invisível para o JavaScript, mitigando roubo via XSS. `SameSite=Strict` reduz o risco de CSRF, o que permite manter o CSRF do Spring desabilitado em uma API stateless. O custo é que o token não pode ser lido pelo frontend e **não é revogável**: o logout apenas apaga o cookie, e um token emitido continua válido até expirar.

## Revisar quando

For exigida revogação imediata, login cross-site legítimo, SSO/OIDC ou rotação de tokens.
