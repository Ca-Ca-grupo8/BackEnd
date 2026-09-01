# AGENTS.md

Inventory/stock API ("Estoque", jewelry business) for the Caecá project. Single Spring Boot 4.0.5 module under `estoque/` (Java 21, Maven, Lombok). Everything else at the repo root (`ProjetoPi/`) is just a Bruno/OpenCollection API collection scaffold — ignore it.

## Build & test

- No `mvnw` script is committed. Use Maven (`mvn`) or IntelliJ's bundled Maven. Java 21 is required (`<java.version>21</java.version>`).
- `mvn test -Dtest=JoiaServiceTest` — run one test class. Almost all tests are pure Mockito and need **no database**.
- The only exception is `EstoqueApplicationTests` (`@SpringBootTest` contextLoads): it boots the full app and needs MySQL at `localhost:3306` (schema `caeca` auto-created, user/pass `root`/`root`). If MySQL is down, run focused tests with `-Dtest=...` to skip it.
- `mvn verify` additionally runs the JaCoCo gate: **branch coverage ≥ 0.70 or the build fails**. Excluded from coverage: `dto/**`, `database/model/**`, `config/**`.

## Spring Boot 4 gotchas (this repo compiles against Boot 4 / Jackson 3 / Security 7)

Do not "fix" these to the older Boot 3.x equivalents — they are intentional and current:

- Starters are named `spring-boot-starter-webmvc` (not `-web`); test starters carry a `-test` suffix (`spring-boot-starter-webmvc-test`, `spring-boot-starter-data-jpa-test`).
- Controllers/slice tests use `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest` / `.AutoConfigureMockMvc` (not `org.springframework.boot.test.autoconfigure.web.servlet.*`).
- ObjectMapper for JSON in tests is Jackson 3: `tools.jackson.databind.ObjectMapper`.
- Bean mocking in tests: `@MockitoBean` from `org.springframework.test.context.bean.override.mockito.MockitoBean` (not `@MockBean`).
- Spring Security 7 removed `AntPathRequestMatcher`; use `requestMatchers(String...)` (see `SecurityConfiguracao`).
- jjwt 0.12.x API (`io.jsonwebtoken`), not the older 0.9 builders.

## Architecture & conventions

- Package-by-feature layout: `controller/<recurso>`, `service/<recurso>`, `database/model/<recurso>`, `database/repository/<recurso>`, `dto/<recurso>` with a sibling `XMapper` class (`dto/movimentacao/MovimentacaoMapper` etc.). New entities/repos/services land in a matching folder.
- **Movimentação uses a Strategy pattern** (`strategy/movimentacao/`): each type (`EntradaStrategy`, `SaidaStrategy`, `ReentradaStrategy`) is a `@Component` implementing `MovimentacaoStrategy`, selected via `suporta(MotivoMovimentacao)` and dispatched by `MovimentacaoContext`. Adding a movement type = new strategy class (must be a Spring bean), not a new `if`.
- File/photo storage is behind `ArquivoStrategy` (`strategy/arquivo/`); the S3 implementation is selected via `APP_STORAGE_TYPE` env var (default `s3`, bucket/region configurable). Tests mock this interface.
- CEP lookup goes through an external `ViaCepClient` (Brazilian zip-code service).
- Persistence: MySQL, `ddl-auto=update` — the schema is auto-managed, **no migration tool, no `data.sql`**. `spring.sql.init.mode=always` in `application.properties` is inert (no init scripts exist).
- Auth: stateless JWT. `POST /usuarios` (registration) and `POST /usuarios/login` are public; login returns the token as an HttpOnly cookie (`authToken`) plus a session DTO. All other endpoints require the JWT; `config/` holds the filter, provider, and entry point. Swagger UI: `/swagger-ui`.
- Controller tests bypass auth with `@AutoConfigureMockMvc(addFilters = false)`.

## Style

- All code comments, Javadocs, error messages, `@DisplayName`, and commit messages are written in **Brazilian Portuguese**. Match that.
- Tests use `@Nested` + `@DisplayName("...")` grouped by method name.
- No lint/formatter config exists; follow the existing formatting (tabs, ~4-space continuation).