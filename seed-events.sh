#!/bin/bash
# ============================================================
# seed-events.sh — Génération d'événements réels via le gateway
# Appelle le vrai gateway (port 8080) pour que l'AnalyticsFilter
# capture tout automatiquement dans event_log
# Usage : bash seed-events.sh
# ============================================================

set -e

GATEWAY="http://localhost:8080"
ANALYTICS="http://localhost:8090"

echo ""
echo "=== seed-events.sh : génération d'événements réels ==="
echo ""

# ─── Vérification gateway ──────────────────────────────────
if ! curl -sf "$GATEWAY/actuator/health" > /dev/null 2>&1; then
    echo "ERREUR : Gateway non disponible sur $GATEWAY"
    echo "Démarrez le gateway avant de lancer ce script."
    exit 1
fi
echo "Gateway disponible."

# ─── Helpers ──────────────────────────────────────────────
login() {
    local username="$1"
    local password="$2"
    local token
    token=$(curl -sf -X POST "$GATEWAY/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$username\",\"password\":\"$password\"}" \
        2>/dev/null | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('token','') or d.get('accessToken','') or d.get('jwt',''))" 2>/dev/null || true)
    echo "$token"
}

get_auth() {
    local token="$1"
    local url="$2"
    curl -sf -H "Authorization: Bearer $token" "$url" > /dev/null 2>&1 || true
}

post_auth() {
    local token="$1"
    local url="$2"
    local body="$3"
    curl -sf -X POST "$url" \
        -H "Authorization: Bearer $token" \
        -H "Content-Type: application/json" \
        -d "$body" > /dev/null 2>&1 || true
}

echo ""
echo "--- Connexions (USER_LOGIN) ---"

# Login admin → USER_LOGIN
ADMIN_TOKEN=$(login "admin" "admin123")
if [ -z "$ADMIN_TOKEN" ]; then
    echo "  AVERTISSEMENT : Login admin échoué (utilisateur inexistant ou mauvais mdp)"
    echo "  Les événements admin ne seront pas générés."
else
    echo "  Admin connecté."
fi

# Login athlete
ATHLETE_TOKEN=$(login "leon.marchand" "athlete123")
if [ -z "$ATHLETE_TOKEN" ]; then
    ATHLETE_TOKEN=$(login "athlete1" "athlete123")
fi
if [ -z "$ATHLETE_TOKEN" ]; then
    echo "  AVERTISSEMENT : Login athlete échoué."
else
    echo "  Athlète connecté."
fi

# Login commissaire
COMMISSAIRE_TOKEN=$(login "commissaire1" "commissaire123")
if [ -z "$COMMISSAIRE_TOKEN" ]; then
    echo "  AVERTISSEMENT : Login commissaire échoué."
else
    echo "  Commissaire connecté."
fi

# Login spectateur
SPECTATEUR_TOKEN=$(login "user1" "user123")
if [ -z "$SPECTATEUR_TOKEN" ]; then
    SPECTATEUR_TOKEN=$(login "spectateur1" "password123")
fi
if [ -z "$SPECTATEUR_TOKEN" ]; then
    echo "  AVERTISSEMENT : Login spectateur échoué."
else
    echo "  Spectateur connecté."
fi

echo ""
echo "--- Vues compétitions (COMPETITION_VIEW) ---"

for comp_id in 1 2 3 4 5; do
    [ -n "$ADMIN_TOKEN" ]       && get_auth "$ADMIN_TOKEN"       "$GATEWAY/competitions/$comp_id"
    [ -n "$ATHLETE_TOKEN" ]     && get_auth "$ATHLETE_TOKEN"     "$GATEWAY/events/$comp_id"
    [ -n "$SPECTATEUR_TOKEN" ]  && get_auth "$SPECTATEUR_TOKEN"  "$GATEWAY/competitions/$comp_id"
done
echo "  Vues compétitions générées."

echo ""
echo "--- Vues épreuves / résultats (RESULT_VIEW) ---"

