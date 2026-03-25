#!/bin/bash
# deploy-gateway.sh — Installation Nginx sur vm-gateway
# Usage: ./deploy/deploy-gateway.sh <SSH_USER@VM_GATEWAY_IP> <IP_PRIVEE_VM_SERVICES>
# Exemple: ./deploy/deploy-gateway.sh ubuntu@51.X.X.X 10.0.0.2
set -e

TARGET="${1:-ubuntu@<IP_VM_GATEWAY>}"
PRIV_SERVICES="${2:-<IP_PRIVEE_VM_SERVICES>}"
NGINX_CONF="$(dirname "$0")/nginx.conf"

echo "[1/4] Remplacement du placeholder <PRIV_SERVICES> → $PRIV_SERVICES..."
TMP_CONF=$(mktemp)
sed "s/<PRIV_SERVICES>/$PRIV_SERVICES/g" "$NGINX_CONF" > "$TMP_CONF"

echo "[2/4] Installation de Nginx sur $TARGET..."
ssh "$TARGET" "sudo apt-get update -y && sudo apt-get install -y nginx"

echo "[3/4] Transfert et activation de la config Nginx..."
scp "$TMP_CONF" "$TARGET:/tmp/glop.conf"
ssh "$TARGET" "sudo cp /tmp/glop.conf /etc/nginx/sites-available/glop && \
               sudo ln -sf /etc/nginx/sites-available/glop /etc/nginx/sites-enabled/glop && \
               sudo rm -f /etc/nginx/sites-enabled/default && \
               sudo nginx -t && \
               sudo systemctl reload nginx"

rm -f "$TMP_CONF"

echo "[4/4] Vérification Nginx..."
ssh "$TARGET" "sudo systemctl is-active nginx && curl -sf http://localhost/nginx-health || echo 'WARNING: health check échoué'"

echo "Nginx déployé sur $TARGET → proxy vers vm-services ($PRIV_SERVICES:8080)"
echo ""
echo "Pour activer SSL (après configuration du domaine) :"
echo "  ssh $TARGET 'sudo apt install -y certbot python3-certbot-nginx && sudo certbot --nginx -d <VOTRE_DOMAINE>'"
