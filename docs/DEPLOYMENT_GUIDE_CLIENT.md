# Guide de déploiement complet — CiblOrgaSport

## Pour le client / administrateur système

Ce guide vous accompagne pas à pas pour déployer l'application CiblOrgaSport en production, depuis la création des comptes jusqu'à la vérification finale. Aucune connaissance préalable de Docker n'est requise pour suivre ce guide.

---

## Table des matières

1. [Vue d'ensemble de l'architecture](#1-vue-densemble-de-larchitecture)
2. [Pré-requis et comptes à créer](#2-pré-requis-et-comptes-à-créer)
3. [Provisionner la VM OVH](#3-provisionner-la-vm-ovh)
4. [Configurer l'accès SSH](#4-configurer-laccès-ssh)
5. [Installer Docker sur la VM](#5-installer-docker-sur-la-vm)
6. [Configurer les secrets de production](#6-configurer-les-secrets-de-production)
7. [Builder et publier les images Docker](#7-builder-et-publier-les-images-docker)
8. [Déployer l'application](#8-déployer-lapplication)
9. [Configurer Nginx (reverse proxy)](#9-configurer-nginx-reverse-proxy)
10. [Vérifier que tout fonctionne](#10-vérifier-que-tout-fonctionne)
11. [Activer HTTPS avec un nom de domaine](#11-activer-https-avec-un-nom-de-domaine)
12. [Maintenance et opérations courantes](#12-maintenance-et-opérations-courantes)
13. [Résolution de problèmes](#13-résolution-de-problèmes)

---

## 1. Vue d'ensemble de l'architecture

L'application CiblOrgaSport est composée de **15 conteneurs Docker** qui tournent sur **une seule VM** :

```
Internet
    |
    | Port 80 (HTTP) ou 443 (HTTPS)
    |
+---+----------------------------+
|         VM OVH (Ubuntu)        |
|                                |
|  [Nginx]                       |  ← Point d'entrée unique
|     |                          |
|  [Spring Cloud Gateway :8080]  |  ← Routeur
|     |                          |
|  [13 microservices Spring Boot] |  ← Logique métier
|  [PostgreSQL :5432]            |  ← Base de données
|  [Kafka :9092]                 |  ← Messagerie interne
+--------------------------------+
```

Les utilisateurs n'accèdent qu'au port 80 (ou 443 si HTTPS). Tous les autres ports sont internes à la VM et invisibles depuis Internet.

---

## 2. Pré-requis et comptes à créer

Avant de commencer, vous devez disposer des éléments suivants. Prévoyez 30 à 60 minutes pour cette étape.

### 2.1 Compte OVH Cloud

1. Rendez-vous sur [ovhcloud.com](https://www.ovhcloud.com/fr/)
2. Créez un compte ou connectez-vous
3. Activez le **Public Cloud** (menu principal → Public Cloud)
4. Ajoutez un moyen de paiement (carte bancaire)

### 2.2 Compte Docker Hub

Docker Hub est le registre qui stocke les images Docker de l'application.

1. Rendez-vous sur [hub.docker.com](https://hub.docker.com/)
2. Créez un compte (bouton "Sign Up")
3. Notez votre nom d'utilisateur (exemple : `monentreprise`)
4. Créez un **Personal Access Token** pour l'authentification :
   - Cliquez sur votre avatar → Account Settings
   - Menu "Security" → "New Access Token"
   - Nom : `ciblorgasport-deploy`
   - Permissions : **Read & Write**
   - Copiez le token et conservez-le précieusement (il ne s'affiche qu'une fois)

### 2.3 Machine locale avec Docker Desktop

Les images Docker sont construites sur votre machine locale et envoyées sur Docker Hub. Vous devez avoir Docker Desktop installé :

- **Windows / Mac** : téléchargez [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Linux** : exécutez `curl -fsSL https://get.docker.com | sh`

Vérifiez l'installation :
```bash
docker --version
# Docker version 24.x.x ou plus récent attendu
```

---

## 3. Provisionner la VM OVH

### 3.1 Créer la VM

1. Dans l'interface OVH Public Cloud, cliquez sur **"Créer une instance"**
2. Choisissez la configuration suivante :

| Paramètre | Valeur recommandée |
|-----------|-------------------|
| **Modèle** | B2-15 ou B3-30 (16 Go RAM minimum) |
| **Région** | Gravelines (GRA) ou Roubaix (RBX) |
| **Image** | Ubuntu 22.04 LTS ou Ubuntu 24.04 LTS |
| **Clé SSH** | Ajouter votre clé (voir section 4) |
| **Réseau** | Ext-Net (IP publique) |

> **Pourquoi 16 Go de RAM ?** L'application fait tourner 15 conteneurs simultanément. En dessous de 8 Go, certains services risquent de manquer de mémoire.

3. Cliquez sur **"Créer une instance"**
4. Attendez que le statut passe à **"Activée"**
5. Notez l'**adresse IPv4 publique** (exemple : `137.74.133.131`)

### 3.2 Ouvrir le port 80 (Security Group)

Par défaut, OVH bloque tous les ports sauf le 22 (SSH). Vous devez ouvrir le port 80 :

1. Dans OVH Public Cloud → **Réseau → Security Groups**
2. Cliquez sur le security group associé à votre instance
3. Onglet **"Règles entrantes"** → **"Ajouter une règle"**
4. Remplissez :
   - Protocole : **TCP**
   - Port : **80**
   - Source : **0.0.0.0/0** (Internet entier)
5. Répétez pour le port **443** si vous souhaitez activer HTTPS plus tard
6. Validez

---

## 4. Configurer l'accès SSH

SSH est le protocole qui vous permet de vous connecter à la VM pour l'administrer.

### 4.1 Générer une clé SSH (si vous n'en avez pas)

Sur votre machine locale :

```bash
# Sur Mac / Linux :
ssh-keygen -t ed25519 -C "ciblorgasport-deploy"
# Appuyez sur Entrée pour accepter les valeurs par défaut
# La clé publique est dans : ~/.ssh/id_ed25519.pub

# Sur Windows (PowerShell) :
ssh-keygen -t ed25519 -C "ciblorgasport-deploy"
```

### 4.2 Ajouter la clé SSH dans OVH

1. Dans OVH Public Cloud → **Clés SSH** → **"Ajouter une clé SSH"**
2. Nom : `deploy-key`
3. Contenu : copiez le contenu de `~/.ssh/id_ed25519.pub`

```bash
# Pour afficher votre clé publique :
cat ~/.ssh/id_ed25519.pub
```

### 4.3 Tester la connexion SSH

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>
# Exemple : ssh ubuntu@137.74.133.131

# Si la connexion réussit, vous verrez :
# Welcome to Ubuntu 22.04.x LTS ...
# ubuntu@<nom-vm>:~$
```

Tapez `exit` pour vous déconnecter.

---

## 5. Installer Docker sur la VM

Connectez-vous à la VM et exécutez ces commandes une par une :

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Installer Docker
curl -fsSL https://get.docker.com | sudo sh

# Ajouter l'utilisateur ubuntu au groupe docker
# (permet d'utiliser docker sans sudo)
sudo usermod -aG docker ubuntu

# Se déconnecter et se reconnecter pour activer le changement
exit
```

Reconnectez-vous et vérifiez :

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>
docker --version
docker compose version
# Docker version 24.x.x ...
# Docker Compose version v2.x.x ...
```

Si les deux commandes affichent un numéro de version, Docker est correctement installé.

---

## 6. Configurer les secrets de production

Les secrets sont les informations sensibles de l'application : mots de passe, clés de chiffrement, etc.

### 6.1 Sur votre machine locale

Dans le dossier du projet CiblOrgaSport_Back :

```bash
# Copier le fichier exemple
cp .env.prod.example .env.prod
```

Ouvrez `.env.prod` avec un éditeur de texte et remplissez chaque valeur :

```dotenv
# Nom du service Docker PostgreSQL — NE PAS CHANGER
DB_HOST=postgres

# Base de données
POSTGRES_DB=glop
POSTGRES_USER=admin
POSTGRES_PASSWORD=<inventez un mot de passe fort, ex: K9x2mP!qR7nL4vB>

# Clé secrète JWT — générer avec la commande ci-dessous
JWT_SECRET=<voir commande ci-dessous>

# Votre nom d'utilisateur Docker Hub
DOCKERHUB_USERNAME=<votre-username-dockerhub>

# Identifiant Kafka — générer avec la commande ci-dessous
KAFKA_CLUSTER_ID=<voir commande ci-dessous>

# Profil Spring Boot — NE PAS CHANGER
SPRING_PROFILES_ACTIVE=prod
```

### 6.2 Générer les valeurs secrètes

**JWT_SECRET** (clé de chiffrement des tokens d'authentification) :
```bash
openssl rand -hex 64
# Exemple de résultat :
# a3f8c2d1e4b7906f5e2c8a1d4b7e0f3c6a9d2e5b8c1f4a7d0e3b6c9f2a5d8e1b4c7f0a3d6
```

**KAFKA_CLUSTER_ID** (identifiant unique du cluster Kafka) :
```bash
docker run --rm confluentinc/cp-kafka:7.7.0 kafka-storage random-uuid
# Exemple de résultat :
# gZIoGN-7RFewCbQlrYvGqQ
```

Collez ces valeurs dans votre `.env.prod`.

> **IMPORTANT** : Le fichier `.env.prod` contient des informations sensibles. Ne le commitez jamais dans Git. Il est déjà listé dans `.gitignore`.

---

## 7. Builder et publier les images Docker

Cette étape construit le code source Java en images Docker et les envoie sur Docker Hub.

### 7.1 Se connecter à Docker Hub

Sur votre machine locale :

```bash
docker login -u <votre-username-dockerhub>
# Saisissez votre Personal Access Token comme mot de passe (pas votre mot de passe Docker Hub)
```

### 7.2 Lancer le build

```bash
# Depuis la racine du projet CiblOrgaSport_Back :
./deploy/build-and-push.sh
```

Ce script va :
1. Construire 13 images Docker (une par microservice)
2. Chaque build compile le code Java avec Maven et crée une image légère
3. Pousser chaque image sur Docker Hub

**Durée estimée :** 20 à 40 minutes selon la vitesse de votre connexion Internet.

Vous verrez défiler des messages comme :
```
─── [gateway] → monentreprise/ciblorgasport-gateway:latest
...
─── [auth-service] → monentreprise/ciblorgasport-auth:latest
...
Toutes les images ont été buildées et poussées avec succès.
```

### 7.3 Vérifier sur Docker Hub

Rendez-vous sur `https://hub.docker.com/u/<votre-username>`. Vous devriez voir 13 dépôts nommés `ciblorgasport-*`.

---

## 8. Déployer l'application

### 8.1 Copier les fichiers de configuration sur la VM

Depuis votre machine locale :

```bash
scp docker-compose.prod.services.yml .env.prod ubuntu@<IP_DE_VOTRE_VM>:~/
```

### 8.2 Lancer tous les conteneurs

Connectez-vous à la VM :

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>
```

Téléchargez les images et démarrez les conteneurs :

```bash
# Télécharger toutes les images depuis Docker Hub (peut prendre quelques minutes)
docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull

# Démarrer tous les conteneurs en arrière-plan
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d
```

### 8.3 Vérifier que les conteneurs démarrent

```bash
docker ps --format "table {{.Names}}\t{{.Status}}"
```

Vous devriez voir 15 lignes. Attendez que tous les statuts affichent `Up` :

```
NAMES                    STATUS
postgres                 Up 2 minutes (healthy)
kafka                    Up 2 minutes (healthy)
gateway                  Up 2 minutes
auth-service             Up 2 minutes
event-service            Up 2 minutes
billetterie              Up 2 minutes
abonnement-service       Up 2 minutes
incident-service         Up 2 minutes
participants-service     Up 2 minutes
resultats-service        Up 2 minutes
notifications-service    Up 2 minutes
lieu-service             Up 2 minutes
geolocation-service      Up 2 minutes
analytics-service        Up 2 minutes
volunteer-service        Up 2 minutes
```

> Les microservices Spring Boot peuvent prendre 30 à 60 secondes à démarrer. Si un conteneur affiche `Restarting`, attendez 1 à 2 minutes — c'est normal pendant le démarrage initial (les services attendent que la base de données soit prête).

---

## 9. Configurer Nginx (reverse proxy)

Nginx fait le lien entre Internet et votre application. Il reçoit les requêtes sur le port 80 et les transmet au Spring Cloud Gateway.

Connectez-vous à la VM et exécutez ces commandes :

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Installer Nginx
sudo apt-get update && sudo apt-get install -y nginx

# Créer la configuration
sudo tee /etc/nginx/sites-available/ciblorgasport > /dev/null << 'NGINX_CONF'
upstream gateway {
    server 127.0.0.1:8080;
}

limit_req_zone $binary_remote_addr zone=api:10m rate=30r/s;

server {
    listen 80;
    server_name _;

    # En-têtes de sécurité
    add_header X-Frame-Options DENY;
    add_header X-Content-Type-Options nosniff;

    # Vérification de santé Nginx
    location /nginx-health {
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }

    # Toutes les requêtes API
    location / {
        limit_req zone=api burst=50 nodelay;
        proxy_pass http://gateway;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_read_timeout 60s;
    }
}
NGINX_CONF

# Activer la configuration et désactiver le site par défaut
sudo ln -sf /etc/nginx/sites-available/ciblorgasport /etc/nginx/sites-enabled/ciblorgasport
sudo rm -f /etc/nginx/sites-enabled/default

# Tester la configuration (doit afficher "syntax is ok")
sudo nginx -t

# Démarrer Nginx et l'activer au démarrage
sudo systemctl enable nginx
sudo systemctl reload nginx

# Vérifier que Nginx est actif
sudo systemctl is-active nginx
```

Si la dernière commande affiche `active`, Nginx est opérationnel.

---

## 10. Vérifier que tout fonctionne

### 10.1 Test rapide depuis votre navigateur

Ouvrez un navigateur et allez sur :
```
http://<IP_DE_VOTRE_VM>/actuator/health
```

Vous devriez voir une réponse JSON avec `"status":"UP"`.

### 10.2 Health check complet

Depuis votre machine locale :

```bash
./deploy/health-check.sh <IP_DE_VOTRE_VM>
```

Résultat attendu :
```
═══════════════════════════════════════════════════════
 Health Check — CiblOrgaSport Production
 Gateway: http://<IP_DE_VOTRE_VM>
═══════════════════════════════════════════════════════

[ Nginx ]
  OK     [200] nginx

[ Spring Cloud Gateway ]
  OK     [200] gateway /actuator/health

[ Microservices (via gateway /actuator/health) ]
  OK     [403] auth-service
  OK     [401] event-service
  OK     [401] lieu-service
  ...

═══════════════════════════════════════════════════════
 Résultat : 12 OK / 0 ECHEC
═══════════════════════════════════════════════════════
```

> Les codes 401 et 403 sont **normaux** : ils indiquent que les services répondent et que la sécurité JWT est active. Ce n'est pas une erreur.

### 10.3 Premier appel API avec Postman

1. Ouvrez Postman
2. Importez l'environnement `postman/postman/environments/CiblOrgaSport_Production.postman_environment.json`
3. Sélectionnez l'environnement **"CiblOrgaSport Production"**
4. Testez la connexion :
   - Méthode : `POST`
   - URL : `http://<IP_DE_VOTRE_VM>/auth/api/auth/signin`
   - Body (JSON) :
     ```json
     {
       "username": "admin",
       "password": "password"
     }
     ```
5. Vous recevrez un token JWT dans la réponse — copiez-le dans la variable `token` de l'environnement Postman

---

## 11. Activer HTTPS avec un nom de domaine

Cette étape est optionnelle mais recommandée pour la production. Elle nécessite que vous possédiez un nom de domaine.

### 11.1 Pointer le DNS vers votre VM

Chez votre registrar de domaine (OVH, Gandi, etc.) :
- Créez un enregistrement de type **A**
- Hôte : `@` (ou `api` si vous souhaitez un sous-domaine)
- Valeur : l'adresse IP de votre VM
- TTL : 3600 (1 heure)

Attendez 15 à 60 minutes que la propagation DNS s'effectue. Testez :
```bash
ping votre-domaine.fr
# Doit résoudre vers l'IP de votre VM
```

### 11.2 Obtenir un certificat SSL gratuit (Let's Encrypt)

Connectez-vous à la VM :

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Installer Certbot
sudo apt install -y certbot python3-certbot-nginx

# Obtenir et configurer le certificat automatiquement
sudo certbot --nginx -d votre-domaine.fr --non-interactive --agree-tos -m admin@votre-domaine.fr

# Tester le renouvellement automatique
sudo certbot renew --dry-run
```

Après cette étape, votre API est accessible via `https://votre-domaine.fr`.

Le certificat se renouvelle automatiquement tous les 90 jours.

---

## 12. Maintenance et opérations courantes

### 12.1 Voir les logs d'un service

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Logs du gateway (les 100 dernières lignes)
docker logs gateway --tail=100 -f

# Logs de l'auth-service
docker logs auth-service --tail=100 -f

# Ctrl+C pour arrêter l'affichage en temps réel
```

### 12.2 Redémarrer un service

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>
docker compose -f docker-compose.prod.services.yml --env-file .env.prod restart auth-service
```

### 12.3 Mettre à jour l'application (nouvelle version)

Sur votre machine locale, après avoir modifié le code :

```bash
# 1. Rebuilder et pousser uniquement le service modifié
docker build -t <DOCKERHUB_USERNAME>/ciblorgasport-auth:latest ./auth-service/
docker push <DOCKERHUB_USERNAME>/ciblorgasport-auth:latest

# 2. Sur la VM, télécharger la nouvelle image et redémarrer le service
ssh ubuntu@<IP_DE_VOTRE_VM> \
  'docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull auth-service && \
   docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d auth-service'
```

### 12.4 Sauvegarder la base de données

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Créer un dump de la base de données
docker exec postgres pg_dump -U admin glop > backup_$(date +%Y%m%d_%H%M%S).sql

# Copier le backup sur votre machine locale
exit
scp ubuntu@<IP_DE_VOTRE_VM>:~/backup_*.sql ./backups/
```

### 12.5 Restaurer la base de données

```bash
# Copier le backup sur la VM
scp backup_20240101_120000.sql ubuntu@<IP_DE_VOTRE_VM>:~/

# Restaurer
ssh ubuntu@<IP_DE_VOTRE_VM>
docker exec -i postgres psql -U admin glop < backup_20240101_120000.sql
```

### 12.6 Arrêter et redémarrer toute l'application

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Arrêter (les données PostgreSQL sont conservées dans le volume Docker)
docker compose -f docker-compose.prod.services.yml --env-file .env.prod down

# Redémarrer
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d
```

### 12.7 Vérifier l'utilisation des ressources

```bash
ssh ubuntu@<IP_DE_VOTRE_VM>

# Utilisation CPU et mémoire par conteneur
docker stats --no-stream

# Espace disque utilisé par Docker
docker system df
```

---

## 13. Résolution de problèmes

### Un service affiche "Restarting" dans `docker ps`

**Cause probable** : le service n'arrive pas à démarrer.

**Solution** :
```bash
docker logs <nom-du-service> --tail=50
```
Lisez l'erreur. Les causes les plus fréquentes :
- `Connection refused` sur PostgreSQL → attendez 30 secondes, PostgreSQL démarre plus lentement
- `Invalid JWT secret` → vérifiez `JWT_SECRET` dans `.env.prod`
- `Could not connect to Kafka` → vérifiez que le service `kafka` est `healthy`

### `docker compose up -d` affiche des warnings "variable is not set"

Ces warnings sont normaux lors de la commande `docker compose ps` sans `--env-file`. Lors du démarrage avec `--env-file .env.prod`, toutes les variables sont correctement injectées dans les conteneurs.

### L'API ne répond pas depuis le navigateur (timeout)

Vérifiez dans l'ordre :
1. Le port 80 est-il ouvert dans les Security Groups OVH ?
2. Nginx est-il actif ? `sudo systemctl status nginx`
3. Le gateway est-il démarré ? `docker logs gateway --tail=20`

### Erreur "no main manifest attribute" dans les logs du gateway

Le JAR n'a pas été compilé correctement. Solution :
```bash
# Sur votre machine locale, rebuilder le gateway
docker build -t <DOCKERHUB_USERNAME>/ciblorgasport-gateway:latest ./gateway/
docker push <DOCKERHUB_USERNAME>/ciblorgasport-gateway:latest

# Sur la VM
docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull gateway
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d gateway
```

### PostgreSQL ne démarre pas

```bash
docker logs postgres --tail=30
```
Vérifiez que `POSTGRES_PASSWORD` dans `.env.prod` ne contient pas de caractères spéciaux problématiques comme `@`, `#`, `$` sans être entre guillemets.

### Le certificat SSL ne se renouvelle pas automatiquement

```bash
# Vérifier que le cron Certbot est actif
sudo systemctl status certbot.timer

# Tester manuellement
sudo certbot renew --dry-run
```

---

## Récapitulatif des informations importantes

Conservez ces informations en lieu sûr :

| Information | Valeur |
|-------------|--------|
| IP de la VM | `<IP_DE_VOTRE_VM>` |
| URL de l'API | `http://<IP_DE_VOTRE_VM>` ou `https://votre-domaine.fr` |
| Utilisateur SSH | `ubuntu` |
| Utilisateur PostgreSQL | `admin` (défini dans .env.prod) |
| Nom de la base de données | `glop` |
| Fichier de secrets | `.env.prod` (sur votre machine locale — NE PAS partager) |

---

## Support et contact

Pour toute question technique concernant le déploiement, référez-vous à :
- [PORTS.md](./PORTS.md) — Documentation des ports
- [DEPLOYMENT_STEPS.md](./DEPLOYMENT_STEPS.md) — Référence rapide pour les développeurs
- Les logs des conteneurs via `docker logs <service>`
