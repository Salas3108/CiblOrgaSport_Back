# Analytics Service — CiblOrgaSport

Service de suivi d'utilisation pour les Championnats d'Europe de Natation 2026.
Il collecte automatiquement toutes les requêtes HTTP passant par le gateway,
agrège les données chaque nuit, et expose les stats à Metabase pour les dashboards de Marius.

---

## Architecture

```
[Frontend / Mobile]
        ↓
[Gateway :8080]
        ↓ → AnalyticsFilter (fire-and-forget, 500ms timeout)
        ↓                  ↓
[Microservices]    [analytics-service :8090]
                           ↓
                   [PostgreSQL :5400 / glop]
                           ↑
                   [Metabase :3001]  ← Dashboard Marius
```

---

## Ports

| Service           | Port  |
|-------------------|-------|
| analytics-service | 8090  |
| PostgreSQL        | 5400  |
| Metabase          | 3001  |
| pgAdmin           | 8082  |

---

## Démarrage

### Option 1 — Docker (recommandé)

```bash
# Depuis la racine du projet CiblOrgaSport_Back
docker compose up -d postgres analytics-service metabase

# Vérifier que tout tourne
docker ps
```

> **Important** : ne pas lancer `mvn spring-boot:run` en même temps que Docker,
> le port 8090 serait déjà occupé par le conteneur.

### Option 2 — Local (développement)

PostgreSQL doit tourner en Docker avant de lancer le service localement :

```bash
docker compose up -d postgres

# Puis dans analytics-service/
mvn spring-boot:run
```

Si le port 8090 est déjà occupé :
```bash
kill -9 $(lsof -t -i:8090)
```

---

## Base de données

- **Host** : `localhost:5400` (local) ou `postgres:5432` (Docker)
- **Base** : `glop`
- **User** : `admin` / **Password** : `password`

### Tables créées automatiquement (ddl-auto=update)

#### `event_log` — Événements bruts
| Colonne       | Type         | Description                        |
|---------------|--------------|------------------------------------|
| id            | BIGINT PK    | Auto-incrémenté                    |
| user_id       | BIGINT       | ID de l'utilisateur (nullable)     |
| user_role     | VARCHAR(50)  | ADMIN, ATHLETE, COMMISSAIRE, etc.  |
| event_type    | VARCHAR(50)  | Type d'événement (voir enum)       |
| endpoint      | VARCHAR(255) | URL appelée                        |
| http_method   | VARCHAR(10)  | GET, POST, PUT, DELETE             |
| status_code   | INTEGER      | Code HTTP retourné                 |
| duration_ms   | BIGINT       | Temps de traitement en ms          |
| ip_address    | VARCHAR(50)  | IP du client                       |
| timestamp     | TIMESTAMP    | Date/heure de l'événement (indexé) |
| metadata      | TEXT (JSON)  | Infos supplémentaires (competitionId, etc.) |

Index : `timestamp`, `user_id`, `event_type`

#### `daily_stats` — Agrégats journaliers
Un enregistrement par jour, calculé chaque nuit à **00h01**.

| Colonne                   | Description                          |
|---------------------------|--------------------------------------|
| stat_date                 | Date (UNIQUE)                        |
| total_connections         | Nombre total de connexions           |
| unique_users              | Utilisateurs uniques connectés       |
| connections_athletes      | Connexions des athlètes              |
| connections_spectateurs   | Connexions des users (spectateurs)   |
| connections_commissaires  | Connexions des commissaires          |
| connections_volontaires   | Connexions des volontaires           |
| connections_admins        | Connexions des admins                |
| total_page_views          | Vues de pages                        |
| total_notifications_sent  | Notifications envoyées               |
| total_competition_views   | Vues de compétitions                 |
| total_result_views        | Vues de résultats                    |
| avg_response_time_ms      | Temps de réponse moyen               |
| total_incidents           | Incidents déclarés                   |
| calculated_at             | Date du calcul                       |

#### `weekly_stats` — Agrégats hebdomadaires
Un enregistrement par semaine, calculé chaque **lundi à 00h05**.

