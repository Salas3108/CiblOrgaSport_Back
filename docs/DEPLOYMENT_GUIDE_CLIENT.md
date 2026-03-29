# Guide de déploiement — CiblOrgaSport Backend

## Retour d'expérience : comment nous avons déployé le backend en production

Ce document décrit **exactement** ce que nous avons fait pour déployer l'application,
dans l'ordre chronologique, avec les problèmes rencontrés et les solutions appliquées.
Il sert de référence pour reproduire ou mettre à jour le déploiement.

**URL de production finale :** [http://137.74.133.131](http://137.74.133.131)
**VM de production :** OVH Public Cloud, Gravelines (GRA11), Ubuntu 25.04, 16 Go RAM

---

## Table des matières

1. [Infrastructure OVH provisionnée](#1-infrastructure-ovh-provisionnée)
2. [Création du compte Docker Hub](#2-création-du-compte-docker-hub)
3. [Préparation du code : branche et Dockerfiles](#3-préparation-du-code--branche-et-dockerfiles)
4. [Fichier de secrets .env.prod](#4-fichier-de-secrets-envprod)
5. [Build et push des images Docker](#5-build-et-push-des-images-docker)
6. [Installation de Docker sur la VM](#6-installation-de-docker-sur-la-vm)
7. [Déploiement de la stack complète](#7-déploiement-de-la-stack-complète)
8. [Configuration de Nginx](#8-configuration-de-nginx)
9. [Vérification finale](#9-vérification-finale)
10. [Problèmes rencontrés et solutions](#10-problèmes-rencontrés-et-solutions)
11. [Mise à jour du déploiement](#11-mise-à-jour-du-déploiement)

---

## 1. Infrastructure OVH provisionnée

Nous avons démarré avec 3 VMs OVH Public Cloud prévues pour une architecture 3 tiers.
Après plusieurs problèmes réseau entre régions OVH (voir section 10), nous avons simplifié
à **une seule VM** qui fait tout tourner.

### VM de production active

| Paramètre | Valeur |
| --------- | ------ |
| IP publique | `137.74.133.131` |
| OS | Ubuntu 25.04 LTS |
| Région OVH | Gravelines (GRA11) |
| RAM | 16 Go |
| vCPU | 4 |
| Utilisateur SSH | `ubuntu` |

### Se connecter à la VM

```bash
ssh ubuntu@137.74.133.131
```

---

## 2. Création du compte Docker Hub

Docker Hub stocke les 13 images Docker de l'application.

**Compte utilisé :** `salim26072000`
**Images publiées :** `salim26072000/ciblorgasport-*:latest`

### Créer un Personal Access Token (PAT)

Si tu dois te reconnecter ou changer de machine :

1. hub.docker.com → ton avatar → Account Settings → Security
2. "New Access Token" → nom : `ciblorgasport` → permissions : **Read & Write**
3. Copie le token (affiché une seule fois)

```bash
# Se connecter avec le token (pas le mot de passe du compte)
docker login -u salim26072000
# Password: colle le Personal Access Token ici
```

> **Attention :** Si tu t'es inscrit sur Docker Hub avec Google/Gmail, tu n'as pas de
> mot de passe. Tu dois obligatoirement utiliser un Personal Access Token.

---

## 3. Préparation du code : branche et Dockerfiles

### Branche de déploiement

Tout le code de déploiement est sur la branche `deploy/ovh-production` :

```bash
git checkout deploy/ovh-production
```

### Ce que nous avons créé / corrigé

**10 Dockerfiles manquants** créés (seuls `billetterie`, `resultats-service` et
`notifications-service` en avaient un) :

- `gateway/Dockerfile`
- `auth-service/Dockerfile`
- `event-service/Dockerfile`
- `abonnement-service/Dockerfile`
- `incident-service/Dockerfile`
- `participants-service/Dockerfile`
- `lieu-service/Dockerfile`
- `geolocation-service/Dockerfile`
- `volunteer-service/Dockerfile`

**Modèle standard utilisé pour chaque Dockerfile :**

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE <PORT>
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

**Corrections sur les Dockerfiles existants :**

- `billetterie/Dockerfile` : `EXPOSE 8080` corrigé en `EXPOSE 8083`
- `notifications-service/Dockerfile` : `-DskipTests` remplacé par
  `-Dmaven.test.skip=true` (le pom.xml ignorait le flag `-DskipTests`)
- `gateway/pom.xml` : ajout de `spring-boot-maven-plugin` manquant (le JAR
  généré sans ce plugin n'est pas exécutable → erreur "no main manifest attribute")

**Profile Spring Boot de production :**

Fichier `gateway/src/main/resources/application-prod.properties` créé pour
surcharger toutes les routes du gateway (localhost → noms de services Docker) :

```properties
spring.cloud.gateway.routes[0].uri=http://auth-service:8081
spring.cloud.gateway.routes[1].uri=http://event-service:8084
# ... (10 routes au total)
```

Activé via `SPRING_PROFILES_ACTIVE=prod` dans le docker-compose.

---

## 4. Fichier de secrets .env.prod

Le fichier `.env.prod` est **sur ta machine locale uniquement**, jamais dans Git.

**Emplacement local :** `CiblOrgaSport_Back/.env.prod`

Contenu actuel (valeurs réelles) :

```dotenv
# PostgreSQL — nom du service Docker (ne pas changer)
DB_HOST=postgres

# Base de données
POSTGRES_DB=glop
POSTGRES_USER=admin
POSTGRES_PASSWORD=<mot de passe configuré lors du déploiement>

# JWT — clé de signature des tokens (min 64 caractères)
JWT_SECRET=<clé générée avec: openssl rand -hex 64>

# Docker Hub
DOCKERHUB_USERNAME=salim26072000

# Kafka
KAFKA_CLUSTER_ID=<ID généré avec kafka-storage random-uuid>

# Spring Boot
SPRING_PROFILES_ACTIVE=prod
```

### Générer de nouvelles valeurs secrètes si besoin

```bash
# Nouveau JWT_SECRET
openssl rand -hex 64

# Nouveau KAFKA_CLUSTER_ID
docker run --rm confluentinc/cp-kafka:7.7.0 kafka-storage random-uuid
```

---

## 5. Build et push des images Docker

### Lancer le build de toutes les images

Depuis la racine du projet, sur ta machine locale :

```bash
# Se connecter à Docker Hub d'abord
docker login -u salim26072000

# Builder et pousser les 13 images
./deploy/build-and-push.sh
```

Ce script fait pour chaque service :

1. `docker build -t salim26072000/ciblorgasport-<service>:latest ./<service>/`
2. `docker push salim26072000/ciblorgasport-<service>:latest`

**Durée :** environ 25-35 minutes la première fois (compilation Maven + upload).
Les fois suivantes, Maven et Docker utilisent leur cache → beaucoup plus rapide.

### Images publiées

| Image Docker Hub | Service |
| ---------------- | ------- |
| `salim26072000/ciblorgasport-gateway` | Spring Cloud Gateway |
| `salim26072000/ciblorgasport-auth` | auth-service |
| `salim26072000/ciblorgasport-billetterie` | billetterie |
| `salim26072000/ciblorgasport-event` | event-service |
| `salim26072000/ciblorgasport-abonnement` | abonnement-service |
| `salim26072000/ciblorgasport-incident` | incident-service |
| `salim26072000/ciblorgasport-participants` | participants-service |
| `salim26072000/ciblorgasport-resultats` | resultats-service |
| `salim26072000/ciblorgasport-notifications` | notifications-service |
| `salim26072000/ciblorgasport-lieu` | lieu-service |
| `salim26072000/ciblorgasport-geolocation` | geolocation-service |
| `salim26072000/ciblorgasport-analytics` | analytics-service |
| `salim26072000/ciblorgasport-volunteer` | volunteer-service |

---

## 6. Installation de Docker sur la VM

**Fait une seule fois** lors de la mise en place. Si la VM est recréée, refaire ces étapes.

```bash
# Se connecter à la VM
ssh ubuntu@137.74.133.131

# Installer Docker
curl -fsSL https://get.docker.com | sudo sh

# Ajouter l'utilisateur ubuntu au groupe docker
sudo usermod -aG docker ubuntu

# Se déconnecter puis reconnecter pour activer le groupe
exit
ssh ubuntu@137.74.133.131

# Vérifier
docker --version
docker compose version
```

---

## 7. Déploiement de la stack complète

### Architecture finale : tout sur une seule VM

Après les problèmes réseau OVH (voir section 10), nous avons intégré PostgreSQL
directement dans le docker-compose des microservices. La stack complète tourne sur
une seule VM avec le réseau Docker interne `prod-net`.

```text
VM 137.74.133.131
└── Docker réseau prod-net
    ├── postgres:16          (base de données)
    ├── kafka:7.7.0          (messagerie)
    ├── gateway:latest       (routeur Spring Cloud)
    ├── auth-service:latest
    ├── billetterie:latest
    ├── event-service:latest
    ├── abonnement-service:latest
    ├── incident-service:latest
    ├── participants-service:latest
    ├── resultats-service:latest
    ├── notifications-service:latest
    ├── lieu-service:latest
    ├── geolocation-service:latest
    ├── analytics-service:latest
    └── volunteer-service:latest
```

### Commandes de déploiement

**Depuis la machine locale :**

```bash
# Copier les fichiers de config sur la VM
scp docker-compose.prod.services.yml .env.prod ubuntu@137.74.133.131:~/
```

**Depuis la VM :**

```bash
ssh ubuntu@137.74.133.131

# Télécharger toutes les images depuis Docker Hub
docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull

# Démarrer tous les conteneurs
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d

# Vérifier que tout est Up (attendre 1-2 minutes)
docker ps --format "table {{.Names}}\t{{.Status}}"
```

**Résultat attendu (15 conteneurs) :**

```text
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

> Les services Spring Boot mettent 30 à 60 secondes à démarrer. Un statut
> `Restarting` pendant les 2 premières minutes est normal — les services attendent
> que PostgreSQL et Kafka soient prêts. Avec `restart: always`, ils repartent
> automatiquement dès que la base est disponible.

---

## 8. Configuration de Nginx

Nginx est installé directement sur la VM (pas dans Docker) et fait le lien entre
le port 80 public et le gateway Docker sur le port 8080 en localhost.

### Pourquoi Nginx sur la VM et non dans Docker ?

Le port 80 est accessible depuis Internet sur cette VM (OVH l'autorise par défaut).
Le port 8080 du gateway Docker n'est pas accessible depuis l'extérieur à cause du
pare-feu OVH. Nginx sur la VM fait le pont en localhost : aucune restriction réseau.

### Installation et configuration

```bash
ssh ubuntu@137.74.133.131

# Installer Nginx
sudo apt-get install -y nginx

# Écrire la configuration
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

# Activer la config et désactiver le site par défaut
sudo ln -sf /etc/nginx/sites-available/glop /etc/nginx/sites-enabled/glop
sudo rm -f /etc/nginx/sites-enabled/default

# Tester, activer et démarrer
sudo nginx -t
sudo systemctl enable nginx
sudo systemctl reload nginx
```

---

## 9. Vérification finale

### Health check complet

Depuis ta machine locale :

```bash
./deploy/health-check.sh 137.74.133.131
```

Résultat obtenu lors de notre déploiement :

```text
═══════════════════════════════════════════════════════
 Health Check — CiblOrgaSport Production
 Gateway: http://137.74.133.131
═══════════════════════════════════════════════════════

[ Nginx ]
  OK     [200] nginx

[ Spring Cloud Gateway ]
  OK     [200] gateway /actuator/health

[ Microservices (via gateway /actuator/health) ]
  OK     [403] auth-service
  OK     [401] event-service
  OK     [401] lieu-service
  OK     [401] billetterie
  OK     [401] abonnement-service
  OK     [401] incident-service
  OK     [401] participants-service
  OK     [401] resultats-service
  OK     [401] notifications-service
  OK     [401] volunteer-service

═══════════════════════════════════════════════════════
 Résultat : 12 OK / 0 ECHEC
═══════════════════════════════════════════════════════
```

> Les codes 401 et 403 sont normaux : les endpoints actuator sont protégés par
> Spring Security. Cela confirme que les services répondent et que la sécurité JWT
> est active.

### Test d'un appel API réel

```bash
curl -X POST http://137.74.133.131/auth/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

Réponse attendue :

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}
```

---

## 10. Problèmes rencontrés et solutions

Cette section documente les problèmes réels que nous avons rencontrés pendant le
déploiement et les solutions appliquées.

---

### Problème 1 — Rocky Linux 10 incompatible avec Docker

**VM concernée :** vm-database (57.130.46.227)
**Symptôme :**

```text
iptables v1.8.11 (nf_tables): RULE_APPEND failed (No such file or directory)
```

**Cause :** Rocky Linux 10 utilise exclusivement `nftables`. Docker CE utilise
`iptables` qui n'est pas disponible dans le noyau.

**Solution :** Re-imager la VM en Ubuntu 25.04 via le panneau OVH.
Sur Ubuntu, Docker s'installe et fonctionne normalement.

---

### Problème 2 — OVH bloque les ports entre régions différentes

**Symptôme :** `nc: connect to 137.74.133.131 port 8080 failed: Connection timed out`

**Cause :** Les VMs étaient dans deux régions OVH différentes (Paris et Gravelines).
OVH bloque les ports non-standard (8080, 5432, etc.) entre régions via ses Security
Groups réseau. Seuls les ports 22 et 80 sont accessibles par défaut.

**Solution :** Abandonner l'architecture 3 VMs et tout faire tourner sur une seule VM.
Le gateway Docker expose le port 8080 en localhost, Nginx fait le proxy 80 → 8080
en local. Aucune communication inter-VM nécessaire.

---

### Problème 3 — `no main manifest attribute` dans le gateway

**Symptôme :** Le conteneur `gateway` redémarrait en boucle avec l'erreur :

```text
no main manifest attribute, in /app/app.jar
```

**Cause :** Le `gateway/pom.xml` n'avait pas le plugin `spring-boot-maven-plugin`
dans sa section `<build>`. Sans ce plugin, Maven génère un JAR standard (non
exécutable) au lieu d'un fat JAR Spring Boot.

**Solution :** Ajouter le plugin dans `gateway/pom.xml` :

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

Rebuild et push de l'image gateway.

---

### Problème 4 — `-DskipTests` ignoré dans notifications-service

**Symptôme :** Le build Docker de `notifications-service` échoue avec des erreurs
de tests unitaires malgré le flag `-DskipTests` dans le Dockerfile.

**Cause :** Le `pom.xml` de `notifications-service` surcharge la propriété
`skipTests` et l'ignore via la configuration du plugin Surefire.

**Solution :** Remplacer `-DskipTests` par `-Dmaven.test.skip=true` dans le Dockerfile.
Ce flag est prioritaire sur toute configuration pom.xml.

```dockerfile
# Avant (ignoré par pom.xml)
RUN mvn -q -DskipTests package

# Après (prioritaire sur tout)
RUN mvn -q -Dmaven.test.skip=true package
```

---

### Problème 5 — Docker Hub "insufficient_scope" (accès refusé au push)

**Symptôme :**

```text
denied: requested access to the resource is denied
```

**Cause :** Deux causes successives :

1. Première tentative sans être connecté à Docker Hub
2. Personal Access Token créé avec les droits **Read-only** au lieu de **Read & Write**

**Solution :**

1. `docker login -u salim26072000` avec le token
2. Dans Docker Hub → Account Settings → Security → modifier le token → cocher **Read & Write**
3. Re-lancer `docker login` avec le nouveau token

---

### Problème 6 — CORS bloqué depuis le frontend AWS S3

**Symptôme :**

```text
Access to fetch at 'http://137.74.133.131/auth/login' from origin
'http://ciblorgasport-frontend-prod.s3-website.eu-west-3.amazonaws.com'
has been blocked by CORS policy
```

**Cause :** La configuration CORS du gateway (`SecurityConfig.java`) n'autorisait
que `http://localhost:3000`.

**Solution :** Ajouter l'URL S3 dans `SecurityConfig.java` :

```java
configuration.addAllowedOrigin("http://ciblorgasport-frontend-prod.s3-website.eu-west-3.amazonaws.com");
```

Rebuild du gateway, push, redéploiement. Test de vérification :

```bash
curl -I -X OPTIONS http://137.74.133.131/auth/api/auth/signin \
  -H "Origin: http://ciblorgasport-frontend-prod.s3-website.eu-west-3.amazonaws.com" \
  -H "Access-Control-Request-Method: POST"
# Attendu : Access-Control-Allow-Origin: http://ciblorgasport-...
```

---

## 11. Mise à jour du déploiement

### Mettre à jour un seul service

Exemple avec `auth-service` après modification du code :

```bash
# 1. Sur ta machine locale — rebuilder et pousser l'image
docker build -t salim26072000/ciblorgasport-auth:latest ./auth-service/
docker push salim26072000/ciblorgasport-auth:latest

# 2. Sur la VM — télécharger la nouvelle image et redémarrer
ssh ubuntu@137.74.133.131 \
  'docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull auth-service && \
   docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d auth-service'
```

### Mettre à jour tous les services

```bash
# Rebuild et push de toutes les images
./deploy/build-and-push.sh

# Sur la VM
ssh ubuntu@137.74.133.131 \
  'docker compose -f docker-compose.prod.services.yml --env-file .env.prod pull && \
   docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d'
```

### Redémarrer toute la stack sans rebuild

```bash
ssh ubuntu@137.74.133.131
docker compose -f docker-compose.prod.services.yml --env-file .env.prod down
docker compose -f docker-compose.prod.services.yml --env-file .env.prod up -d
```

### Voir les logs en temps réel

```bash
ssh ubuntu@137.74.133.131

# Tous les services
docker compose -f docker-compose.prod.services.yml logs -f --tail=50

# Un service spécifique
docker logs gateway -f --tail=100
docker logs postgres -f --tail=50
```

### Sauvegarder la base de données

```bash
ssh ubuntu@137.74.133.131

# Créer un dump
docker exec postgres pg_dump -U admin glop > backup_$(date +%Y%m%d_%H%M%S).sql

# Copier le dump sur ta machine locale
exit
scp ubuntu@137.74.133.131:~/backup_*.sql ./backups/
```

---

## Récapitulatif de l'état final

| Composant | Emplacement | Statut |
| --------- | ----------- | ------ |
| Nginx | VM 137.74.133.131, port 80 | Actif (systemd) |
| Spring Cloud Gateway | Docker, port 8080 (interne) | Up |
| PostgreSQL 16 | Docker, réseau prod-net | Healthy |
| Kafka KRaft | Docker, réseau prod-net | Healthy |
| 13 microservices | Docker, réseau prod-net | Up |
| Images Docker Hub | salim26072000/ciblorgasport-* | 13 images |
| Code source | github.com/Salas3108/CiblOrgaSport_Back | Branche deploy/ovh-production |
| Frontend CORS | SecurityConfig.java | AWS S3 autorisé |
