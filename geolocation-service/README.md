# geolocation-service

Microservice de géolocalisation pour **CiblOrgaSport** — Championnats d'Europe de Natation 2026.

Ce service gère le suivi GPS en temps réel des athlètes ainsi que la gestion des Fan Zones.

---

## Sommaire

- [Stack technique](#stack-technique)
- [Démarrage rapide](#démarrage-rapide)
- [Configuration](#configuration)
- [Architecture](#architecture)
- [Modèle de données](#modèle-de-données)
- [Endpoints exposes](#endpoints-exposes)
- [API REST](#api-rest)
- [WebSocket temps réel](#websocket-temps-réel)
- [Sécurité](#sécurité)
- [Gestion des erreurs](#gestion-des-erreurs)
- [Tests](#tests)

---

## Stack technique

| Composant       | Technologie                         |
|-----------------|-------------------------------------|
| Framework       | Spring Boot 3.3.4                   |
| Langage         | Java 17                             |
| Base de données | PostgreSQL (prod) / H2 (tests)      |
| ORM             | Spring Data JPA / Hibernate         |
| Sécurité        | Spring Security + JWT (JJWT 0.11.5) |
| WebSocket       | Spring WebSocket / STOMP + SockJS   |
| Build           | Maven                               |
| Port            | **8091**                            |

---

## Démarrage rapide

### Prérequis

- Java 17+
- Maven 3.8+
- PostgreSQL accessible sur `localhost:5400`
- `auth-service` en cours d'exécution (pour la génération des tokens JWT)

### Lancer le service

```bash
cd geolocation-service
mvn spring-boot:run
```

Le service démarre sur **http://localhost:8091**.

### Lancer les tests

```bash
mvn test
```

Les tests utilisent une base H2 en mémoire (profil `test`). Aucune connexion PostgreSQL requise.

### Note — PostgreSQL au premier démarrage

Si la table `athlete_position` existe depuis une ancienne version du service (avec colonne `epreuve_id`), exécuter cette migration avant le démarrage :

```sql
ALTER TABLE athlete_position DROP COLUMN IF EXISTS epreuve_id;
```

---

## Configuration

### `application.properties`

| Propriété | Valeur | Description |
|-----------|--------|-------------|
| `server.port` | `8091` | Port HTTP du service |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5400/glop` | URL PostgreSQL |
| `spring.datasource.username` | `admin` | Utilisateur DB |
| `spring.datasource.password` | `password` | Mot de passe DB |
| `spring.jpa.hibernate.ddl-auto` | `update` | Mise à jour automatique du schéma |
| `ciblorgasport.app.jwtSecret` | `mySuperSecretKey...` | Clé HMAC-SHA256 partagée avec l'`auth-service` |

### `application-test.properties`

Utilisé automatiquement lors de l'exécution des tests (`mvn test`). Remplace PostgreSQL par une base H2 en mémoire.

---

## Architecture

```text
geolocation-service/
├── controller/
│   ├── AthletePositionController.java   # Routes /api/geo/athletes/**
│   └── FanZoneController.java           # Routes /api/geo/fanzones/**
├── service/
│   ├── AthletePositionService.java      # Logique positions + diffusion WebSocket
│   └── FanZoneBusinessService.java      # Logique fan zones + calcul Haversine
├── entity/
│   ├── AthletePosition.java             # Table athlete_position
│   ├── FanZone.java                     # Table fan_zone
│   ├── FanZoneService.java              # Table fan_zone_service
│   └── enums/TypeService.java           # Enum services disponibles
├── dto/
│   ├── PositionRequest.java             # Body POST position
│   ├── PositionResponse.java            # Réponse position
│   ├── WebSocketPositionMessage.java    # Message diffusé via STOMP
│   ├── FanZoneRequest.java              # Body création fan zone
│   ├── FanZoneResponse.java             # Réponse fan zone
│   └── ErrorResponse.java              # Format d'erreur uniforme
├── repository/
│   ├── AthletePositionRepository.java
│   └── FanZoneRepository.java
├── security/
│   ├── JwtUtils.java                    # Validation et lecture des claims JWT
│   └── AuthTokenFilter.java             # Filtre HTTP : extraction du token Bearer
├── config/
│   ├── SecurityConfig.java              # Règles Spring Security
│   └── WebSocketConfig.java             # Configuration STOMP / SockJS
└── exception/
    ├── GlobalExceptionHandler.java      # Gestionnaire d'erreurs global
    └── ResourceNotFoundException.java
```

---

## Modèle de données

### Table `athlete_position`

| Colonne      | Type             | Contrainte   | Description                     |
|--------------|------------------|--------------|---------------------------------|
| `id`         | BIGINT           | PK, auto-inc | Identifiant unique              |
| `athlete_id` | BIGINT           | NOT NULL     | ID de l'athlète (cross-service) |
| `latitude`   | DOUBLE PRECISION | NOT NULL     | Coordonnée GPS                  |
| `longitude`  | DOUBLE PRECISION | NOT NULL     | Coordonnée GPS                  |
| `timestamp`  | TIMESTAMP        | NOT NULL     | Horodatage de la position       |

Index : `idx_athlete_position_athlete_id` sur `athlete_id`.

### Table `fan_zone`

| Colonne        | Type    | Contrainte   | Description        |
|----------------|---------|--------------|--------------------|
| `id`           | BIGINT  | PK, auto-inc |                    |
| `nom`          | VARCHAR | NOT NULL     | Nom de la fan zone |
| `description`  | TEXT    | nullable     |                    |
| `latitude`     | DOUBLE  | NOT NULL     |                    |
| `longitude`    | DOUBLE  | NOT NULL     |                    |
| `capacite_max` | INTEGER | nullable     | Capacité d'accueil |
| `adresse`      | VARCHAR | nullable     |                    |

### Table `fan_zone_service`

| Colonne        | Type    | Contrainte      | Description                                          |
|----------------|---------|-----------------|------------------------------------------------------|
| `id`           | BIGINT  | PK, auto-inc    |                                                      |
| `fan_zone_id`  | BIGINT  | FK → `fan_zone` |                                                      |
| `type_service` | VARCHAR | NOT NULL        | `ECRAN_GEANT`, `RESTAURATION`, `BOUTIQUE`, `MEDICAL` |

---

## Endpoints exposes

Base URL : `http://localhost:8091`

### Positions des athlètes

| Méthode    | URL                                               | Rôle requis                        | Description                            |
|------------|---------------------------------------------------|------------------------------------|----------------------------------------|
| `POST`     | `/api/geo/athletes/{athleteId}/position`          | `ATHLETE` (son propre ID)          | Enregistre la position GPS + WS        |
| `GET`      | `/api/geo/athletes/{athleteId}/position`          | `COMMISSAIRE`, `ADMIN`             | Dernière position connue               |
| `GET`      | `/api/geo/athletes/{athleteId}/history`           | `COMMISSAIRE`, `ADMIN`             | Historique sur une période             |
| `DELETE`   | `/api/geo/athletes/{athleteId}/positions`         | `ADMIN`                            | Supprime toutes les positions (RGPD)   |

Paramètres query pour `/history` : `dateDebut` et `dateFin` au format ISO-8601 (`2026-03-07T00:00:00`).

### Fan Zones

| Méthode    | URL                                               | Rôle requis | Description                                        |
|------------|---------------------------------------------------|-------------|----------------------------------------------------|
| `POST`     | `/api/geo/fanzones`                               | `ADMIN`     | Crée une fan zone                                  |
| `GET`      | `/api/geo/fanzones`                               | PUBLIC      | Liste toutes les fan zones                         |
| `GET`      | `/api/geo/fanzones/nearby?lat=&lng=&rayon=`       | PUBLIC      | Fan zones dans un rayon (défaut 500 m), par distance |
| `DELETE`   | `/api/geo/fanzones/{fanzoneId}`                   | `ADMIN`     | Supprime une fan zone                              |

### WebSocket

| Endpoint              | Accès  | Description                                   |
|-----------------------|--------|-----------------------------------------------|
| `/ws/geo` (SockJS)    | PUBLIC | Point de connexion STOMP                      |
| `/topic/athletes/{athleteId}` | PUBLIC (subscribe) | Position live d'un athlète  |

---

## API REST

### Positions des athlètes

#### `POST /api/geo/athletes/{athleteId}/position`

Enregistre la position GPS d'un athlète et la diffuse immédiatement via WebSocket.

- **Rôle requis** : `ROLE_ATHLETE`
- **Contrainte** : le `userId` extrait du JWT doit correspondre à `{athleteId}` (un athlète ne peut enregistrer que sa propre position)

**Body :**

```json
{
  "latitude": 48.8566,
  "longitude": 2.3522
}
```

**Réponse 200 :**

```json
{
  "id": 42,
  "athleteId": 9,
  "latitude": 48.8566,
  "longitude": 2.3522,
  "timestamp": "2026-03-07T20:27:07.579985"
}
```

---

#### `GET /api/geo/athletes/{athleteId}/position`

Retourne la dernière position connue d'un athlète.

- **Rôle requis** : `ROLE_COMMISSAIRE` ou `ROLE_ADMIN`
- **Réponse 200** : `PositionResponse`
- **Réponse 404** : aucune position enregistrée pour cet athlète

---

#### `GET /api/geo/athletes/{athleteId}/history?dateDebut=&dateFin=`

Retourne l'historique des positions d'un athlète sur une période, trié par timestamp croissant.

- **Rôle requis** : `ROLE_COMMISSAIRE` ou `ROLE_ADMIN`
- **Paramètres** : `dateDebut` et `dateFin` au format ISO-8601 (`2026-03-07T00:00:00`)
- **Réponse 200** : `PositionResponse[]`

**Exemple :**

```http
GET /api/geo/athletes/9/history?dateDebut=2026-03-07T00:00:00&dateFin=2026-03-07T23:59:59
```

---

#### `DELETE /api/geo/athletes/{athleteId}/positions`

Supprime toutes les positions d'un athlète (conformité RGPD).

- **Rôle requis** : `ROLE_ADMIN`
- **Réponse 204** No Content

---

### Fan Zones

#### `POST /api/geo/fanzones`

Crée une nouvelle fan zone.

- **Rôle requis** : `ROLE_ADMIN`

**Body :**

```json
{
  "nom": "Zone Olympique Bassin",
  "description": "Fan zone principale près du bassin",
  "latitude": 48.8566,
  "longitude": 2.3522,
  "capaciteMax": 5000,
  "adresse": "1 Av. Pierre de Coubertin, Paris",
  "services": ["ECRAN_GEANT", "RESTAURATION", "BOUTIQUE"]
}
```

Champs obligatoires : `nom`, `latitude`, `longitude`. Tous les autres sont optionnels.

**Réponse 201 :**

```json
{
  "id": 1,
  "nom": "Zone Olympique Bassin",
  "description": "Fan zone principale près du bassin",
  "latitude": 48.8566,
  "longitude": 2.3522,
  "capaciteMax": 5000,
  "adresse": "1 Av. Pierre de Coubertin, Paris",
  "services": ["ECRAN_GEANT", "RESTAURATION", "BOUTIQUE"],
  "distance": null
}
```

---

#### `GET /api/geo/fanzones`

Retourne toutes les fan zones. **Accès public**, aucun token requis.

- **Réponse 200** : `FanZoneResponse[]` (champ `distance` est `null`)

---

#### `GET /api/geo/fanzones/nearby?lat=&lng=&rayon=`

Retourne les fan zones dans un rayon donné, **triées par distance croissante**. **Accès public**.

| Paramètre | Type   | Défaut | Description                     |
|-----------|--------|--------|---------------------------------|
| `lat`     | double | —      | Latitude du point de référence  |
| `lng`     | double | —      | Longitude du point de référence |
| `rayon`   | double | `500`  | Rayon de recherche en **mètres** |

- **Réponse 200** : `FanZoneResponse[]` avec le champ `distance` rempli (en mètres)

**Exemple :**

```http
GET /api/geo/fanzones/nearby?lat=48.8566&lng=2.3522&rayon=1000
```

La distance est calculée avec la **formule de Haversine** (rayon terrestre : 6 371 000 m).

---

#### `DELETE /api/geo/fanzones/{fanzoneId}`

Supprime une fan zone.

- **Rôle requis** : `ROLE_ADMIN`
- **Réponse 204** No Content
- **Réponse 404** si la fan zone n'existe pas

---

### Valeurs de `TypeService`

| Valeur         | Description         |
|----------------|---------------------|
| `ECRAN_GEANT`  | Écran géant         |
| `RESTAURATION` | Restauration        |
| `BOUTIQUE`     | Boutique officielle |
| `MEDICAL`      | Point médical       |

---

## WebSocket temps réel

### Connexion

- **Endpoint SockJS** : `http://localhost:8091/ws/geo`
- **Protocole** : STOMP over SockJS
- **Broker** : Simple broker (in-memory), préfixe `/topic`

> Utiliser l'URL HTTP (pas `ws://`) — SockJS gère le transport.

### Topic par athlète

Chaque athlète dispose d'un topic dédié :

```text
/topic/athletes/{athleteId}
```

Dès qu'un athlète enregistre une position via `POST /api/geo/athletes/{athleteId}/position`, le service diffuse automatiquement le message suivant sur ce topic :

```json
{
  "athleteId": 9,
  "latitude": 48.8566,
  "longitude": 2.3522,
  "timestamp": "2026-03-07T20:27:07.579985"
}
```

### Exemple de connexion (JavaScript)

```javascript
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8091/ws/geo'),
  reconnectDelay: 5000,
  onConnect: () => {
    // Suivre un athlète spécifique
    client.subscribe('/topic/athletes/9', (message) => {
      const position = JSON.parse(message.body);
      console.log(position.latitude, position.longitude);
    });

    // Suivre plusieurs athlètes simultanément
    [9, 12, 15].forEach(id => {
      client.subscribe(`/topic/athletes/${id}`, (message) => {
        const position = JSON.parse(message.body);
        // mettre à jour la carte
      });
    });
  }
});

client.activate();
```

---

## Sécurité

### Authentification JWT

Le service valide les tokens JWT émis par l'`auth-service` (secret HMAC-SHA256 partagé).

Chaque requête protégée doit inclure le header :

```http
Authorization: Bearer <token>
```

### Claims JWT exploités

| Claim     | Type   | Usage                                                       |
|-----------|--------|-------------------------------------------------------------|
| `subject` | String | Username de l'utilisateur                                   |
| `role`    | String | Rôle (`ROLE_ATHLETE`, `ROLE_COMMISSAIRE`, `ROLE_ADMIN`, …) |
| `userId`  | Long   | ID de l'utilisateur (vérifié pour le POST position)        |

### Matrice des accès

| Endpoint                                  | PUBLIC | ATHLETE               | COMMISSAIRE | ADMIN |
|-------------------------------------------|:------:|:---------------------:|:-----------:|:-----:|
| `POST /api/geo/athletes/{id}/position`    |        | ✅ (son id uniquement) |             |       |
| `GET /api/geo/athletes/{id}/position`     |        |                       | ✅          | ✅    |
| `GET /api/geo/athletes/{id}/history`      |        |                       | ✅          | ✅    |
| `DELETE /api/geo/athletes/{id}/positions` |        |                       |             | ✅    |
| `POST /api/geo/fanzones`                  |        |                       |             | ✅    |
| `GET /api/geo/fanzones`                   | ✅     |                       |             |       |
| `GET /api/geo/fanzones/nearby`            | ✅     |                       |             |       |
| `DELETE /api/geo/fanzones/{id}`           |        |                       |             | ✅    |
| `WS /ws/geo/**`                           | ✅     |                       |             |       |

### Règle "own ID only"

Lors d'un `POST /api/geo/athletes/{athleteId}/position`, le service compare le `userId` extrait du JWT avec l'`athleteId` de l'URL. Si les deux ne correspondent pas → **403 Forbidden**. Un athlète ne peut envoyer que sa propre position GPS.

---

## Gestion des erreurs

Toutes les erreurs retournent le format uniforme suivant :

```json
{
  "status": 403,
  "error": "Accès refusé",
  "message": "Vous ne pouvez enregistrer que votre propre position",
  "timestamp": "2026-03-07T20:27:07.579985",
  "path": "/api/geo/athletes/9/position"
}
```

| Code HTTP | Cause                                               |
|-----------|-----------------------------------------------------|
| `400`     | Body invalide (champ obligatoire manquant)          |
| `401`     | Token JWT absent ou invalide                        |
| `403`     | Rôle insuffisant ou violation de la règle "own ID"  |
| `404`     | Ressource introuvable (athlète sans position, fan zone inexistante) |
| `500`     | Erreur interne inattendue                           |

---

## Tests

### Structure

```text
src/test/
├── java/
│   └── com/ciblorgasport/geolocationservice/
│       ├── service/
│       │   ├── AthletePositionServiceTest.java    # 6 tests unitaires (Mockito)
│       │   └── FanZoneBusinessServiceTest.java    # 8 tests unitaires (Haversine + CRUD)
│       └── controller/
│           ├── AthletePositionControllerTest.java # 10 tests @WebMvcTest
│           └── FanZoneControllerTest.java         # 9 tests @WebMvcTest
└── resources/
    ├── application-test.properties                # H2 in-memory
    └── mockito-extensions/
        └── org.mockito.plugins.MockMaker          # mock-maker-subclass (compatibilité Java 21+)
```

**Total : 33 tests — 0 échec.**

### Ce qui est testé

- Enregistrement de position : cas nominal, mauvais `userId` → 403, body invalide → 400
- Lecture de position : 200 (commissaire), 403 (athlète), 404 (inconnu)
- Historique : 200 avec tableau
- Suppression : 204 (admin), 403 (commissaire)
- Fan zones : création 201, validation 400, accès 403, liste 200, proximité (tri + rayon), suppression 204/404
- Formule Haversine : même point = 0 m, Paris → Lyon ≈ 392 km
- Diffusion WebSocket : vérification que `convertAndSend` est appelé sur `/topic/athletes/{id}`
