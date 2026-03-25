#!/bin/bash
# deploy-services.sh — Déploiement Kafka + microservices sur vm-services
# Usage: ./deploy/deploy-services.sh <SSH_USER@VM_SERVICES_IP>
# Exemple: ./deploy/deploy-services.sh ubuntu@10.0.0.2
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
TARGET="${1:-ubuntu@<IP_VM_SERVICES>}"

echo "[1/4] Transfert des fichiers vers vm-services ($TARGET)..."
scp "$ROOT_DIR/docker-compose.prod.services.yml" "$TARGET:/home/ubuntu/docker-compose.prod.services.yml"
scp "$ROOT_DIR/.env.prod" "$TARGET:/home/ubuntu/.env.prod"

echo "[2/4] Pull des images depuis Docker Hub..."
ssh "$TARGET" "cd /home/ubuntu && docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull"

echo "[3/4] Démarrage des services..."
ssh "$TARGET" "cd /home/ubuntu && docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d"

echo "[4/4] Statut des conteneurs..."
ssh "$TARGET" "docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'"

echo "Services déployés sur $TARGET."
echo "Suivre les logs: ssh $TARGET 'docker compose -f docker-compose.prod.services.yml logs -f --tail=50'"
