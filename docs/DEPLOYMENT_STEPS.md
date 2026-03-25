# Étapes de déploiement — CiblOrgaSport

Référence rapide pour les développeurs. Pour un guide complet destiné aux clients, voir [DEPLOYMENT_GUIDE_CLIENT.md](./DEPLOYMENT_GUIDE_CLIENT.md).

---

## Pré-requis

- 1 VM OVH Ubuntu 22.04+ (minimum 8 Go RAM, recommandé 16 Go)
- Docker Hub : compte avec accès en écriture
- Clé SSH configurée vers la VM
- Fichier `.env.prod` rempli (voir `.env.prod.example`)

---

## Étape 1 — Installer Docker sur la VM

```bash
ssh ubuntu@<IP_VM>
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker ubuntu
# Se déconnecter et se reconnecter pour activer le groupe docker
exit
ssh ubuntu@<IP_VM>
docker --version  # vérification
```

---

## Étape 2 — Builder et pousser les images Docker

Depuis votre machine locale, à la racine du projet :

```bash
docker login -u <DOCKERHUB_USERNAME>
./deploy/build-and-push.sh
```

Ce script construit les 13 images et les pousse sur Docker Hub sous `<DOCKERHUB_USERNAME>/ciblorgasport-*:latest`.

---

## Étape 3 — Remplir .env.prod

```bash
cp .env.prod.example .env.prod
# Éditer avec vos valeurs :
#   POSTGRES_PASSWORD=<mot de passe fort>
#   JWT_SECRET=$(openssl rand -hex 64)
#   KAFKA_CLUSTER_ID=$(docker run --rm confluentinc/cp-kafka:7.7.0 kafka-storage random-uuid)
#   DOCKERHUB_USERNAME=<votre compte>
#   DB_HOST=postgres   ← ne pas changer, c'est le nom du service Docker
```

---

## Étape 4 — Déployer les microservices

```bash
# Copier les fichiers vers la VM
scp docker-compose.prod.services.yml .env.prod ubuntu@<IP_VM>:~/

# Se connecter et lancer
ssh ubuntu@<IP_VM>
docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d

# Vérifier que tout est Up
docker ps --format "table {{.Names}}\t{{.Status}}"
```

---

## Étape 5 — Installer et configurer Nginx

```bash
ssh ubuntu@<IP_VM>
sudo apt-get install -y nginx

sudo tee /etc/nginx/sites-available/glop > /dev/null << 'EOF'
upstream gateway {
    server 127.0.0.1:8080;
}

limit_req_zone $binary_remote_addr zone=api:10m rate=30r/s;

server {
    listen 80;
    server_name _;

    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;

    location /nginx-health {
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    location / {
        limit_req zone=api burst=50 nodelay;
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 60s;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/glop /etc/nginx/sites-enabled/glop
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl enable nginx && sudo systemctl reload nginx
```

---

## Étape 6 — Vérification

```bash
# Depuis votre machine locale :
./deploy/health-check.sh <IP_VM>

# Résultat attendu : 12 OK / 0 ECHEC
```

---

## Commandes utiles en production

```bash
# Voir tous les conteneurs
ssh ubuntu@<IP_VM> 'docker ps'

# Logs d'un service
ssh ubuntu@<IP_VM> 'docker logs auth-service -f --tail=50'

# Redémarrer un service
ssh ubuntu@<IP_VM> 'docker compose -f docker-compose.prod.services.yml --env-file .env.prod restart auth-service'

# Mettre à jour une image (ex: gateway)
docker build -t <HUB>/ciblorgasport-gateway:latest ./gateway/
docker push <HUB>/ciblorgasport-gateway:latest
ssh ubuntu@<IP_VM> 'docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull gateway && docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d gateway'

# Topics Kafka
ssh ubuntu@<IP_VM> 'docker exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092'

# Connexion PostgreSQL
ssh ubuntu@<IP_VM> 'docker exec -it postgres psql -U admin -d glop'

# Arrêter toute la stack
ssh ubuntu@<IP_VM> 'docker compose -f docker-compose.prod.services.yml --env-file .env.prod down'
```

---

## Activation SSL (après configuration d'un domaine)

1. Pointer le DNS du domaine vers `<IP_VM>`
2. Exécuter sur la VM :

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d votre-domaine.fr --non-interactive --agree-tos -m admin@votre-domaine.fr
```

Certbot renouvelle automatiquement le certificat tous les 90 jours.

---

## Schéma d'architecture final

```
Internet :80
    |
   Nginx (vm-services, 137.74.133.131)
    |
   Spring Cloud Gateway :8080
    |
   +-- auth-service :8081      +-- lieu-service :8090
   +-- billetterie :8083       +-- geolocation-service :8091
   +-- event-service :8084     +-- analytics-service :8092
   +-- abonnement-service :8085 +-- volunteer-service :8093
   +-- incident-service :8086  +-- PostgreSQL :5432 (interne)
   +-- participants-service :8087 +-- Kafka :9092 (interne)
   +-- resultats-service :8088
   +-- notifications-service :8089
```

Tout tourne dans le réseau Docker `prod-net`. Seul le port 80 est ouvert sur Internet.
