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

# Function to get default port for a service
get_default_port() {
  local service_name=$1
  case "$service_name" in
    "auth-service") echo "8081";;
    "event-service") echo "8082";;
    "billetterie") echo "8083";;
    "incident-service") echo "8084";;
    "gateway") echo "8080";;
    *) echo "8080";;
  esac
}

# Parse port from application.properties or application.yml
detect_port() {
  local service_dir=$1
  local service_name=$2
  local default_port
  default_port=$(get_default_port "$service_name")
  local prop_file="${service_dir}/src/main/resources/application.properties"
  local yml_file="${service_dir}/src/main/resources/application.yml"
  local port=""

  if [ -f "$prop_file" ]; then
    port=$(grep -E '^server\.port\s*=' "$prop_file" | tail -n1 | awk -F'=' '{gsub(/ /,"",$2);print $2}')
  fi
  if [ -z "$port" ] && [ -f "$yml_file" ]; then
    port=$(awk '/^server:/{flag=1} flag && /^\s*port:/{print $2; exit}' "$yml_file" | tr -d ' ')
  fi
  echo "${port:-$default_port}"
}

start_service() {
  local service_name=$1
  local service_dir=$2
  local jar_pattern=$3
  local wait_time=${4:-0}

  echo -e "${BLUE}📦 Construction: ${service_name}${NC}"
  
  # Check if service directory exists
  if [ ! -d "$service_dir" ]; then
    echo -e "${RED}❌ Répertoire introuvable: ${service_dir}${NC}"
    return 1
  fi

  # Build the service
  (cd "$service_dir" && mvn clean package -DskipTests -q) || {
    echo -e "${RED}❌ Build échoué pour ${service_name}${NC}"
    return 1
  }

  # Find JAR file (exclude .original files)
  local jar_file
  jar_file=$(find "$service_dir/target" -name "$jar_pattern" -type f ! -name "*.original" | head -n1)
  if [ -z "$jar_file" ]; then
    echo -e "${RED}❌ JAR introuvable pour ${service_name} (pattern: $jar_pattern)${NC}"
    ls -la "$service_dir/target/"*.jar 2>/dev/null || echo "Aucun JAR trouvé dans target/"
    return 1
  fi

  # Détection du port
  local port
  port=$(detect_port "$service_dir" "$service_name")

  [ $wait_time -gt 0 ] && sleep $wait_time

  local log_file="${LOG_DIR}/${service_name}.log"
  echo -e "${BLUE}🚀 Démarrage: ${service_name} sur le port ${YELLOW}${port}${NC}"
  
  # Start the service and properly capture PID
  cd "$service_dir"
  nohup java -jar "$jar_file" --server.port=${port} > "$log_file" 2>&1 &
  local pid=$!
  cd - > /dev/null

  echo $pid > "${LOG_DIR}/${service_name}.pid"
  
  # Wait and verify process started
  sleep 1
  if ps -p $pid > /dev/null 2>&1; then
    echo -e "${GREEN}✅ ${service_name} démarré (PID: $pid, Port: ${port}) | Logs: ${log_file}${NC}"
  else
    echo -e "${RED}❌ ${service_name} n'a pas pu démarrer (Port: ${port}) | Logs: ${log_file}${NC}"
    return 1
  fi
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

# Start database first
echo -e "${BLUE}🗄️  Démarrage de la base de données...${NC}"
docker-compose up -d postgres 2>/dev/null || echo -e "${YELLOW}⚠️  Docker compose non disponible${NC}"
sleep 3

# Service paths
AUTH_DIR="${ROOT_DIR}/auth-service"
BILL_DIR="${ROOT_DIR}/billetterie"
EVENT_DIR="${ROOT_DIR}/event-service"
INCIDENT_DIR="${ROOT_DIR}/incident-service"
GATEWAY_DIR="${ROOT_DIR}/gateway"

# Start services in parallel (gateway last)
echo -e "${BLUE}🚀 Démarrage des microservices...${NC}"

start_service "auth-service" "$AUTH_DIR" "auth-service-*.jar" 0 &
start_service "event-service" "$EVENT_DIR" "event-service-*.jar" 0 &
start_service "billetterie" "$BILL_DIR" "billetterie-*.jar" 0 &
start_service "incident-service" "$INCIDENT_DIR" "incident-service-*.jar" 0 &
start_service "abonnement-service" "${ROOT_DIR}/abonnement-service" "abonnement-service-*.jar" 0 &

# Wait for all background services to start
wait

# Start gateway after other services
start_service "gateway" "$GATEWAY_DIR" "gateway-*.jar" 5 &
wait

echo -e "${YELLOW}⏳ Vérification de l'état des services...${NC}"
sleep 8

services="auth-service event-service billetterie incident-service abonnement-service gateway"
all_running=true
echo -e "\n${BLUE}📊 État des services:${NC}"

for s in $services; do
  if check_service "$s"; then
    # Get port for display
    case "$s" in
      "auth-service") p=$(detect_port "$AUTH_DIR" "$s");;
      "event-service") p=$(detect_port "$EVENT_DIR" "$s");;
      "billetterie") p=$(detect_port "$BILL_DIR" "$s");;
      "incident-service") p=$(detect_port "$INCIDENT_DIR" "$s");;
      "gateway") p=$(detect_port "$GATEWAY_DIR" "$s");;
    esac
    echo -e "${GREEN}  ✅ $s: En cours (Port ${p})${NC}"
  else
    echo -e "${RED}  ❌ $s: Échec (voir logs/${s}.log)${NC}"
    all_running=false
  fi
done

if [ "$all_running" = true ]; then
  echo -e "\n${GREEN}🎉 Tous les services ont été démarrés avec succès!${NC}"
  echo -e "\n${BLUE}📋 URLs des services:${NC}"
  echo -e "${GREEN}  🔐 Auth Service: http://localhost:$(detect_port "$AUTH_DIR" "auth-service")/api/auth${NC}"
  echo -e "${GREEN}  🎪 Event Service: http://localhost:$(detect_port "$EVENT_DIR" "event-service")/api/events${NC}"
  echo -e "${GREEN}  🎫 Billetterie: http://localhost:$(detect_port "$BILL_DIR" "billetterie")/api/tickets${NC}"
  echo -e "${GREEN}  🚨 Incident Service: http://localhost:$(detect_port "$INCIDENT_DIR" "incident-service")/api/incidents${NC}"
  echo -e "${GREEN}  🌐 Gateway: http://localhost:$(detect_port "$GATEWAY_DIR" "gateway")${NC}"
else
  echo -e "\n${YELLOW}⚠️  Certains services ont échoué. Vérifiez les logs.${NC}"
fi

echo -e "\n${BLUE}📋 Logs: ${LOG_DIR}/[service].log${NC}"
echo -e "${BLUE}🛑 Pour arrêter: ./scripts/stop-all-services.sh${NC}"