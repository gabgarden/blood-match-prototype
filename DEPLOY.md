# Deploy (Heroku)

## 1. Pre-requisitos
- Heroku CLI instalado
- App criada no Heroku
- Add-on MongoDB configurado (ou URI externa)
- Java 17 configurado no ambiente local e no Heroku
- Repositório limpo ou com apenas os arquivos esperados para release

## 2. Checklist antes do commit
```bash
./mvnw test
git status --short
```

Confirme que:
- `.env` nao esta versionado
- `JWT_SECRET` esta apenas em variavel de ambiente local/Heroku
- `.env.example` foi mantido apenas com placeholders

## 3. Variaveis de ambiente (Config Vars)
Configure no Heroku:
- `MONGODB_URI`
- `MONGODB_DATABASE` (opcional, default: `bloodmatch`)
- `JWT_SECRET` (obrigatorio, minimo 32 caracteres)
- `JWT_EXPIRATION_MS` (opcional, default: `86400000`)
- `JWT_ISSUER` (opcional, default: `bloodmatch-api`)

Exemplo:
```bash
heroku config:set MONGODB_URI='mongodb+srv://...'
heroku config:set MONGODB_DATABASE='bloodmatch'
heroku config:set JWT_SECRET='coloque-um-segredo-real-com-32-ou-mais-caracteres'
heroku config:set JWT_EXPIRATION_MS='86400000'
heroku config:set JWT_ISSUER='bloodmatch-api'
```

## 4. Commit e push
```bash
git add .
git commit -m "feat: add jwt security and heroku deploy setup"
```

Se o remoto do Heroku ainda nao existir:
```bash
heroku login
heroku git:remote -a <nome-da-app>
```

## 5. Deploy
```bash
git push heroku main
```

Se seu branch local for `master`, use:
```bash
git push heroku master:main
```

## 6. Verificacao
- Health básico: abrir `https://<app>.herokuapp.com`
- Testar login: `POST /auth/login`
- Testar endpoint protegido sem token (deve retornar 401)
- Validar que o login retorna `accessToken`, `tokenType`, `expiresIn`, `roles` e `partyId`

## 7. Boas praticas de segredo
- Nunca commitar `.env`
- Usar `.env.example` apenas com placeholders
- Rotacionar `JWT_SECRET` ao trocar de ambiente
- Manter `JWT_SECRET` fora do `application.properties` em valores fixos
- Preferir config vars do Heroku para tudo que for sensivel

## 8. Observacoes sobre o Heroku
- O app usa `PORT` automaticamente via `server.port=${PORT:8080}`
- O `Procfile` esta configurado para subir o jar gerado no build
- O projeto usa Java 17 via `system.properties`
- Se `JWT_SECRET` nao estiver configurado, o app falha no startup por design
