# BackEnd Ca&Cá — Estoque

API REST para gestão de estoque de semijoias, feita com **Spring Boot 4** e **Java 21**. Cobrimos cadastro de joias, movimentações de estoque (entrada/saída/reentrada), revendedoras e maletas, dashboards e notificações via Brevo.

## Stack

| Camada | Tecnologia |
| --- | --- |
| Framework | Spring Boot 4.0.5 (Spring MVC, Spring Security 7, Spring Data JPA) |
| Linguagem | Java 21 |
| Build | Maven (módulo único: `estoque/`) |
| Banco | MySQL (`ddl-auto=update`, Flyway com migrações Java) |
| Auth | JWT stateless (JJWT 0.12.6), cookie HttpOnly `authToken` ou header `Authorization: Bearer` |
| Storage | AWS S3 (SDK v2) via Strategy |
| Filas | RabbitMQ (produção/consumo de notificações) |
| HTTP client | Spring Cloud OpenFeign (Brevo, ViaCEP) |
| Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Testes | JUnit 5 + Mockito + MockMvc + H2 + JaCoCo (gate de 70%) |

## Estrutura do repositório

```
BackEnd/
├── Dockerfile          # imagem da aplicação (jar na raiz)
├── ProjetoPi/          # opencollection.yml (não é código)
└── estoque/            # único módulo Maven — rode todo comando daqui
    ├── pom.xml
    ├── .env.example
    └── src/
        ├── main/java/com/caeca/estoque/
        │   ├── controller/    # endpoints REST
        │   ├── service/       # regras de negócio (Strategy em movimentacao/ e arquivo/)
        │   ├── database/      # model (entidades) + repository
        │   ├── dto/           # DTOs e Mappers manuais (XMapper)
        │   ├── config/        # Security, JWT, S3, CORS, Swagger
        │   ├── client/        # Feign (Brevo, ViaCEP)
        │   ├── producer/      # RabbitMQ
        │   ├── consumer/      # CrmNotificadorConsumer
        │   └── exception/     # GlobalExceptionHandler
        └── test/java/         # 38 classes de teste
```

## Pré-requisitos

- Java 21 e Maven 3.8+
- MySQL rodando em `localhost:3306` (banco `caeca`, usuário `root` / senha `root`)
- RabbitMQ em `localhost:5672` (guest/guest) — necessário para notificações

## Variáveis de ambiente

O projeto **não lê `.env` automaticamente** — exporte no shell ou configure no run config da IDE. Copie `estoque/.env.example` como referência.

| Variável | Obrigatória | Padrão | Descrição |
| --- | --- | --- | --- |
| `JWT_SECRET` | **Sim** | — | Segredo Base64 para assinar JWT. Gere com `openssl rand -base64 48` |
| `BREVO_API_KEY` | **Sim** | — | Chave da API Brevo (e-mails/CRM) |
| `JWT_VALIDITY` | Não | `1800` | Validade do token em segundos |
| `APP_STORAGE_TYPE` | Não | `s3` | Tipo de storage de arquivos |
| `APP_STORAGE_S3_BUCKET` | Não | `bucket-fotos-caeca` | Bucket S3 |
| `APP_STORAGE_S3_REGION` | Não | `us-east-1` | Região S3 |
| `APP_MALETA_CRON` | Não | `* * * * * *` | Cron das rotinas de maleta |
| `APP_ROTINAS_CRON` | Não | `0 0 8 * * *` | Cron das rotinas diárias |
| `APP_ESTOQUE_MENSAL_CRON` | Não | `0 0 9 1 * *` | Cron do relatório mensal |

> A aplicação **não sobe** sem `JWT_SECRET` e `BREVO_API_KEY`.

## Rodando localmente

