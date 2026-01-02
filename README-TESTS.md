
# Guide d'exécution des tests

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
