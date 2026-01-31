# Resultat Service

Microservice de gestion des résultats d'épreuves avec saisie manuelle par les commissaires.

## Démarrage rapide

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

### Port
Port par défaut: **8085**

## Documentation
Voir [RESULTAT_API.md](RESULTAT_API.md) pour la documentation complète de l'API.

## Démarrage via scripts
```bash
# Démarrer tous les services
./scripts/start-all-services.sh

# Arrêter tous les services
./scripts/stop-all-services.sh
```

## Configuration
Voir `src/main/resources/application.properties` pour les paramètres:
- Port serveur
- Connexion PostgreSQL
- Clé secrète JWT
- URL du service d'authentification
