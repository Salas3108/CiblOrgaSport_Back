#!/bin/bash
# health-check.sh — Vérification post-déploiement de tous les services
# Usage: ./deploy/health-check.sh <GATEWAY_PUBLIC_IP>
# Exemple: ./deploy/health-check.sh 51.X.X.X

GATEWAY_IP="${1:-localhost}"
BASE_URL="http://$GATEWAY_IP"
PASS=0
FAIL=0

check() {
  local name="$1"
  local url="$2"
  local http_code
  http_code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 10 "$url" 2>/dev/null)
  # 200 = OK, 401/403 = service UP but secured by JWT (expected in prod)
  if [ "$http_code" = "200" ] || [ "$http_code" = "401" ] || [ "$http_code" = "403" ]; then
    echo "  OK     [$http_code] $name"
    PASS=$((PASS + 1))
  else
    echo "  ECHEC  [$http_code] $name ($url)"
    FAIL=$((FAIL + 1))
  fi
}

echo "═══════════════════════════════════════════════════════"
echo " Health Check — CiblOrgaSport Production"
echo " Gateway: $BASE_URL"
echo "═══════════════════════════════════════════════════════"
echo ""

echo "[ Nginx ]"
check "nginx" "$BASE_URL/nginx-health"

echo ""
echo "[ Spring Cloud Gateway ]"
check "gateway /actuator/health" "$BASE_URL/actuator/health"

echo ""
echo "[ Microservices (via gateway /actuator/health) ]"
check "auth-service"           "$BASE_URL/auth/actuator/health"
check "event-service"          "$BASE_URL/events/actuator/health"
check "lieu-service"           "$BASE_URL/lieux/actuator/health"
check "billetterie"            "$BASE_URL/billets/actuator/health"
check "abonnement-service"     "$BASE_URL/api/abonnements/actuator/health"
check "incident-service"       "$BASE_URL/incidents/actuator/health"
check "participants-service"   "$BASE_URL/athlete/actuator/health"
check "resultats-service"      "$BASE_URL/resultats/actuator/health"
check "notifications-service"  "$BASE_URL/notifications/actuator/health"
check "volunteer-service"      "$BASE_URL/api/v1/volunteers/actuator/health"

echo ""
echo "═══════════════════════════════════════════════════════"
echo " Résultat : $PASS OK / $FAIL ECHEC"
echo "═══════════════════════════════════════════════════════"

[ $FAIL -eq 0 ] && exit 0 || exit 1