```bash
# 1. Suba MySQL e RabbitMQ (docker ou instalação local)

# 2. Exporte as variáveis obrigatórias
export JWT_SECRET=$(openssl rand -base64 48)
export BREVO_API_KEY=sua-chave

# 3. Suba a aplicação (sempre de dentro de estoque/)
cd estoque
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. Não há seed de dados: o schema é gerado pelas entidades e é preciso inserir um usuário diretamente no MySQL para obter o primeiro token.

### Docker

Há um `Dockerfile` na raiz (base `eclipse-temurin:21-jre-alpine`, porta 8080). Não existe `docker-compose`.

```bash
cd estoque && mvn -DskipTests package
docker build -t caeca-estoque .
docker run -p 8080:8080 caeca-estoque
```

## Testes e qualidade

Todos os comandos rodam de dentro de `estoque/`:

| Ação | Comando |
| --- | --- |
| Testes | `mvn test` |
| Suíte + cobertura (gate) | `mvn verify` |
| Teste único | `mvn test -Dtest=JoiaServiceTest` |
| Pular JaCoCo | `mvn test -Djacoco.skip=true` |

- `mvn verify` **falha se branch coverage < 70%** (JaCoCo exclui `dto/`, `database/model/`, `config/`).
- Suíte rápida (~1,5 min, ~200 testes); roda offline com `-o` após o primeiro build.
- Convenção de testes: `@Nested` por método e `@DisplayName` em português começando com "Deve ...".

## API

Rotas principais (todas exigem autenticação, exceto login/logout e Swagger):

| Recurso | Base path | Observações |
| --- | --- | --- |
| Usuários | `/usuarios` | `POST /login`, `POST /logout`, `GET /me`; criação exige `ROLE_ADMIN` |
| Joias | `/joias` | CRUD + filtros (nome, categoria, material, banho, status, período) |
| Movimentações | `/movimentacoes` | entrada/saída/reentrada (Strategy), upload opcional de arquivo |
| Vendas / maletas | `/vendas` | movimentações com revendedoras |
| Revendedoras | `/revendedoras` | CRUD + notificações e rotinas |
| Fornecedores | `/fornecedores` | CRUD |
| Materiais | `/materiais` | CRUD |
| Categorias | `/categorias` | CRUD |
| Banhos | `/banhos` | CRUD |
| Motivos / tipos de movimentação | `/motivos-movimentacao`, `/tipos-movimentacao` | |
| Status de maleta | `/status-maletas` | |
| Endereços | `/enderecos` | ViaCEP |
| Arquivos | `/arquivos` | upload/download em S3 |
| Dashboard | `/dashboard` | KPIs e entrada x saída |

- **Paginação:** `Pageable` com `size=10` por padrão.
- **Auth:** cookie HttpOnly `authToken` (prioritário) ou header `Authorization: Bearer <token>`.
- **Documentação:** Swagger UI em `/swagger-ui/**`.
- **CORS:** origens fixas `localhost:5173` e `localhost:3000` (ajustar em `SecurityConfiguracao`).
- **Rotas públicas:** apenas as listadas em `URLS_PERMITIDAS` em `config/SecurityConfiguracao.java`.
- **Erros:** exceções de domínio (`NotFoundException`, `BadRequestException`, `ConflictException`, `QtdInsuficienteException`) tratadas pelo `GlobalExceptionHandler`.

## Arquitetura

- Fluxo `controller → service → database/{model,repository}`.
- Mapeamento **manual** com classes `XMapper` (`toEntity`, `toResponse`, `of`) — sem MapStruct.
- DTOs: padrão `XRequestDto`/`XResponseDto`; domínios mais novos (joia, revendedora) usam `EntradaDto`/`SaidaDto`/`AtualizacaoDto`.
- Strategy em `MovimentacaoService.cadastrar` (Entrada/Saída/Reentrada) e em `ArquivoStrategy` (S3).
- Eventos: `RevendedoraService` publica `NotificacaoEventoDto` → `CrmNotificadorConsumer` (`@Async`) → Brevo via Feign.

## Branches e commits

- Branches `feat/...`, `refactor/...`, `chore/...` a partir de `main`, com PR para `main`.
- Conventional Commits em português: `feat: adiciona paginação com tamanho 10`.
