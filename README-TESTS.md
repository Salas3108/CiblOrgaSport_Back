
# Guide d'exécution des tests

## CI GitHub Actions (V1)
- Declenchement automatique sur push vers main et pull request.
- Scope V1: build + tests unitaires Maven par microservice.
- Les services sont executes en parallel via une matrix GitHub Actions.
- Les rapports Surefire sont publies en artefacts a chaque run.

Workflow CI: `.github/workflows/ci.yml`

## Services couverts par la CI
- abonnement-service
- analytics-service
- auth-service
- billetterie
- event-service
- gateway
- geolocation-service
- incident-service
- lieu-service
- notifications-service
- participants-service
- resultats-service
- volunteer-service

## Prérequis
- Java 17+
- Maven 3.6+
- Dépendances : JUnit 5, Mockito, AssertJ

## Commandes rapides

### Lancer tous les tests
```bash
cd billetterie
mvn test
```

### Lancer une classe spécifique
```bash
mvn test -Dtest=TicketServiceImplTest
```

### Lancer un test spécifique
```bash
mvn test -Dtest=TicketServiceImplTest#findById_ShouldReturnTicketWhenExists
```

## Utilisation du script
```bash
# Rendre le script exécutable
chmod +x run-tests.sh

# Lancer tous les tests
./run-tests.sh

# Lancer avec rapport
./run-tests.sh --report
```

## Rapports de test
Les rapports sont générés dans `billetterie/target/site/surefire-report.html`

En CI, les rapports sont aussi disponibles en artefacts GitHub Actions:
- `surefire-<service>/target/surefire-reports/**`
- `surefire-<service>/target/site/surefire-report.html`
