# CiblOrgaSport Backend 

## Architecture Overview

**CiblOrgaSport** is a microservices-based sports organization platform built with **Spring Boot 3.2** and **Java 17**. The system uses a gateway pattern with Spring Cloud Gateway routing to multiple services, JWT authentication via JJWT (v0.11.5), and PostgreSQL (v16) for persistence.

### Service Topology

- **Gateway** (port 8080): Spring Cloud Gateway - entry point, security filter, JWT validation
- **Auth Service** (port 8081): User authentication, login/register, JWT issuance, document uploads
- **Event Service** (port 8082): Events, competitions, epreuves (competitions), locations (lieux)
- **Billetterie** (port 8083): Ticket management with inter-service auth validation via `AuthServiceClient`
- **Abonnement Service** (port 8081): User subscriptions
- **Incident Service** (port 8084): Incident reporting and location management

All services communicate via **RestTemplate** (not Feign). The gateway enforces JWT validation; services accept calls from billetterie to auth-service via HTTP client calls.

## Critical Workflows

### Build & Compilation
```bash
# Maven build (each service is a separate module)
cd <service-directory>
mvn clean compile          # Compile only
mvn clean package          # Build JAR to target/
mvn clean install          # Build & install to local repo

# Service building
mvn spring-boot:build-image  # Build Docker image (if configured)
```

### Running Services
All services are Spring Boot JARs running on their configured ports. Start via:
```bash
# Docker approach (preferred for local dev)
docker-compose up -d postgres  # Start DB
docker-compose build billetterie
docker-compose up -d billetterie

# Direct JVM (for debugging)
cd <service> && mvn spring-boot:run
# Or: java -jar target/*-SNAPSHOT.jar
```

Startup scripts in `scripts/`:
- `start-all-services.sh` - Starts all services, auto-detects ports from application.properties/yml
- `stop-all-services.sh` - Kills all services
- Services write PID files to `logs/<service>.pid` for process management

### Testing
- **Unit tests**: `src/test/java/**/*Test.java` (JUnit 5)
- **Integration tests**: Tests requiring running services
- Run via: `mvn test` or `mvn verify`
- See `AUTH_TESTS.md` and `README-TESTS.md` for test documentation

### Database
PostgreSQL 16 runs in Docker. Health checks ensure readiness before services start:
```bash
docker-compose up -d postgres
# Wait for "service_healthy" condition (20s startup + retries)
```
Database: `glop`, user: `admin`, password: `password`
PgAdmin at http://localhost:8082 (credentials: admin@admin.com / password)

## Key Architectural Patterns

### JWT & Security
- **Token Format**: JJWT library (0.11.5), tokens signed with a secret
- **Gateway Auth**: `security/` filters in gateway validate JWT headers before routing
- **Service Auth**: Services expect valid JWT in `Authorization: Bearer <token>` header
- **Roles**: Encoded in token claims (e.g., `ROLE_ADMIN`, `ROLE_USER`)
- **Enforcement**: Spring Security annotations (`@PreAuthorize("hasRole('ADMIN')")`) in controllers

**Example pattern**: Auth service issues token → billetterie calls auth-service via `AuthServiceClient` to fetch user details for validation.

### Inter-Service Communication
Services call each other via **RestTemplate** with HTTP:
```java
// Pattern from AuthServiceClient.java
RestTemplate restTemplate = new RestTemplate();
String url = authServiceUrl + "/auth/user/username/" + username;
ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
```
- Service URLs injected via `@Value("${service-name.url:http://localhost:PORT}")`
- Fallback to localhost defaults for local development
- No circuit breaker pattern yet (explicit try-catch exception handling)
- Methods should check response status: `response.getStatusCode().is2xxSuccessful()`

### Data Structure Conventions
Services follow standard Spring patterns:
- **Controller layer**: `@RestController`, handle HTTP requests, validate JWT
- **Service layer**: Business logic, database transactions
- **Repository layer**: Spring Data JPA (`extends JpaRepository<Entity, ID>`)
- **Entity layer**: `@Entity` classes with `@Id` primary keys, mapped to database
- **DTO layer**: Data transfer objects in `dto/` folders for API responses

