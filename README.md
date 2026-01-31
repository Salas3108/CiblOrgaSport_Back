# CiblOrgaSport – Backend

Backend du projet CiblOrgaSport. Ce document décrit l’installation, l’exécution, l’architecture, l’API et l’avancement.

## Aperçu
- Objectif: [décrire brièvement le but du backend]
- Stack: [Node/Express | NestJS | Spring Boot | autre] + [DB: Postgres/MySQL/Mongo] + [ORM: Prisma/TypeORM/Mongoose] + [Auth: JWT/OAuth]
- Statut: actif

## Prérequis
- Runtime: [Node >= xx | JDK xx | autre]
- Package manager: [npm | pnpm | yarn]
- Base de données: [Postgres/MySQL/Mongo], accessible via réseau local
- Outils:
  - Docker (optionnel)
  - Make (optionnel)
  - Git

## Installation
```bash
git clone <repo-url>
cd CiblOrgaSport_Back
[ npm install | pnpm install | yarn ]
```

## Configuration
Créer un fichier .env à la racine:
```
# Exemple
PORT=3000
NODE_ENV=development
DATABASE_URL=postgres://user:pass@localhost:5432/ciblorgasport
JWT_SECRET=change-me
# ...autres variables
```

## Démarrage
```bash
# Dev
npm run dev
# Prod
npm run build && npm run start
```

## 🚀 Démarrage des Services

### Démarrage automatique de tous les services

Pour lancer tous les services CiblOrgaSport en une seule commande :

```bash
# Rendre le script exécutable (première fois uniquement)
chmod +x scripts/start-all-services.sh

# Lancer tous les services
./scripts/start-all-services.sh
```

### Arrêt de tous les services

```bash
# Rendre le script exécutable (première fois uniquement)
chmod +x scripts/stop-all-services.sh

# Arrêter tous les services
./scripts/stop-all-services.sh
```

### Surveillance des logs

Les logs de chaque service sont automatiquement sauvegardés dans le dossier `logs/` :

```bash
# Voir les logs en temps réel
tail -f logs/api-server.log
tail -f logs/auth-service.log
tail -f logs/notification-service.log
```

### Démarrage manuel des services individuels

Si vous préférez démarrer les services individuellement :

```bash
# Base de données
npm run db:start

# Serveur API principal
npm run start:dev

# Service d'authentification
npm run auth:start

# Service de notifications
npm run notifications:start

# Service de téléchargement
npm run upload:start

# Tâches en arrière-plan
npm run jobs:start
```

## Scripts utiles
- npm run dev: démarrage en mode développement
- npm run build: build de production
- npm run start: lancement en production
- npm run test: tests unitaires
- npm run test:e2e: tests end-to-end
- npm run lint: linting
- npm run format: formattage

(Ajuster selon vos scripts réels.)

## Architecture (suggestion)
```
/src
  /modules
    /users
    /auth
    /teams
    /events
  /config
  /database
  /middlewares
  /shared
```
- Principes: séparation par domaine, services testables, contrôleurs fins, validations centralisées.

## API (aperçu)
- Base URL: http://localhost:${PORT}/api
- Auth: Bearer JWT
- Endpoints (exemples):
  - POST /auth/login
  - POST /auth/register
  - GET /users/me (auth)
  - CRUD /teams, /events
- Exemple:
```bash
curl -X GET "http://localhost:3000/api/health" -H "Accept: application/json"
```

## Base de données & Migrations
- ORM/ODM: [Prisma/TypeORM/Mongoose]
- Commandes:
```bash
# Prisma (exemple)
npx prisma migrate dev
npx prisma studio
```
(Ajuster selon l’ORM choisi.)

## Tests
- Unitaires: [Jest | Vitest | JUnit]
- E2E: [Supertest | Playwright | Postman/Newman]
```bash
npm run test
npm run test:e2e
```

## Tests API avec Postman
Importer les fichiers de collection et d’environnement situés dans `postman/` :
- `postman/CiblOrgaSport.postman_collection.json`
- `postman/CiblOrgaSport.postman_environment.json`

Étapes:
1. Ouvrir Postman, importer la collection et l’environnement.
2. Démarrer les services (voir section Démarrage).
3. Obtenir un token JWT via `auth-service /auth/login` ou `monolith /api/auth/login`.
4. Mettre à jour la variable `token` dans l’environnement.
5. Exécuter les requêtes regroupées par microservice.

Remarques:
- Les URLs de base sont configurables via l’environnement (`*_base`).
- Certaines requêtes nécessitent un rôle (ADMIN/ATHLETE).
- Les endpoints multipart (upload-documents) attendent des fichiers en form-data.

