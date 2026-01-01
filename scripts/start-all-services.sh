#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Démarrage de tous les services CiblOrgaSport...${NC}"

# Create logs directory if it doesn't exist
mkdir -p logs

# Function to start a service
start_service() {
    local service_name=$1
    local command=$2
    local log_file="logs/${service_name}.log"
    
    echo -e "${BLUE}📦 Démarrage du service: ${service_name}${NC}"
    
    # Start service in background and redirect output to log file
    nohup $command > $log_file 2>&1 &
    local pid=$!
    
    # Save PID for later management
    echo $pid > "logs/${service_name}.pid"
    
    echo -e "${GREEN}✅ ${service_name} démarré (PID: $pid)${NC}"
    sleep 2
}

# Start database service
start_service "database" "npm run db:start"

# Wait for database to be ready
echo -e "${BLUE}⏳ Attente de la base de données...${NC}"
sleep 5

# Start API server
start_service "api-server" "npm run start:dev"

# Start authentication service
start_service "auth-service" "npm run auth:start"

# Start notification service
start_service "notification-service" "npm run notifications:start"

# Start file upload service
start_service "upload-service" "npm run upload:start"

# Start background jobs
start_service "background-jobs" "npm run jobs:start"

echo -e "${GREEN}🎉 Tous les services ont été démarrés avec succès!${NC}"
echo -e "${BLUE}📋 Vérifiez les logs dans le dossier 'logs/'${NC}"
echo -e "${BLUE}🛑 Pour arrêter tous les services: ./scripts/stop-all-services.sh${NC}"

# Display running services
echo -e "\n${BLUE}📊 Services en cours d'exécution:${NC}"
ps aux | grep -E "(npm|node)" | grep -v grep | while read line; do
    echo -e "${GREEN}  → $line${NC}"
done

# Ajoutez cette section pour surveiller les services
echo "🔄 Surveillance des services (Ctrl+C pour arrêter)..."
while true; do
    sleep 30
    echo "📊 $(date): Services en cours d'exécution"
    ps aux | grep -E "(database|api-server|auth-service|notification-service|upload-service|background-jobs)" | grep -v grep | wc -l | xargs echo "   → Nombre de services actifs:"
done