| Colonne                | Description                              |
|------------------------|------------------------------------------|
| week_start             | Lundi de la semaine (UNIQUE)             |
| week_end               | Dimanche de la semaine                   |
| total_connections      | Connexions totales sur la semaine        |
| unique_users           | Utilisateurs uniques                     |
| peak_day               | Jour avec le plus de connexions          |
| peak_connections       | Nombre de connexions le jour de pic      |
| top_competition_id     | ID de la compétition la plus vue         |
| top_competition_views  | Nombre de vues de cette compétition      |
| avg_daily_connections  | Moyenne de connexions par jour           |
| growth_rate_percent    | Évolution vs semaine précédente (%)      |
| calculated_at          | Date du calcul                           |

---

## Types d'événements (EventType)

| Type                   | Déclenché quand                          |
|------------------------|------------------------------------------|
| USER_LOGIN             | Appel à `/auth/login`                    |
| USER_LOGOUT            | Appel à `/auth/logout`                   |
| PAGE_VIEW              | Toute autre requête                      |
| COMPETITION_VIEW       | Appel à `/competitions/**` ou `/events/**` |
| RESULT_VIEW            | Appel à `/results/**` ou `/epreuves/**`  |
| NOTIFICATION_SENT      | Appel à `/notifications/**`             |
| NOTIFICATION_SUBSCRIBED| Appel à `/abonnements/**`               |
| FANZONE_VIEW           | Appel à `/geo/**` ou `/fanzones/**`     |
| ATHLETE_PROFILE_VIEW   | Appel à `/athlete/**`                   |
| VOLUNTEER_CHECKIN      | (manuel via gateway filter)             |
| INCIDENT_DECLARED      | Appel à `/incidents/**`                 |
| SEARCH_PERFORMED       | Appel à `/search/**`                    |

---

## Endpoints REST

Tous les endpoints `GET` sont réservés au rôle **ADMIN** (JWT requis).
Le `POST /events/track` est public (appelé par le gateway).

| Méthode | URL                                        | Description                          |
|---------|--------------------------------------------|--------------------------------------|
| POST    | `/api/analytics/events/track`              | Reçoit les événements du gateway     |
| GET     | `/api/analytics/daily?date=2026-07-15`     | Stats d'un jour précis               |
| GET     | `/api/analytics/daily/range?start=&end=`   | Stats sur une période                |
| GET     | `/api/analytics/weekly?weekStart=2026-07-13` | Stats d'une semaine                |
| GET     | `/api/analytics/weekly/all`                | Toutes les semaines                  |
| GET     | `/api/analytics/events/today`              | Événements du jour (temps réel)      |
| GET     | `/api/analytics/events/live`               | 50 derniers événements bruts         |
| GET     | `/api/analytics/top/competitions?start=&end=` | Top 5 compétitions                |
| POST    | `/api/analytics/recalculate?date=`         | Force le recalcul d'une journée      |

### Exemple — Envoyer un événement test

```bash
curl -X POST http://localhost:8090/api/analytics/events/track \
  -H "Content-Type: application/json" \
  -d '{
    "eventType": "USER_LOGIN",
    "userRole": "ADMIN",
    "endpoint": "/auth/login",
    "httpMethod": "POST",
    "statusCode": 200,
    "durationMs": 45,
    "ipAddress": "127.0.0.1"
  }'
```

### Exemple — Forcer le recalcul du jour

```bash
curl -X POST "http://localhost:8090/api/analytics/recalculate?date=2026-07-15" \
  -H "Authorization: Bearer <token-admin>"
```

---

## Scheduler automatique

| Job           | Cron          | Heure       | Action                              |
|---------------|---------------|-------------|-------------------------------------|
| daily_stats   | `0 1 0 * * *` | 00h01 / nuit | Calcule les stats de J-1           |
| weekly_stats  | `0 5 0 * * MON` | 00h05 lundi | Calcule les stats de la semaine préc. |

En cas d'échec du job daily, il tente automatiquement de recalculer J-2.

---

## Sécurité

- JWT partagé avec les autres services
- Secret : `mySuperSecretKeyForCiblorgasportApplicationThatIsVeryLongAndSecure`
- Propriété : `ciblorgasport.app.jwtSecret`
- `POST /api/analytics/events/track` → `permitAll()` (appelé par le gateway)
- `GET /api/analytics/**` → `hasRole('ADMIN')` uniquement