## Qualité de code
- Lint: ESLint/TSLint
- Format: Prettier
- Hooks Git: Husky + lint-staged (optionnel)

## Sécurité
- Variables sensibles via .env
- Mots de passe hashés (bcrypt/argon2)
- En-têtes sécurisés (helmet)
- Validation d’entrée (class-validator/zod)
- Ratelimiting & CORS

## Observabilité
- Logs: [pino/winston], niveaux par environnement
- Monitoring: [OpenTelemetry/Prometheus]
- Healthcheck: /api/health

## CI/CD & Déploiement
- CI: [GitHub Actions/GitLab CI] (lint, test, build)
- Déploiement: [Docker/K8s/VM/PAAS]
- Stratégie: [blue-green/rolling]

## Docker (optionnel)
```yaml
# docker-compose.yml (exemple)
version: '3.9'
services:
  api:
    build: .
    env_file: .env
    ports:
      - "3000:3000"
    depends_on:
      - db
  db:
    image: postgres:16
    environment:
      POSTGRES_PASSWORD: password
      POSTGRES_DB: ciblorgasport
    ports:
      - "5432:5432"
```

## Microservice Billetterie (Spring Boot)
- Port: 8081
- Objectif: permettre à Suzanne (spectatrice) de consulter et stocker ses billets.
- Endpoints:
  - GET /api/tickets?spectatorId={id} — liste des billets du spectateur
  - POST /api/tickets — crée et stocke un billet
    - body: { spectatorId, eventId, seat? }

### Démarrage
```bash
cd billetterie
./mvnw spring-boot:run
# Astuces:
# - Utiliser le wrapper Maven (./mvnw) pour éviter les écarts de version
# - Pour logs détaillés: ./mvnw -X spring-boot:run
# - Pour stacktrace: ./mvnw -e spring-boot:run
```

### Config
Variables (via application.properties ou env):
- BILL_DB_URL, BILL_DB_USER, BILL_DB_PASS

### Dépannage (billetterie)
En cas d'échec "spring-boot-maven-plugin ... Process terminated with exit code: 1":
- Vérifier la version Java:
  ```bash
  java -version
  ./mvnw -v
  ```
  Utiliser une JDK compatible avec Spring Boot (Java 17+ recommandé).
- Nettoyer et recompiler:
  ```bash
  cd billetterie
  ./mvnw clean package
  ```
- Lancer avec logs détaillés pour identifier la cause:
  ```bash
  ./mvnw -X spring-boot:run
  ```
- Ports et configuration:
  - Assurer que le port 8081 est libre.
  - Vérifier application.yml et variables: BILL_DB_URL, BILL_DB_USER, BILL_DB_PASS.
  - Si la DB n’est pas accessible, tester une URL locale ou dockerisée.
- Dépendances et plugin:
  - Vérifier dans pom.xml la version du plugin spring-boot:
    - org.springframework.boot:spring-boot-maven-plugin aligné avec la version Spring Boot du projet.
- Tests/env:
  - Essayer sans tests au run (si applicable): `./mvnw spring-boot:run -DskipTests`
- Logs d’erreur courants:
  - Bean creation failure: vérifier les @Configuration/@ComponentScan.
  - Profile manquant: définir `SPRING_PROFILES_ACTIVE=dev` si nécessaire.

## Roadmap & Avancement
- Fondations
  - [x] Initialisation repo
  - [x] Config .env et healthcheck
  - [ ] Intégration ORM et migrations
- Authentification
  - [x] Modèle utilisateur
  - [x] Inscription/Connexion
  - [ ] JWT refresh/rotation
  - [ ] Rôles & permissions
- Domaines métier
  - [ ] Gestion équipes
  - [ ] Gestion événements
  - [ ] Inscriptions/participations
- Qualité
  - [ ] Tests unitaires > 70% coverage
  - [ ] E2E critiques
  - [ ] Lint + format + hooks Git
- Ops
  - [ ] Docker prêt prod
  - [ ] CI (lint/test/build)
  - [ ] Déploiement staging

Indicateurs (à compléter):
- Couverture tests: xx%
- Temps de build: xx s
- Uptime staging: xx%

## Contribution
- Branches: main (protégée), feat/*, fix/*
- PR: petites, testées, description claire, checklist
- Convention de commits: Conventional Commits

## Licence
[MIT/Apache-2.0/Propriétaire] – à préciser.

## Contacts
- Mainteneur: [nom] – [email]
- Tickets: utiliser Issues
