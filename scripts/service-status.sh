#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}📊 État des services CiblOrgaSport${NC}"
echo "=================================="

# Function to check service status
check_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat $pid_file)
        if kill -0 $pid 2>/dev/null; then
            echo -e "${GREEN}✅ ${service_name}: En cours (PID: $pid)${NC}"
        else
            echo -e "${RED}❌ ${service_name}: Arrêté (PID obsolète)${NC}"
            rm -f $pid_file
        fi
    else
        echo -e "${RED}❌ ${service_name}: Non démarré${NC}"
    fi
}

# Check all services
check_service "database"
check_service "api-server"
check_service "auth-service"
check_service "notification-service"
check_service "upload-service"
check_service "background-jobs"

echo ""
echo -e "${YELLOW}📋 Processus Node.js actifs:${NC}"
ps aux | grep -E "(npm|node)" | grep -v grep || echo -e "${RED}Aucun processus Node.js trouvé${NC}"
