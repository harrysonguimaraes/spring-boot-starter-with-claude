
Crie um novo projeto Spring Boot com as seguintes especificações:

---

## Estrutura e build

- Gradle + Java 17, usando as versões estáveis mais recentes de todas as dependências (Spring Boot, Spock, Groovy, plugins — pesquise antes de usar)
- Arquivo de configuração: `application.yml` (nunca `.properties`); ele deve importar `security.yml`
- Classe principal: `Main.java` (nunca `Application.java`)

---

## Dependências obrigatórias

Além das dependências web padrão, incluir:

- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `io.jsonwebtoken:jjwt-api:0.12.6` + `jjwt-impl` + `jjwt-jackson` (runtimeOnly para os dois últimos)
- `me.paulschwarz:spring-dotenv:4.0.0`
- `spring-security-test` (testImplementation)

---

## Componentes de negócio

- Um `@RestController` com endpoint `GET /hello-world` retornando `{"text": "hello world!"}` em JSON
- Um `@Service` injetado no controller, responsável pela lógica de negócio
- Um DTO/record para a resposta

> Regra: controllers sempre retornam o objeto diretamente — nunca envoltos em `ResponseEntity<T>`

---

## Pacote `security`

Criar o pacote `security` com os seguintes componentes:

### `SecurityConfig.java` (`@Configuration` + `@EnableWebSecurity`)
- Lê `app.security.username`, `app.security.password`, `app.security.api-key` via `@Value`
- `SecurityFilterChain` com: CSRF desabilitado, sessão `STATELESS`, `/auth/login` permitAll, demais rotas autenticadas
- `authenticationEntryPoint` explícito retornando 401
- Registra `JwtAuthenticationFilter` e `ApiKeyAuthenticationFilter` antes de `UsernamePasswordAuthenticationFilter`
- `ApiKeyAuthenticationFilter` **não é `@Component`** — instanciado via `@Bean` com a chave passada pelo construtor
- Beans: `UserDetailsService` (InMemory), `PasswordEncoder` (BCrypt), `AuthenticationManager`

### `JwtUtil.java` (`@Component`)
- `@Value` para `app.jwt.secret` e `app.jwt.expiration-ms`
- `@PostConstruct init()` que constrói e cacheia o `SecretKey` uma única vez
- Métodos: `generateToken(username)`, `extractClaims(token)` (público), `extractUsername(token)`, `isValid(token)`
- Usa JJWT 0.12.6: `Jwts.builder().subject()`, `Jwts.parser().verifyWith()`, `Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret))`

### `JwtAuthenticationFilter.java` (`@Component`, extends `OncePerRequestFilter`)
- Lê header `Authorization: Bearer <token>`
- **Um único parse por request** via `extractClaims()` em try-catch (`JwtException | IllegalArgumentException` ignorados)
- Seta `SecurityContextHolder` somente se username não-nulo e autenticação ainda ausente

### `ApiKeyAuthenticationFilter.java` (extends `OncePerRequestFilter`, **sem** `@Component`)
- Header: `X-Api-Key`
- Autentica como `"api-client"` com `ROLE_USER` se chave válida e contexto ainda vazio
- Sempre chama `filterChain.doFilter()` — não bloqueia sozinho

---

## `AuthController` (pacote `controller`)

- `POST /auth/login` com `@Valid @RequestBody LoginRequest`
- Autentica via `AuthenticationManager`, retorna `LoginResponse(token)`
- `LoginRequest`: record com `@NotBlank(message = "username is required")` e `@NotBlank(message = "password is required")`
- `LoginResponse`: record com `String token`

---

## `GlobalExceptionHandler` (pacote `controller`)

Anotado com `@RestControllerAdvice`. Handlers obrigatórios:

- `BadCredentialsException` → 401 `{"error": "Invalid username or password"}`
- `MethodArgumentNotValidException` → 400 `{"error": "Validation failed", "details": ["..."]}`

`ErrorResponse`: record em `dto` com `@JsonInclude(NON_NULL)` para omitir `details` quando nulo; construtor compacto `ErrorResponse(String error)` que chama `this(error, null)`

---

## Arquivos de configuração

### `application.yml`
```yaml
spring:
  application:
    name: <project-name>
  config:
    import: security.yml
```

### `security.yml`
```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    expiration-ms: 86400000
  security:
    username: ${APP_USERNAME}
    password: ${APP_PASSWORD}
    api-key: ${APP_API_KEY}
```

### `.env` (git-ignorado)
```
JWT_SECRET=<base64 de pelo menos 32 bytes>
APP_USERNAME=user
APP_PASSWORD=password123
APP_API_KEY=<uuid ou string aleatória>
```

---

## Testes — usar Spock (nunca JUnit)

- `src/test/groovy/`, classes estendem `Specification`, blocos `given/when/then`
- Specs Spring devem ter `@ExtendWith(SpringExtension)` explícito
- Usar `@SpringBean` (spock-spring), nunca `@MockBean`; stub com `>>`, nunca Mockito

### Specs obrigatórias

| Spec | Tipo | Observações |
|---|---|---|
| `<Name>ServiceSpec` | Unidade pura | Sem Spring, instância direta |
| `<Name>ControllerSpec` | `@WebMvcTest` | `@SpringBean JwtUtil = Stub()`, testa apenas 401 sem token |
| `AuthControllerSpec` | `@WebMvcTest(AuthController)` + `@Import(SecurityConfig)` + `@TestPropertySource` | Login válido → 200, credenciais erradas → 401, campos em branco → 400 |
| `JwtUtilSpec` | Unidade pura | Chama `jwtUtil.init()` no `setup()` para simular `@PostConstruct` |
| `ApiKeyAuthenticationFilterSpec` | `@WebMvcTest` + `@Import(SecurityConfig)` + `@TestPropertySource` | Chave válida → 200, inválida → 401, ausente → 401 |
| `<Name>IntegrationSpec` | `@SpringBootTest` + `@AutoConfigureMockMvc` | Faz login via POST, extrai token, usa em GET |

---

## Arquivos raiz

- `.gitignore` ignorando: `.claude/`, `.gradle/`, `.idea/`, `build/`, `.env`, `**/.DS_Store`
- `.env` criado e git-ignorado (nunca commitado)

---

## Critério de conclusão

Todos os testes devem passar com `./gradlew test` antes de encerrar.
