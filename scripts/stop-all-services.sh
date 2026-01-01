#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${RED}🛑 Arrêt de tous les services CiblOrgaSport...${NC}"

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        echo -e "${BLUE}🔄 Arrêt du service: ${service_name} (PID: $pid)${NC}"
        
        if kill -0 $pid 2>/dev/null; then
            kill $pid
            sleep 2
            
            # Force kill if still running
            if kill -0 $pid 2>/dev/null; then
                kill -9 $pid
                echo -e "${RED}⚠️  Force kill: ${service_name}${NC}"
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

# Stop all services
stop_service "background-jobs"
stop_service "upload-service" 
stop_service "notification-service"
stop_service "auth-service"
stop_service "api-server"
stop_service "database"

# Kill any remaining node/npm processes
echo -e "${BLUE}🧹 Nettoyage des processus restants...${NC}"
pkill -f "npm"
pkill -f "node.*CiblOrgaSport"

echo -e "${GREEN}✅ Tous les services ont été arrêtés${NC}"
