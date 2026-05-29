# Deploy Checklist: spring-boot-starter-with-claude

Update this file as the project evolves. Run `/deploy-check` in any session to verify each item interactively.

---

## Pre-Deploy

### Code
- [ ] All tests passing (`./gradlew test` — BUILD SUCCESSFUL)
- [ ] Branch up to date with `main` (no merge conflicts)
- [ ] No debug or WIP commits in history

### Secrets & Configuration
- [ ] `.env` configured in the target environment with all required variables:
  - `JWT_SECRET` (base64-encoded, minimum 32 bytes)
  - `APP_USERNAME`
  - `APP_PASSWORD`
  - `APP_API_KEY`
- [ ] `JWT_SECRET` is unique per environment (do not reuse dev value in prod)
- [ ] `.env` is **not** committed to the repository (verify `.gitignore`)
- [ ] On Railway: variables set in the project's **Variables** tab (not via `.env` file)

### Build
- [ ] Production JAR generated: `./gradlew bootJar`
- [ ] JAR name matches `Procfile`: `spring-boot-starter-with-claude-0.0.1-SNAPSHOT.jar`
- [ ] App starts cleanly: `java -jar build/libs/spring-boot-starter-with-claude-0.0.1-SNAPSHOT.jar`

---

## Deploy

- [ ] Deployed to target environment
- [ ] `POST /auth/login` with valid credentials → 200 + token in response body
- [ ] `GET /hello-world` with `Authorization: Bearer <token>` → 200
- [ ] `GET /hello-world` with `X-Api-Key: <key>` → 200
- [ ] `GET /hello-world` without any credential → **401** (not 403, not 500)
- [ ] `POST /auth/login` with wrong credentials → 401 `{"error": "Invalid username or password"}`
- [ ] `POST /auth/login` with blank fields → 400 `{"error": "Validation failed", "details": [...]}`

---

## Post-Deploy

- [ ] Startup logs clean — no `BeanCreationException` or `Could not resolve placeholder` errors
- [ ] PR merged and feature branch deleted
- [ ] Postman collection `baseUrl` variable updated to the production URL

---

## Rollback Triggers

Roll back immediately if any of the following occur:

| Symptom | Likely Cause |
|---|---|
| Protected endpoint returns 500 instead of 401 | Security filter misconfiguration |
| `POST /auth/login` returns 500 | `JWT_SECRET` missing or malformed in environment |
| Startup fails with `Could not resolve placeholder` | Missing environment variable (check all four) |

**Rollback procedure:** redeploy the previous JAR and restore the previous environment variables.
