#!/bin/bash
# setup-vm.sh — Installation de Docker sur Ubuntu 22.04
# Usage: ssh ubuntu@<VM_IP> 'bash -s' < deploy/setup-vm.sh
set -e

echo "[1/4] Mise à jour des paquets..."
sudo apt-get update -y
sudo apt-get upgrade -y

echo "[2/4] Installation de Docker..."
curl -fsSL https://get.docker.com | sudo sh

echo "[3/4] Ajout de l'utilisateur ubuntu au groupe docker..."
sudo usermod -aG docker ubuntu

echo "[4/4] Vérification de l'installation..."
docker --version
docker compose version

echo "Docker installé avec succès. Déconnectez-vous et reconnectez-vous pour activer les permissions docker."
