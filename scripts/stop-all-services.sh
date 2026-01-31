#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${RED}🛑 Arrêt de tous les services CiblOrgaSport...${NC}"

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="${ROOT_DIR}/logs"

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="${LOG_DIR}/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        echo -e "${BLUE}🔄 Arrêt du service: ${service_name} (PID: $pid)${NC}"
        
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            sleep 2
            
            # Force kill if still running
            if kill -0 $pid 2>/dev/null; then
                kill -9 $pid
                echo -e "${YELLOW}⚠️  Force kill: ${service_name}${NC}"
            else
                echo -e "${GREEN}✅ ${service_name} arrêté${NC}"
            fi
        else
            echo -e "${RED}❌ ${service_name} n'était pas en cours d'exécution${NC}"
        fi
        
        rm -f $pid_file
    else
        echo -e "${RED}❌ Fichier PID introuvable pour: ${service_name}${NC}"
    fi
}

# Stop all services (reverse order)
stop_service "gateway"
stop_service "resultat-service"
stop_service "incident-service"
stop_service "billetterie"
stop_service "event-service"
stop_service "auth-service"

# Stop database
echo -e "${BLUE}🗄️  Arrêt de la base de données...${NC}"
docker-compose stop postgres

# Kill any remaining Java processes related to our services
echo -e "${BLUE}🧹 Nettoyage des processus Java restants...${NC}"
pkill -f "java.*ciblorgasport"
pkill -f "java.*incident-service"
pkill -f "java.*auth-service"
pkill -f "java.*event-service"
pkill -f "java.*billetterie"
pkill -f "java.*resultat-service"
pkill -f "java.*gateway"

echo -e "${GREEN}✅ Tous les services ont été arrêtés${NC}"
