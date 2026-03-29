# CiblOrgaSport — Backend

Backend microservices de la plateforme de gestion d'événements sportifs.

- **GitHub :** [github.com/Salas3108/CiblOrgaSport_Back](https://github.com/Salas3108/CiblOrgaSport_Back)
- **API Production :** [http://137.74.133.131](http://137.74.133.131)
- **Branche déploiement :** [`deploy/ovh-production`](https://github.com/Salas3108/CiblOrgaSport_Back/tree/deploy/ovh-production)

---

## Stack technique

| Couche | Technologie |
| ------ | ----------- |
| Langage | Java 17 |
| Framework | Spring Boot 3.2 |
| Gateway | Spring Cloud Gateway |
| Base de données | PostgreSQL 16 |
| Messagerie | Apache Kafka (KRaft, sans Zookeeper) |
| Authentification | JWT (jjwt 0.11.5) |
| Build | Maven 3.9 |
| Conteneurisation | Docker + Docker Compose |
| Reverse proxy | Nginx |

---

## Microservices

| Service | Port | Rôle |
| ------- | ---- | ---- |
| gateway | 8080 | Routeur central (Spring Cloud Gateway) |
| auth-service | 8081 | Authentification et gestion des JWT |
| billetterie | 8083 | Billets et réservations |
| event-service | 8084 | Événements, compétitions, épreuves |
| abonnement-service | 8085 | Abonnements |
| incident-service | 8086 | Déclaration et suivi d'incidents |
| participants-service | 8087 | Athlètes, commissaires, inscriptions |
| resultats-service | 8088 | Saisie et consultation des résultats |
| notifications-service | 8089 | Notifications (Kafka consumer) |
| lieu-service | 8090 | Sites et lieux sportifs |
| geolocation-service | 8091 | Géolocalisation |
| analytics-service | 8092 | Statistiques et tableaux de bord |
| volunteer-service | 8093 | Gestion des volontaires |

---

## Démarrage local

### Pré-requis

- Java 17+
- Maven 3.9+
- Docker Desktop

### Lancer l'infrastructure (PostgreSQL + Kafka)

```bash
docker compose up -d postgres kafka
```

### Lancer un service individuellement

```bash
cd auth-service
mvn spring-boot:run
```

### Lancer toute la stack en local

```bash
docker compose up -d
```

L'API est alors disponible sur `http://localhost:8080`.

---

## Tests avec Postman

Deux environnements sont disponibles dans `postman/postman/environments/` :

| Fichier | Utilisation |
| ------- | ----------- |
| `CiblOrgaSport_Local.postman_environment.json` | Développement local (`localhost`) |
| `CiblOrgaSport_Production.postman_environment.json` | Production (`http://137.74.133.131`) |

**Import dans Postman :**

1. File → Import
2. Sélectionner le fichier d'environnement souhaité
3. Importer également la collection `postman/postman/collections/CiblOrgaSport-Microservices.postman_collection.json`
4. Sélectionner l'environnement voulu dans le menu déroulant en haut à droite

---

## Documentation

| Document | Description |
| -------- | ----------- |
| [docs/PORTS.md](docs/PORTS.md) | Cartographie complète des ports internes et externes |
| [docs/DEPLOYMENT_STEPS.md](docs/DEPLOYMENT_STEPS.md) | Référence rapide déploiement (pour développeurs) |
| [docs/DEPLOYMENT_GUIDE_CLIENT.md](docs/DEPLOYMENT_GUIDE_CLIENT.md) | Guide complet déploiement (pour administrateurs et clients) |
| [deploy/README.md](deploy/README.md) | Scripts de déploiement OVH |

---

## Architecture de production

```text
Internet :80
    |
  Nginx
    |
  Spring Cloud Gateway :8080          vm-services (137.74.133.131)
    |
  +-- auth-service :8081
  +-- billetterie :8083
  +-- event-service :8084
  +-- abonnement-service :8085
  +-- incident-service :8086
  +-- participants-service :8087
  +-- resultats-service :8088
  +-- notifications-service :8089
  +-- lieu-service :8090
  +-- geolocation-service :8091
  +-- analytics-service :8092
  +-- volunteer-service :8093
  +-- PostgreSQL :5432 (interne Docker)
  +-- Kafka :9092 (interne Docker)
```

Seul le port 80 est exposé sur Internet. Tous les autres ports communiquent via le réseau Docker interne `prod-net`.

---

## Branches

| Branche | Rôle |
| ------- | ---- |
| `main` | Code source stable |
| `deploy/ovh-production` | Configuration de déploiement OVH (Dockerfiles, compose, scripts, docs) |
| `resultat_salim_V2` | Développement en cours |

---

## Sécurité

- Authentification par **JWT** sur tous les endpoints (sauf `/auth/api/auth/signin` et `/auth/api/auth/signup`)
- Rôles : `ADMIN`, `ATHLETE`, `COMMISSAIRE`, `VOLONTAIRE`
- Le token JWT est injecté dans les headers `X-User-*` par le gateway et transmis aux microservices
- Secrets gérés via `.env.prod` (jamais commité dans Git)

---

## Contribution

- Convention de commits : [Conventional Commits](https://www.conventionalcommits.org/)
- Branches : `feat/*`, `fix/*`, `deploy/*`
- Pull Requests vers `main`

---

## Contacts

- Repository : [github.com/Salas3108/CiblOrgaSport_Back](https://github.com/Salas3108/CiblOrgaSport_Back)
- Issues : [github.com/Salas3108/CiblOrgaSport_Back/issues](https://github.com/Salas3108/CiblOrgaSport_Back/issues)
