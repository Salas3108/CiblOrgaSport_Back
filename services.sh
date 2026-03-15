#!/bin/bash
# ============================================================
# services.sh — Démarrage / Arrêt de tous les services
# Usage :
#   ./services.sh start   → lance Docker + tous les microservices Maven
#   ./services.sh stop    → arrête tout proprement
# ============================================================

ROOT="$(cd "$(dirname "$0")" && pwd)"
cd "$ROOT"

LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

# ─── Services Maven (ordre : dépendances d'abord, gateway en dernier) ───
MAVEN_SERVICES=(
    "auth-service:8081"
    "event-service:8084"
    "billetterie:8083"
    "abonnement-service:8085"
    "incident-service:8086"
    "participants-service:8087"
    "notifications-service:8089"
    "geolocation-service:8091"
    "gateway:8080"
)

# ─── Couleurs ─────────────────────────────────────────────
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

ok()   { echo -e "  ${GREEN}✓${NC} $1"; }
warn() { echo -e "  ${YELLOW}⚠${NC}  $1"; }
err()  { echo -e "  ${RED}✗${NC} $1"; }
info() { echo -e "  ${BLUE}→${NC} $1"; }

# ─── Helpers ──────────────────────────────────────────────
port_in_use() {
    lsof -ti :"$1" > /dev/null 2>&1
}

kill_port() {
    local port="$1"
    local pids
    pids=$(lsof -ti :"$port" 2>/dev/null || true)
    if [ -n "$pids" ]; then
        echo "$pids" | xargs kill -15 2>/dev/null || true
        sleep 1
        pids=$(lsof -ti :"$port" 2>/dev/null || true)
        [ -n "$pids" ] && echo "$pids" | xargs kill -9 2>/dev/null || true
        return 0
    fi
    return 1
}

# ============================================================
# COMMANDE : start
# ============================================================
cmd_start() {
    echo ""
    echo "╔══════════════════════════════════════════════════════╗"
    echo "║         CiblOrgaSport — Démarrage des services       ║"
    echo "╚══════════════════════════════════════════════════════╝"

    # ── 1. Docker ───────────────────────────────────────────
    echo ""
    echo "=== [1/3] Services Docker ==="
    docker compose up -d postgres kafka pgadmin analytics-service metabase
    ok "Docker : postgres, kafka, pgadmin, analytics-service, metabase"

    # ── 2. Attente PostgreSQL ───────────────────────────────
    echo ""
    info "Attente PostgreSQL..."
    MAX=60; COUNT=0
    until docker exec postgres pg_isready -U admin -d glop > /dev/null 2>&1; do
        COUNT=$((COUNT+1))
        [ $COUNT -ge $MAX ] && { err "PostgreSQL non disponible après ${MAX}s"; exit 1; }
        printf "."
        sleep 1
    done
    echo ""
    ok "PostgreSQL prêt."

    # ── 3. Services Maven ───────────────────────────────────
    echo ""
    echo "=== [2/3] Microservices Maven ==="

    for entry in "${MAVEN_SERVICES[@]}"; do
        SERVICE="${entry%%:*}"
        PORT="${entry##*:}"

        if [ ! -d "$ROOT/$SERVICE" ]; then
            warn "$SERVICE : dossier introuvable, ignoré."
            continue
        fi

        if port_in_use "$PORT"; then
            warn "$SERVICE : port $PORT déjà utilisé, ignoré."
            continue
        fi

        info "Démarrage $SERVICE (port $PORT)..."
        (cd "$ROOT/$SERVICE" && mvn spring-boot:run -q \
            > "$LOG_DIR/$SERVICE.log" 2>&1) &
        echo $! > "$LOG_DIR/$SERVICE.pid"
        ok "$SERVICE lancé (PID $(cat "$LOG_DIR/$SERVICE.pid"), log: logs/$SERVICE.log)"
    done

    # ── 4. Résumé ───────────────────────────────────────────
    echo ""
    echo "=== [3/3] Résumé ==="
    sleep 3
    echo ""
    printf "%-25s %-8s %s\n" "Service" "Port" "État"
    printf "%-25s %-8s %s\n" "-------" "----" "----"

    # Docker
    for svc_port in "analytics-service:8090" "postgres:5400" "metabase:3001" "pgadmin:8082"; do
        svc="${svc_port%%:*}"; port="${svc_port##*:}"
        if port_in_use "$port"; then
            printf "%-25s %-8s ${GREEN}RUNNING${NC}\n" "$svc (Docker)" "$port"
        else
            printf "%-25s %-8s ${YELLOW}STARTING...${NC}\n" "$svc (Docker)" "$port"
        fi
    done

    # Maven
    for entry in "${MAVEN_SERVICES[@]}"; do
        svc="${entry%%:*}"; port="${entry##*:}"
        [ ! -d "$ROOT/$svc" ] && continue
        if port_in_use "$port"; then
            printf "%-25s %-8s ${GREEN}RUNNING${NC}\n" "$svc" "$port"
        else
            printf "%-25s %-8s ${YELLOW}STARTING...${NC}\n" "$svc" "$port"
        fi
    done

    echo ""
    echo "Les services Maven démarrent en arrière-plan."
    echo "Logs disponibles dans : logs/"
    echo ""
    echo "Attendre ~30s pour que tous les services soient prêts."
    echo "Tester le gateway : curl http://localhost:8080/actuator/health"
    echo ""
}

