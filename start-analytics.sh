#!/bin/bash
# ============================================================
# start-analytics.sh — Démarrage complet + remplissage analytics
# Lance Docker, injecte les données historiques SQL, démarre
# les microservices, génère des événements réels, puis recalcule
# daily_stats et weekly_stats pour les 30 derniers jours.
#
# Usage : chmod +x start-analytics.sh && ./start-analytics.sh
# ============================================================

set -e

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

GATEWAY="http://localhost:8080"
ANALYTICS="http://localhost:8090"
LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║       CiblOrgaSport — Analytics Startup Script      ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""

# ─── 1. Docker ────────────────────────────────────────────
echo "=== [1/6] Démarrage des conteneurs Docker ==="
docker compose build analytics-service --quiet
docker compose up -d postgres analytics-service metabase pgadmin kafka
echo "    Conteneurs lancés."

# ─── 2. Attente PostgreSQL ─────────────────────────────────
echo ""
echo "=== [2/6] Attente PostgreSQL ==="
MAX_WAIT=60
COUNT=0
until docker exec postgres pg_isready -U admin -d glop > /dev/null 2>&1; do
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_WAIT ]; then
        echo "ERREUR : PostgreSQL non disponible après ${MAX_WAIT}s"
        exit 1
    fi
    printf "."
    sleep 1
done
echo ""
echo "    PostgreSQL prêt."

# ─── 3. Injection SQL historique ──────────────────────────
echo ""
echo "=== [3/6] Injection des données historiques (30 jours) ==="
docker exec -i postgres psql -U admin -d glop < "$ROOT/analytics-service/seed-analytics.sql"
echo "    Données historiques injectées dans event_log."

# ─── 4. Démarrage microservices Maven ─────────────────────
echo ""
echo "=== [4/6] Démarrage des microservices (Maven) ==="

start_service() {
    local name="$1"
    local dir="$2"
    if [ -d "$ROOT/$dir" ]; then
        echo "    Démarrage $name..."
        (cd "$ROOT/$dir" && mvn spring-boot:run -q > "$LOG_DIR/$name.log" 2>&1) &
        echo "    $name lancé (PID $!, log: logs/$name.log)"
    else
        echo "    AVERTISSEMENT : répertoire $dir introuvable, $name ignoré."
    fi
}

start_service "auth-service"      "auth-service"
start_service "event-service"     "event-service"
start_service "incident-service"  "incident-service"
start_service "participants-service" "participants-service"
start_service "abonnement-service"   "abonnement-service"
# Gateway en dernier (dépend des autres)
sleep 5
start_service "gateway"           "gateway"

echo ""
echo "=== Attente gateway ($GATEWAY) ==="
MAX_GW=120
COUNT=0
until curl -sf "$GATEWAY/actuator/health" > /dev/null 2>&1; do
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_GW ]; then
        echo "AVERTISSEMENT : Gateway non disponible après ${MAX_GW}s"
        echo "    Les événements réels ne seront pas générés."
        echo "    Passage direct au recalcul."
        SKIP_LIVE=true
        break
    fi
    printf "."
    sleep 2
done
echo ""
[ -z "$SKIP_LIVE" ] && echo "    Gateway prêt."

# ─── 5. Génération événements réels ───────────────────────
echo ""
echo "=== [5/6] Génération d'événements réels via le gateway ==="
if [ -z "$SKIP_LIVE" ]; then
    bash "$ROOT/seed-events.sh"
else
    echo "    (ignoré — gateway indisponible)"
fi

# ─── 6. Recalcul daily_stats & weekly_stats ───────────────
echo ""
echo "=== [6/6] Recalcul daily_stats et weekly_stats (30 jours) ==="

# Attente analytics-service
MAX_AN=60
COUNT=0
until curl -sf "$ANALYTICS/api/analytics/events/live" > /dev/null 2>&1; do
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_AN ]; then
        echo "ERREUR : analytics-service non disponible après ${MAX_AN}s"
        exit 1
    fi
    printf "."
    sleep 2
done
echo ""
echo "    analytics-service prêt."

# Récupérer un token admin pour recalculate (endpoint ADMIN protégé)
echo "    Récupération du token admin..."
ADMIN_TOKEN=""
if curl -sf "$GATEWAY/actuator/health" > /dev/null 2>&1; then
    ADMIN_TOKEN=$(curl -sf -X POST "$GATEWAY/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin123"}' \
        2>/dev/null | python3 -c "
import sys, json
try:
    d = json.load(sys.stdin)
    print(d.get('token','') or d.get('accessToken','') or d.get('jwt',''))
except:
    print('')
" 2>/dev/null || true)
fi

if [ -z "$ADMIN_TOKEN" ]; then
    echo "    AVERTISSEMENT : token admin non obtenu."
    echo "    Le recalcul nécessite un token admin."
    echo "    Lancez manuellement :"
    echo "    curl -X POST 'http://localhost:8090/api/analytics/recalculate?date=YYYY-MM-DD' -H 'Authorization: Bearer <token>'"
else
    echo "    Token admin obtenu. Recalcul des 30 derniers jours..."
    ERRORS=0
    for i in $(seq 0 30); do
        # Compatible macOS (date -v) et Linux (date -d)
        DATE=$(date -v -${i}d +%Y-%m-%d 2>/dev/null || date -d "-${i} days" +%Y-%m-%d)
        HTTP_STATUS=$(curl -sf -o /dev/null -w "%{http_code}" \
            -X POST "$ANALYTICS/api/analytics/recalculate?date=$DATE" \
            -H "Authorization: Bearer $ADMIN_TOKEN" 2>/dev/null || echo "000")
        if [ "$HTTP_STATUS" = "200" ]; then
            printf "."
        else
            printf "x"
            ERRORS=$((ERRORS+1))
        fi
    done
    echo ""
    if [ $ERRORS -eq 0 ]; then
        echo "    30 jours recalculés avec succès."
    else
        echo "    $ERRORS erreurs lors du recalcul (voir logs)."
    fi
fi

# ─── Résumé ───────────────────────────────────────────────
echo ""
echo "╔══════════════════════════════════════════════════════╗"
echo "║                  TOUT EST PRÊT                       ║"
echo "╠══════════════════════════════════════════════════════╣"
echo "║  pgAdmin    : http://localhost:8082                  ║"
echo "║  Metabase   : http://localhost:3001                  ║"
echo "║  Analytics  : http://localhost:8090/api/analytics/   ║"
echo "║  Gateway    : http://localhost:8080                  ║"
echo "╚══════════════════════════════════════════════════════╝"
echo ""
echo "Vérification rapide des tables :"
docker exec postgres psql -U admin -d glop -c \
    "SELECT 'event_log' AS table, count(*) AS lignes FROM event_log
     UNION ALL SELECT 'daily_stats', count(*) FROM daily_stats
     UNION ALL SELECT 'weekly_stats', count(*) FROM weekly_stats;"
echo ""
