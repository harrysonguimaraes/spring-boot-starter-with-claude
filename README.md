# spring-boot-starter-with-claude

A **Spring Boot starter project** with a pre-defined stack, scaffolding, and security configuration — built as a reference baseline so every new Spring API starts from a solid foundation rather than from scratch.

The project ships with JWT + API Key authentication, centralized exception handling, config split by concern, and a full Spock test suite covering unit, slice, and integration layers. The included `GET /hello-world` endpoint is intentionally minimal — it exists to exercise the security layer, not as business logic.

## Stack

- **Java 17** · **Spring Boot 3.2.5** · **Gradle**
- **Spring Web** — REST endpoint exposure
- **Spring Security** — JWT (human users) and API Key (service-to-service) authentication
- **JJWT 0.12.6** — JWT generation and validation
- **spring-dotenv** — secrets loaded from `.env`, never committed
- **spring-boot-starter-validation** — `@NotBlank` request validation
- **Spock Framework 2.4-M4** (Groovy 4) — BDD-style tests replacing JUnit entirely (`given / when / then`)
- Configuration via `application.yml` + `security.yml`

## Architecture

The project is organized into four layers:

- **`security/`** — `SecurityConfig`, `JwtUtil`, `JwtAuthenticationFilter`, `ApiKeyAuthenticationFilter`. Security is always wired up first; business controllers are protected by default.
- **`controller/`** — `AuthController` (`POST /auth/login`), the business `HelloWorldController`, and `GlobalExceptionHandler` (`@RestControllerAdvice`) for centralized error responses.
- **`service/`** — business logic decoupled from HTTP concerns.
- **`dto/`** — `LoginRequest`, `LoginResponse`, `ErrorResponse`, and response records as Java Records.

Config is split by concern: `application.yml` imports `security.yml`, and all credentials are resolved at runtime from environment variables via `.env`.

## Tests

Six Spock Specifications cover every layer:

| Spec | Type | What it tests |
|---|---|---|
| `HelloWorldServiceSpec` | Unit | Service logic, no Spring context |
| `HelloWorldControllerSpec` | `@WebMvcTest` slice | 401 returned without a valid credential |
| `AuthControllerSpec` | `@WebMvcTest` slice | Login flow: valid → 200, bad credentials → 401, blank fields → 400 |
| `JwtUtilSpec` | Unit | Token generation, subject extraction, tampered token rejection |
| `ApiKeyAuthenticationFilterSpec` | `@WebMvcTest` slice | Valid key → 200, invalid key → 401, missing key → 401 |
| `HelloWorldIntegrationSpec` | `@SpringBootTest` | Full end-to-end: login, extract token, call protected endpoint |

All tests pass with `./gradlew test`.

## Replicating the project

This project was built entirely with **Claude**. The prompt used to reproduce it from scratch is available at [`prompts/prompts.md`](./prompts/prompts.md) — use it in a new session to get the same structure, stack, security setup, and test coverage.
