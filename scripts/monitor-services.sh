#!/bin/bash

echo "🔍 Monitoring des services CiblOrgaSport..."
echo "Appuyez sur Ctrl+C pour arrêter"

while true; do
    clear
    echo "📊 État des services - $(date)"
    echo "================================="
    
    # Vérifier chaque service
    services=("database" "api-server" "auth-service" "notification-service" "upload-service" "background-jobs")
    
    for service in "${services[@]}"; do
        if pgrep -f "$service" > /dev/null; then
            echo "✅ $service - ACTIF"
        else
            echo "❌ $service - ARRÊTÉ"
        fi
    done
    
    echo ""
    echo "🔄 Prochaine vérification dans 10 secondes..."
    sleep 10
done