### Endpoint Authorization Patterns
Endpoints follow consistent authorization using Spring Security:
- **Public endpoints**: `/auth/login`, `/auth/register` (no `@PreAuthorize`)
- **Authenticated endpoints**: Most endpoints require valid JWT
- **Admin-only endpoints**: `@PreAuthorize("hasRole('ADMIN')")` (e.g., `/admin/events`, `/auth/admin/validate-athlete`)
- **Owner/Author validation**: Service layer checks `userId` matches token claims or is admin

**Example**: Billetterie endpoints require auth; PUT/DELETE require owner or admin role.

## Service-Specific Conventions

### Adding New Endpoints
1. Create controller class in `src/main/java/com/ciblorgasport/<service>/controller/`
2. Add `@RestController` with path prefix (e.g., `@RequestMapping("/api/items")`)
3. Use `@GetMapping`, `@PostMapping`, etc. with Spring Security annotations
4. DTOs in `src/main/java/com/ciblorgasport/<service>/dto/`
5. Services in `src/main/java/com/ciblorgasport/<service>/service/`

### Calling Other Services
```java
// In billetterie, calling auth-service:
@Component
public class AuthServiceClient {
    @Value("${auth-service.url:http://localhost:8081}")
    private String authServiceUrl;
    
    public Map fetchSpectatorById(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = authServiceUrl + "/auth/user/" + id;
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(url, Map.class);
            if (resp.getStatusCode().is2xxSuccessful()) return resp.getBody();
        } catch (Exception ignored) {}
        return null;
    }
}
```
Always wrap in try-catch and return null/empty on failure—no service should hard-fail on inter-service communication issues.

## Configuration & Environment

### Key Configuration Files
- `src/main/resources/application.properties` or `application.yml` - Service config (port, database, service URLs)
- `docker-compose.yml` - PostgreSQL, PgAdmin, and service container definitions
- Postman collections in `postman/` for testing endpoints

### Critical Property Examples
```properties
server.port=8083                          # Service port
spring.datasource.url=jdbc:postgresql://postgres:5432/glop
spring.jpa.hibernate.ddl-auto=update      # Auto-create tables
auth-service.url=http://localhost:8081    # Inter-service URL
```

### Docker Build Notes
After updating `pom.xml` or `Dockerfile`:
```bash
docker compose build <service-name> && docker compose up -d <service-name>
```
Services define `HEALTHCHECK` with `/actuator/health` or `/health` endpoints.

## Debugging & Common Issues

### Port Conflicts
Services auto-detect ports from `application.properties`. If start script fails, check:
- Ports 8080-8084 are free
- Look at `logs/<service>.pid` for process info

### Database Connection Issues
- Ensure PostgreSQL is running: `docker-compose ps`
- Check health: `docker-compose logs postgres`
- Verify connection string matches environment

### JWT Token Validation Failures
- Gateway validates token format and signature
- Check `Authorization: Bearer <token>` header is present
- Token must be issued by auth-service
- Common fix: Re-login to get fresh token

### Inter-Service Call Failures
- Verify target service is running on expected port
- Check service URL config (e.g., `auth-service.url`)
- Services should gracefully handle failures (return null, not throw)

## Code Quality & Standards

- **Language**: Java 17, Spring Boot 3.2
- **Build tool**: Maven (one `pom.xml` per service)
- **Package naming**: `com.ciblorgasport.<service>.<layer>` (e.g., `com.ciblorgasport.billetterie.client`)
- **Naming conventions**: PascalCase for classes, camelCase for methods/variables
- **Documentation**: French comments in some files; English preferred for new code
- **Logging**: Use SLF4J via Spring (injected logger)

## Quick Reference: Essential Files

| Path | Purpose |
|------|---------|
| [docker-compose.yml](docker-compose.yml) | PostgreSQL, PgAdmin, service definitions |
| [scripts/start-all-services.sh](scripts/start-all-services.sh) | Launch all services with auto-port detection |
| [billetterie/src/main/java/com/ciblorgasport/billetterie/client/AuthServiceClient.java](billetterie/src/main/java/com/ciblorgasport/billetterie/client/AuthServiceClient.java) | Example inter-service communication pattern |
| [gateway/src/main/java/com/ciblorgasport/gateway/security/](gateway/src/main/java/com/ciblorgasport/gateway/security/) | JWT filter implementation |
| [documentation_endpoints_microservices.txt](documentation_endpoints_microservices.txt) | Complete endpoint reference |

---

**Last updated**: January 2026  
**Stack**: Spring Boot 3.2, Java 17, PostgreSQL 16, Spring Cloud Gateway, JJWT 0.11.5
