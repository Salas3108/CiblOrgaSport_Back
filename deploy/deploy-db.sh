#!/bin/bash
# deploy-db.sh — Déploiement PostgreSQL sur vm-database
# Usage: ./deploy/deploy-db.sh <SSH_USER@VM_DATABASE_IP>
# Exemple: ./deploy/deploy-db.sh ubuntu@10.0.0.3
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
TARGET="${1:-ubuntu@<IP_VM_DATABASE>}"

echo "[1/4] Transfert des fichiers vers vm-database ($TARGET)..."
scp "$ROOT_DIR/docker-compose.prod.db.yml" "$TARGET:/home/ubuntu/docker-compose.prod.db.yml"
scp "$ROOT_DIR/.env.prod" "$TARGET:/home/ubuntu/.env.prod"
scp "$ROOT_DIR/init-db.sql" "$TARGET:/home/ubuntu/init-db.sql"

echo "[2/4] Démarrage de PostgreSQL..."
ssh "$TARGET" "cd /home/ubuntu && docker compose -f docker-compose.prod.db.yml --env-file .env.prod up -d"

echo "[3/4] Attente que PostgreSQL soit prêt..."
ssh "$TARGET" "timeout 60 bash -c 'until docker exec postgres pg_isready -q; do sleep 2; done'"

echo "[4/4] Vérification..."
ssh "$TARGET" "docker ps --filter name=postgres --format 'table {{.Names}}\t{{.Status}}'"

echo "PostgreSQL déployé sur $TARGET."
