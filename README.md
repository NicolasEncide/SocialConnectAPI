# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot · Spring Data JPA · Flyway · PostgreSQL

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- PostgreSQL (local na porta 5432 ou via Docker)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### 1. Iniciar o Banco de Dados (PostgreSQL)

Caso use Docker:
```bash
docker compose up -d
```

Ou certifique-se de que o seu serviço local do PostgreSQL está rodando com uma base de dados chamada `socialconnect`.
> As credenciais padrão configuradas em `application.properties` são:
> - **Host**: `localhost:5432`
> - **Database**: `socialconnect`
> - **User**: `postgres`
> - **Password**: `postgres`
>
> Você também pode sobrescrevê-las usando as variáveis de ambiente `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD`.

### 2. Compilar e Rodar a Aplicação

```bash
# Compile o projeto
./mvnw clean compile

# Rode a aplicação (as migrações do Flyway executarão automaticamente)
./mvnw spring-boot:run
```

A documentação Swagger estará disponível em:
`http://localhost:8080/swagger-ui.html`