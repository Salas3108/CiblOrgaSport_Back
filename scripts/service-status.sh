#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"

services=("auth-service" "event-service" "billetterie" "incident-service" "gateway")

echo -e "${BLUE}📊 État des services CiblOrgaSport:${NC}\n"

for service in "${services[@]}"; do
  pid_file="${LOG_DIR}/${service}.pid"
  if [ -f "$pid_file" ]; then
    pid=$(cat "$pid_file")
    if ps -p "$pid" > /dev/null 2>&1; then
      echo -e "${GREEN}✅ $service: En cours (PID: $pid)${NC}"
    else
      echo -e "${RED}❌ $service: Arrêté${NC}"
    fi
  else
    echo -e "${RED}❌ $service: Non démarré${NC}"
  fi
done

# Check database
echo ""
if docker ps | grep -q postgres; then
  echo -e "${GREEN}✅ Database: En cours${NC}"
else
  echo -e "${RED}❌ Database: Arrêtée${NC}"
fi
