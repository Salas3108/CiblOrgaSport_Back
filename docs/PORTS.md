# Architecture des ports — CiblOrgaSport

## Vue d'ensemble

En production, **un seul point d'entrée public** : le port 80 sur l'IP `137.74.133.131`.
Toutes les requêtes passent par Nginx → Spring Cloud Gateway → microservice cible.
Les ports internes ne sont jamais exposés directement sur Internet.

```
Internet
    |
    | :80 (HTTP public)
    |
 [Nginx]  — vm-services (137.74.133.131)
    |
    | :8080 (réseau Docker interne)
    |
 [Spring Cloud Gateway]
    |
    +——— auth-service        :8081
    +——— billetterie         :8083
    +——— event-service       :8084
    +——— abonnement-service  :8085
    +——— incident-service    :8086
    +——— participants-service :8087
    +——— resultats-service   :8088
    +——— notifications-service :8089
    +——— lieu-service        :8090
    +——— geolocation-service :8091
    +——— analytics-service   :8092
    +——— volunteer-service   :8093
    |
    +——— [PostgreSQL]        :5432 (réseau Docker interne)
    +——— [Kafka]             :9092 (réseau Docker interne)
```

---

## Tableau complet des ports

### Services applicatifs

| Service | Port interne | Exposé | Chemin gateway | Description |
|---------|-------------|--------|----------------|-------------|
| Spring Cloud Gateway | 8080 | Non (interne Docker) | — | Routeur central |
| auth-service | 8081 | Non | `/auth/**` | Authentification JWT |
| billetterie | 8083 | Non | `/billets/**` `/abonnements/**` `/api/tickets/**` | Billets et réservations |
| event-service | 8084 | Non | `/events/**` `/competitions/**` `/epreuves/**` `/admin/**` | Gestion des événements |
| abonnement-service | 8085 | Non | `/api/abonnements/**` | Abonnements |
| incident-service | 8086 | Non | `/incidents/**` `/api/incidents/**` | Incidents |
| participants-service | 8087 | Non | `/athlete/**` `/api/athlete/**` `/commissaire/**` | Participants |
| resultats-service | 8088 | Non | `/resultats/**` `/api/resultats/**` | Résultats sportifs |
| notifications-service | 8089 | Non | `/notifications/**` `/api/notifications/**` | Notifications |
| lieu-service | 8090 | Non | `/lieux/**` | Lieux / sites |
| geolocation-service | 8091 | Non | (interne) | Géolocalisation |
| analytics-service | 8092 | Non | `/api/analytics/**` | Statistiques |
| volunteer-service | 8093 | Non | `/api/v1/volunteers/**` `/api/v1/admin/**` | Volontaires |

### Infrastructure

| Composant | Port interne | Exposé | Notes |
|-----------|-------------|--------|-------|
| Nginx | 80 | **Oui (public)** | Reverse proxy, point d'entrée unique |
| PostgreSQL 16 | 5432 | Non (réseau Docker) | Base de données partagée `glop` |
| Kafka (KRaft) | 9092 | Non (réseau Docker) | Broker de messages, mode sans Zookeeper |
| Kafka Controller | 9093 | Non (réseau Docker) | Gestion interne KRaft |

---

## Routage Gateway — détail des prédicats

| Route ID | URI interne | Chemins déclencheurs |
|----------|-------------|---------------------|
| auth-service | `http://auth-service:8081` | `/auth/**` |
| event-service | `http://event-service:8084` | `/events/**` `/competitions/**` `/epreuves/**` `/admin/**` |
| lieu-service | `http://lieu-service:8090` | `/lieux/**` |
| billetterie | `http://billetterie:8083` | `/billets/**` `/abonnements/**` `/api/tickets/**` |
| abonnement | `http://abonnement-service:8085` | `/abonnements/**` `/api/abonnements/**` |
| incident-service | `http://incident-service:8086` | `/incidents/**` `/api/incidents/**` |
| participants-service | `http://participants-service:8087` | `/athlete/**` `/api/athlete/**` `/commissaire/**` `/api/commissaire/**` |
| notification-service | `http://notifications-service:8089` | `/notifications/**` `/api/notifications/**` |
| volunteer-service | `http://volunteer-service:8093` | `/api/v1/admin/programs/**` `/api/v1/admin/volunteers/**` `/api/v1/volunteers/**` |
| resultats-service | `http://resultats-service:8088` | `/resultats/**` `/api/resultats/**` |

---

## Sécurité des ports

### Règles pare-feu recommandées (OVH Security Groups)

| VM | Port | Source | Raison |
|----|------|--------|--------|
| vm-services | 22 | Votre IP uniquement | SSH administration |
| vm-services | 80 | 0.0.0.0/0 (Internet) | API publique |
| vm-services | 443 | 0.0.0.0/0 (Internet) | API publique HTTPS (si SSL activé) |

> Tous les autres ports (8080–8093, 5432, 9092) restent fermés sur Internet.
> La communication entre services se fait exclusivement via le réseau Docker interne `prod-net`.

---

## Ports en développement local

En local (`docker-compose.yml`), les services sont exposés directement pour faciliter les tests :

| Service | Port local |
|---------|-----------|
| Gateway | 8080 |
| auth-service | 8081 |
| billetterie | 8083 |
| event-service | 8084 |
| abonnement-service | 8085 |
| incident-service | 8086 |
| participants-service | 8087 |
| resultats-service | 8088 |
| notifications-service | 8089 |
| lieu-service | 8090 |
| geolocation-service | 8091 |
| analytics-service | 8092 |
| volunteer-service | 8093 |
| PostgreSQL | 5400 (externe) → 5432 (interne) |
| pgAdmin | 8082 |
| Metabase | 3001 |
| Kafka | 9092 |
