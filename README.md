# FórumHub API

API REST para gestão de tópicos de um fórum (CRUD), com **autenticação JWT** (login por **email + senha**), validação e tratamento de erros.

## Stack

- **Java 21**, **Spring Boot 3**
- Spring Web, Spring Security, Spring Validation, Spring Data JPA
- **MySQL** + **Flyway** (migrations)
- **JWT** (Auth0 `java-jwt`)
- Jackson (com rejeição de campos desconhecidos)

---

## Começando

### Pré-requisitos
- Java 21
- Maven
- MySQL em execução

### Configuração

Crie `src/main/resources/application.properties` (ou ajuste o seu) com:

```properties
# Datasource
spring.datasource.url=jdbc:mysql://localhost:3306/forumhub?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&serverTimezone=UTC
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Flyway
spring.flyway.enabled=true

# JWT (padrão do card)
jwt.secret=${JWT_SECRET:coloque-uma-chave-aleatoria-bem-grande-com-32+caracteres}
jwt.expiration=3600
```

> **Dica:** Use um segredo **forte (≥ 32 chars)**. Em produção, passe `JWT_SECRET` por variável de ambiente.

### Rodando

```bash
# compilar
./mvnw clean package

# rodar
./mvnw spring-boot:run
# ou
java -jar target/forumhub-*.jar
```

> O **Flyway** cria as tabelas ao subir (migrations em `src/main/resources/db/migration`).

### Usuário seed (dev)

Na primeira subida, é criado:
- **email:** `admin@forumhub.dev`  
- **senha:** `123456`  
- **nome:** `Admin` (usado como `autor` do tópico)

---

## Modelo de Dados

### Tabela `usuarios`
- `id` (PK, auto)
- `nome` (varchar)
- `email` (varchar, **único**, login)
- `senha` (varchar, **BCrypt**)

### Tabela `topicos`
- `id` (PK, auto)
- `titulo` (varchar, obrigatório, ≤ 180)
- `mensagem` (text, obrigatório)
- `data_criacao` (datetime, obrigatório) — **definido no backend (GMT-3)**
- `status` (enum: `RESPONDIDO` | `NAO_RESPONDIDO`)
- `autor` (varchar) — **preenchido com o nome do usuário autenticado**
- `curso` (varchar, obrigatório)

---

## Autenticação (JWT)

### Login
`POST /login` *(também disponível em `/auth/login`)*

**Request**
```json
{
  "email": "admin@forumhub.dev",
  "senha": "123456"
}
```

**Response**
```json
{ "token": "<JWT>", "type": "Bearer" }
```

Use o token nas próximas requisições no header:
```
Authorization: Bearer <JWT>
```

---

## Endpoints

### Criar tópico
`POST /topicos` *(autenticado)*

**Body**
```json
{
  "titulo": "Dúvida sobre Spring",
  "mensagem": "Como configuro X?",
  "curso": "Java"
}
```

**Response (201)**
```json
{
  "id": 1,
  "titulo": "Dúvida sobre Spring",
  "mensagem": "Como configuro X?",
  "dataCriacao": "2025-08-19 14:30",
  "status": "NAO_RESPONDIDO",
  "autor": "Admin",
  "curso": "Java"
}
```

> Campos extras no JSON são **rejeitados** com 400 (mensagem informa o campo e os permitidos).

---

### Listar tópicos
`GET /topicos` *(autenticado)*

**Response (200)**
```json
[
  { "id": 1, "titulo": "Dúvida", "mensagem": "Como configuro X?", "dataCriacao": "2025-08-19 14:30" }
]
```

---

### Detalhar tópico
`GET /topicos/{id}` *(autenticado)*

**Response (200)**  
Mesmo formato do **criar**.

---

### Atualizar tópico
`PUT /topicos/{id}` *(autenticado)* — **só** `titulo` e `mensagem`.

**Body**
```json
{ "titulo": "Novo título", "mensagem": "Nova mensagem" }
```

**Response (200)**  
Mesmo formato do **criar**.

---

### Excluir tópico
`DELETE /topicos/{id}` *(autenticado)*

**Response (204)** sem body.

---

## Validações & Erros

- **400 Bad Request**
  - Bean Validation (campos obrigatórios/tamanho): lista `fields` com erros.
  - **Campos não permitidos** no JSON: mensagem `"Você não pode definir/alterar o campo 'X'"` + lista de campos permitidos.
- **401 Unauthorized**  
  Token ausente/inválido (JSON com `status=401`, `message="Token inválido ou ausente"`).
- **403 Forbidden**  
  Sem permissão (se aplicarmos roles no futuro).
- **404 Not Found**  
  Tópico não existe.
- **409 Conflict**  
  Tópico **duplicado** (`titulo` + `mensagem` já existem).

---

## Datas & Fuso

- `dataCriacao` é setada no `@PrePersist` com **GMT-3**.
- Formato no JSON: **`yyyy-MM-dd HH:mm`** (sem segundos).

---

## Estrutura (resumo)

```
br/com/forumhub
 ├─ controller
 │   ├─ AuthController.java            # /login
 │   └─ TopicoController.java          # /topicos
 │   └─ dto/ (LoginRequest, TokenResponse, Topico* DTOs)
 ├─ domain
 │   ├─ topico/ (Topico, StatusTopico, Repo, Service)
 │   └─ usuario/ (Usuario, UsuarioRepository)
 ├─ infra
 │   ├─ dev/DevDataConfig.java         # seed Admin
 │   ├─ exception/ApiExceptionHandler… # 400/404/409 + campos extras
 │   ├─ jackson/JacksonConfig.java     # FAIL_ON_UNKNOWN_PROPERTIES
 │   └─ security/
 │       ├─ SecurityConfig.java        # stateless, /login público
 │       ├─ SecurityFilter.java        # valida JWT por request
 │       ├─ TokenService.java          # gera/valida token (Auth0)
 │       ├─ JwtAuthEntryPoint.java     # 401 JSON
 │       └─ JwtAccessDeniedHandler.java# 403 JSON
resources/db/migration
 ├─ V1__create_table_usuarios.sql
 └─ V2__create_table_topicos.sql       # (ou ordem equivalente)
```

---

## Exemplos (curl)

```bash
# Login
curl -s -X POST http://localhost:8080/login \
 -H "Content-Type: application/json" \
 -d '{"email":"admin@forumhub.dev","senha":"123456"}'

# Criar tópico
curl -s -X POST http://localhost:8080/topicos \
 -H "Authorization: Bearer <TOKEN>" \
 -H "Content-Type: application/json" \
 -d '{"titulo":"Dúvida","mensagem":"Como X?","curso":"Java"}'
```

---

## Notas finais

- Em produção, **NUNCA** use segredos fracos ou no arquivo; prefira variáveis de ambiente/secret manager.
- Se quiser deixar **GET /topicos** público, é só ajustar as regras em `SecurityConfig`.