---

## Comment fonctionne la collecte (Gateway Filter)

Le fichier `gateway/src/main/java/com/ciblorgasport/gateway/filter/AnalyticsFilter.java`
intercepte **toutes** les requêtes passant par le gateway :

1. Laisse passer la requête normalement (non bloquant)
2. Après la réponse, construit un payload JSON avec : endpoint, méthode, statut, durée, IP, rôle
3. Envoie ce payload en `POST /api/analytics/events/track` via WebClient
4. Timeout de 500ms — si l'analytics-service est down, la requête principale n'est pas affectée

Requêtes **exclues** de la collecte :
- `/api/analytics/**` (éviter la récursion)
- `/actuator/**`
- Méthodes `OPTIONS` (CORS preflight)

---

## Metabase — Configuration initiale

### 1. Ouvrir Metabase
```
http://localhost:3001
```

### 2. Créer un compte admin
Remplir le formulaire d'inscription (email + mot de passe).

### 3. Connecter la base de données
Choisir **PostgreSQL** et remplir :

| Champ         | Valeur    |
|---------------|-----------|
| Host          | `postgres` |
| Port          | `5432`    |
| Database name | `glop`    |
| Username      | `admin`   |
| Password      | `password` |

### 4. Créer les graphiques
- Cliquer **+ New** → **SQL query**
- Sélectionner la base **glop**
- Copier-coller une requête depuis `metabase-queries.sql`
- Cliquer **Run** puis **Save** → ajouter au dashboard

### 5 requêtes disponibles dans `metabase-queries.sql`

| N° | Graphique                                | Type recommandé      |
|----|------------------------------------------|----------------------|
| 1  | Connexions sur 30 jours                  | Ligne                |
| 2  | Répartition par rôle (7 jours)           | Camembert            |
| 3  | Top 5 compétitions                       | Barres horizontales  |
| 4  | Notifications par type (14 jours)        | Barres empilées      |
| 5  | Tableau récapitulatif hebdomadaire       | Tableau              |

---

## Structure du projet

```
analytics-service/
├── pom.xml
├── Dockerfile
├── metabase-queries.sql          ← requêtes SQL pour Metabase
├── README.md                     ← ce fichier
└── src/main/
    ├── resources/
    │   └── application.properties
    └── java/com/ciblorgasport/analyticsservice/
        ├── AnalyticsServiceApplication.java
        ├── entity/
        │   ├── EventLog.java
        │   ├── DailyStats.java
        │   └── WeeklyStats.java
        ├── enums/
        │   └── EventType.java
        ├── dto/
        │   ├── EventLogRequest.java
        │   ├── DailyStatsResponse.java
        │   └── WeeklyStatsResponse.java
        ├── repository/
        │   ├── EventLogRepository.java
        │   ├── DailyStatsRepository.java
        │   └── WeeklyStatsRepository.java
        ├── service/
        │   ├── EventLogService.java
        │   ├── DailyStatsService.java
        │   └── WeeklyStatsService.java
        ├── aspect/
        │   └── AnalyticsAspect.java
        ├── scheduler/
        │   └── AnalyticsScheduler.java
        ├── controller/
        │   └── AnalyticsController.java
        ├── security/
        │   ├── JwtUtils.java
        │   ├── AuthTokenFilter.java
        │   └── SecurityConfig.java
        └── config/
            └── AsyncConfig.java
```

---

## Notes techniques

### Problème JSONB PostgreSQL
L'opérateur `?` de PostgreSQL pour tester l'existence d'une clé JSON
entre en conflit avec les paramètres `?1` de Spring Data JPA.

**Solution** : utiliser la fonction équivalente `jsonb_exists()` :
```sql
-- NE PAS UTILISER (conflit avec Spring Data JPA) :
WHERE metadata::jsonb ? 'competitionId'

-- UTILISER À LA PLACE :
WHERE jsonb_exists(metadata::jsonb, 'competitionId')
```

### Insertion asynchrone
Les événements sont insérés via `@Async` dans un pool de threads dédié
(`analyticsExecutor`, 4-10 threads, queue de 500).
En cas d'erreur d'insertion, un `WARN` est loggé mais la requête principale
n'est jamais bloquée ni échouée.