for epreuve_id in 45 46 47 48 49; do
    [ -n "$ADMIN_TOKEN" ]       && get_auth "$ADMIN_TOKEN"       "$GATEWAY/epreuves/$epreuve_id"
    [ -n "$COMMISSAIRE_TOKEN" ] && get_auth "$COMMISSAIRE_TOKEN" "$GATEWAY/epreuves/$epreuve_id"
    [ -n "$ATHLETE_TOKEN" ]     && get_auth "$ATHLETE_TOKEN"     "$GATEWAY/epreuves/$epreuve_id"
    [ -n "$SPECTATEUR_TOKEN" ]  && get_auth "$SPECTATEUR_TOKEN"  "$GATEWAY/epreuves/$epreuve_id"
done
echo "  Vues épreuves générées."

echo ""
echo "--- Profils athlètes (ATHLETE_PROFILE_VIEW) ---"

for athlete_id in 1 2 3 4 5; do
    [ -n "$ADMIN_TOKEN" ]      && get_auth "$ADMIN_TOKEN"      "$GATEWAY/athlete/$athlete_id"
    [ -n "$SPECTATEUR_TOKEN" ] && get_auth "$SPECTATEUR_TOKEN" "$GATEWAY/api/athlete/$athlete_id"
done
echo "  Profils athlètes générés."

echo ""
echo "--- Fan zones (FANZONE_VIEW) ---"

curl -sf "$GATEWAY/api/geo/fanzones" > /dev/null 2>&1 || true
curl -sf "$GATEWAY/api/geo/fanzones/nearby?lat=43.2965&lng=5.3698&rayon=2000" > /dev/null 2>&1 || true
echo "  Fan zones générées."

echo ""
echo "--- Notifications (NOTIFICATION_SENT / NOTIFICATION_SUBSCRIBED) ---"

if [ -n "$ADMIN_TOKEN" ]; then
    post_auth "$ADMIN_TOKEN" "$GATEWAY/notifications/" \
        '{"titre":"Test seeding","message":"Notification de test pour analytics","type":"INFO"}'
fi

for i in 1 2 3; do
    [ -n "$SPECTATEUR_TOKEN" ] && post_auth "$SPECTATEUR_TOKEN" "$GATEWAY/abonnements/" \
        "{\"competitionId\":$i,\"type\":\"EMAIL\"}" || true
    [ -n "$SPECTATEUR_TOKEN" ] && post_auth "$SPECTATEUR_TOKEN" "$GATEWAY/api/abonnements/subscribe" \
        "{\"competitionId\":$i}" || true
done
echo "  Notifications générées."

echo ""
echo "--- Incidents (INCIDENT_DECLARED) ---"

if [ -n "$ADMIN_TOKEN" ]; then
    post_auth "$ADMIN_TOKEN" "$GATEWAY/incidents/" \
        '{"description":"Incident technique test seeding","type":"TECHNIQUE","severite":"MINEURE"}'
    post_auth "$ADMIN_TOKEN" "$GATEWAY/api/incidents/" \
        '{"description":"Incident test","type":"SECURITE","severite":"MAJEURE"}'
fi
if [ -n "$COMMISSAIRE_TOKEN" ]; then
    post_auth "$COMMISSAIRE_TOKEN" "$GATEWAY/incidents/" \
        '{"description":"Incident commissaire test","type":"SPORTIF","severite":"MINEURE"}'
fi
echo "  Incidents générés."

echo ""
echo "--- Pages générales (PAGE_VIEW) ---"

for url in "/events" "/lieux" "/competitions" "/admin/dashboard"; do
    [ -n "$ADMIN_TOKEN" ] && get_auth "$ADMIN_TOKEN" "$GATEWAY$url" || true
done
echo "  Pages générales générées."

echo ""
echo "=== Tous les événements réels ont été envoyés au gateway. ==="
echo "    L'AnalyticsFilter les a capturés dans event_log."
echo ""
