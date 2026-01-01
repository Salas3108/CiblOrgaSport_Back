#!/bin/bash

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}🚀 Démarrage simultané de tous les services CiblOrgaSport (Spring Boot)...${NC}"

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"
mkdir -p "$LOG_DIR"
rm -f "$LOG_DIR"/*.pid

# Ports par défaut si non définis dans les fichiers
declare -A DEFAULT_PORTS=(
  ["auth-service"]=8081
  ["event-service"]=8082
  ["billetterie"]=8083
  ["gateway"]=8080
)

# Parse port from application.properties or application.yml
detect_port() {
  local service_dir=$1
  local default_port=$2
  local prop_file="${service_dir}/src/main/resources/application.properties"
  local yml_file="${service_dir}/src/main/resources/application.yml"
  local port=""

  if [ -f "$prop_file" ]; then
    port=$(grep -E '^server\.port\s*=' "$prop_file" | tail -n1 | awk -F'=' '{gsub(/ /,"",$2);print $2}')
  fi
  if [ -z "$port" ] && [ -f "$yml_file" ]; then
    port=$(grep -E '^\s*port:\s*[0-9]+' "$yml_file" | grep -i 'server' -A0 | awk -F':' '{gsub(/ /,"",$2);print $2}' | tail -n1)
  fi
  echo "${port:-$default_port}"
}

start_service() {
  local service_name=$1
  local service_dir=$2
  local jar_pattern=$3
  local wait_time=${4:-0}

  # Détection du port
  local port
  port=$(detect_port "$service_dir" "${DEFAULT_PORTS[$service_name]}")

  echo -e "${BLUE}📦 Construction: ${service_name}${NC}"
  (cd "$service_dir" && mvn -q -DskipTests package) || {
    echo -e "${RED}❌ Build échoué pour ${service_name}${NC}"
    return 1
  }

  local jar_file
  jar_file=$(ls -1 "$service_dir"/target/$jar_pattern 2>/dev/null | head -n1)
  if [ -z "$jar_file" ]; then
    echo -e "${RED}❌ JAR introuvable pour ${service_name} (pattern: $jar_pattern)${NC}"
    return 1
  fi

  [ $wait_time -gt 0 ] && sleep $wait_time

  local log_file="${LOG_DIR}/${service_name}.log"
  echo -e "${BLUE}🚀 Démarrage: ${service_name} sur le port ${YELLOW}${port}${NC}"
  # --server.port prend la priorité sur application.properties/yml
  (cd "$service_dir" && nohup java -jar "$jar_file" --server.port=${port} > "$log_file" 2>&1 &)
  local pid=$!

  echo $pid > "${LOG_DIR}/${service_name}.pid"
  echo -e "${GREEN}✅ ${service_name} démarré (PID: $pid, Port: ${port}) | Logs: ${log_file}${NC}"
}

check_service() {
  local service_name=$1
  local pid_file="${LOG_DIR}/${service_name}.pid"
  if [ -f "$pid_file" ]; then
    local pid
    pid=$(cat "$pid_file")
    if ps -p "$pid" > /dev/null 2>&1; then
      return 0
    fi
  fi
  return 1
}

# Paths
AUTH_DIR="${ROOT_DIR}/auth-service"
BILL_DIR="${ROOT_DIR}/billetterie"
EVENT_DIR="${ROOT_DIR}/event-service"
GATEWAY_DIR="${ROOT_DIR}/gateway"

# Start in parallel (gateway after 3s)
start_service "auth-service" "$AUTH_DIR" "auth-service-*.jar" 0 &
start_service "event-service" "$EVENT_DIR" "event-service-*.jar" 0 &
start_service "billetterie" "$BILL_DIR" "billetterie-*.jar" 0 &
start_service "gateway" "$GATEWAY_DIR" "gateway-*.jar" 3 &

wait

echo -e "${YELLOW}⏳ Vérification de l'état des services...${NC}"
sleep 5

services=("auth-service" "event-service" "billetterie" "gateway")
all_running=true
echo -e "\n${BLUE}📊 État des services:${NC}"
for s in "${services[@]}"; do
  if check_service "$s"; then
    # Ré-afficher le port détecté pour cohérence
    case "$s" in
      "auth-service") p=$(detect_port "$AUTH_DIR" "${DEFAULT_PORTS[$s]}");;
      "event-service") p=$(detect_port "$EVENT_DIR" "${DEFAULT_PORTS[$s]}");;
      "billetterie") p=$(detect_port "$BILL_DIR" "${DEFAULT_PORTS[$s]}");;
      "gateway") p=$(detect_port "$GATEWAY_DIR" "${DEFAULT_PORTS[$s]}");;
    esac
    echo -e "${GREEN}  ✅ $s: En cours (Port ${p})${NC}"
  else
    echo -e "${RED}  ❌ $s: Échec (voir logs/${s}.log)${NC}"
    all_running=false
  fi
done

if [ "$all_running" = true ]; then
  echo -e "\n${GREEN}🎉 Tous les services ont été démarrés avec succès!${NC}"
else
  echo -e "\n${YELLOW}⚠️  Certains services ont échoué. Vérifiez les logs.${NC}"
fi

echo -e "${BLUE}📋 Logs: ${LOG_DIR}/[service].log${NC}"
echo -e "${BLUE}🛑 Pour arrêter: ./scripts/stop-all-services.sh${NC}"