# ============================================================
# COMMANDE : stop
# ============================================================
cmd_stop() {
    echo ""
    echo "╔══════════════════════════════════════════════════════╗"
    echo "║         CiblOrgaSport — Arrêt des services           ║"
    echo "╚══════════════════════════════════════════════════════╝"

    # ── 1. Arrêt Maven via PIDs ─────────────────────────────
    echo ""
    echo "=== [1/2] Arrêt des microservices Maven ==="

    for entry in "${MAVEN_SERVICES[@]}"; do
        SERVICE="${entry%%:*}"
        PORT="${entry##*:}"
        PID_FILE="$LOG_DIR/$SERVICE.pid"
        STOPPED=false

        # Via fichier PID
        if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
            if kill -0 "$PID" 2>/dev/null; then
                kill -15 "$PID" 2>/dev/null || true
                sleep 1
                kill -9 "$PID" 2>/dev/null || true
                STOPPED=true
            fi
            rm -f "$PID_FILE"
        fi

        # Via port (fallback)
        if kill_port "$PORT"; then
            STOPPED=true
        fi

        if $STOPPED; then
            ok "$SERVICE arrêté."
        else
            info "$SERVICE : déjà arrêté."
        fi
    done

    # ── 2. Arrêt Docker ─────────────────────────────────────
    echo ""
    echo "=== [2/2] Arrêt des conteneurs Docker ==="
    docker compose stop
    ok "Docker arrêté."

    echo ""
    ok "Tous les services sont arrêtés."
    echo ""
}

# ============================================================
# COMMANDE : status
# ============================================================
cmd_status() {
    echo ""
    echo "=== État des services ==="
    echo ""
    printf "%-25s %-8s %s\n" "Service" "Port" "État"
    printf "%-25s %-8s %s\n" "-------" "----" "----"

    for svc_port in "analytics-service:8090" "postgres:5400" "metabase:3001" "pgadmin:8082" "kafka:9092"; do
        svc="${svc_port%%:*}"; port="${svc_port##*:}"
        if port_in_use "$port"; then
            printf "%-25s %-8s ${GREEN}RUNNING${NC}\n" "$svc (Docker)" "$port"
        else
            printf "%-25s %-8s ${RED}STOPPED${NC}\n" "$svc (Docker)" "$port"
        fi
    done

    for entry in "${MAVEN_SERVICES[@]}"; do
        svc="${entry%%:*}"; port="${entry##*:}"
        [ ! -d "$ROOT/$svc" ] && continue
        if port_in_use "$port"; then
            printf "%-25s %-8s ${GREEN}RUNNING${NC}\n" "$svc" "$port"
        else
            printf "%-25s %-8s ${RED}STOPPED${NC}\n" "$svc" "$port"
        fi
    done
    echo ""
}

# ============================================================
# POINT D'ENTRÉE
# ============================================================
case "${1:-}" in
    start)  cmd_start ;;
    stop)   cmd_stop ;;
    status) cmd_status ;;
    restart)
        cmd_stop
        sleep 2
        cmd_start
        ;;
    *)
        echo "Usage : $0 {start|stop|status|restart}"
        echo ""
        echo "  start   — Lance Docker + tous les microservices Maven"
        echo "  stop    — Arrête tous les services"
        echo "  status  — Affiche l'état de chaque service"
        echo "  restart — stop puis start"
        exit 1
        ;;
esac
