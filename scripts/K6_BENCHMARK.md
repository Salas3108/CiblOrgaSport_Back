# Benchmark k6

Ce dossier remplace le benchmark Newman/Postman par k6.

## Prerequis

- k6 installe et disponible dans le PATH
- Docker disponible (pour la capture `docker stats`)
- Services cibles demarres

## Fichiers

- `scripts/k6/main.js`: scenario k6 initial (login + endpoint protege `auth/me`)
- `scripts/run-eco-benchmark.ps1`: orchestration benchmark k6 + capture Docker stats

## Lancer le benchmark

Depuis la racine du repo:

```powershell
powershell -ExecutionPolicy Bypass -File ".\scripts\run-eco-benchmark.ps1"
```

Parametres principaux:

```powershell
powershell -ExecutionPolicy Bypass -File ".\scripts\run-eco-benchmark.ps1" \
  -Vus 20 \
  -RampUp "45s" \
  -Steady "180s" \
  -RampDown "45s" \
  -DurationSeconds 300 \
  -AuthBase "http://localhost:8080" \
  -AuthUsername "admin3" \
  -AuthPassword "123456"
```

## Sorties

Chaque execution cree un dossier horodate:

`logs/k6-benchmark/<timestamp>/`

Avec les fichiers:

- `k6-summary.json`: resume d'execution k6
- `k6-metrics.json`: flux de metriques k6 (format json output)
- `k6-output.txt`: sortie console k6
- `docker-stats.csv`: echantillons CPU/RAM/IO par conteneur

## Note

Ce premier lot couvre la base d'authentification. Les parcours Postman restants seront migres par increments vers des scenarios k6 modules.

Le lot critique suivant est desormais integre dans `scripts/k6/main.js`:

- Event service: lecture des events, epreuves et competitions
- Participants service: lecture des assignments d'epreuves et des equipes commissaire
- Resultats service: lecture des classements publics et commissaire par epreuve
- Abonnement service: lecture des abonnements utilisateur
- Billetterie service: lecture liste tickets et estimation de prix
- Incident service: lecture des incidents et endpoint debug auth
- Lieu service: lecture des lieux
- Notifications service: lecture des notifications et compteur
- Analytics service: lecture daily/events today
- Geolocation service: lecture des fanzones et nearby
- Volunteer service: lecture profil et planning

Le script detecte dynamiquement un `epreuveId` depuis event-service pour enchaîner les appels resultats.

## Grafana Integration

### Configuration

L'integration des metriques k6 dans Prometheus/Grafana repose sur deux points:

- Prometheus active le remote write receiver via `--web.enable-remote-write-receiver` dans `docker-compose.yml`.
- Le script `run-eco-benchmark.ps1` pousse les metriques k6 vers Prometheus avec la sortie `experimental-prometheus-rw`.

### Dashboard

Le dashboard principal inclut des panneaux k6 dedies:

- `k6 Active VUs`
- `k6 Request Rate`
- `k6 Failure & Check Error Rate`
- `k6 HTTP Duration p95/p99`

### Execution

```powershell
docker compose down
docker compose up -d

powershell -ExecutionPolicy Bypass -File ".\scripts\run-eco-benchmark.ps1" \
  -Vus 20 \
  -RampUp "45s" \
  -Steady "180s" \
  -RampDown "45s" \
  -AbonnementBase "http://localhost:8082" \
  -BilletterieBase "http://localhost:8081" \
  -EventBase "http://localhost:8084" \
  -IncidentBase "http://localhost:8083" \
  -LieuBase "http://localhost:8090" \
  -NotificationsBase "http://localhost:8089" \
  -ParticipantsBase "http://localhost:8087" \
  -ResultatsBase "http://localhost:8088" \
  -AnalyticsBase "http://localhost:8091" \
  -GeolocationBase "http://localhost:8092" \
  -VolunteerBase "http://localhost:8093" \
  -PrometheusWriteUrl "http://localhost:9090/api/v1/write"
```
