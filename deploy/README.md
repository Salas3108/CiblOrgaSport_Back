# Déploiement OVH — CiblOrgaSport_Back

Architecture 3 VMs :
- **vm-gateway** : Nginx (port 80/443) → Spring Cloud Gateway
- **vm-services** : Kafka + 13 microservices Spring Boot
- **vm-database** : PostgreSQL 16

---

## Pré-requis

1. **Provisionner 3 VMs OVH** (Ubuntu 22.04 LTS)
   - vm-gateway : 2 vCPU, 2 Go RAM
   - vm-services : 4 vCPU, 8 Go RAM (minimum)
   - vm-database : 2 vCPU, 4 Go RAM

2. **Activer le vRack OVH** et noter les IPs privées des 3 VMs

3. **Créer un compte Docker Hub** : hub.docker.com

4. **Configurer les clés SSH** localement vers les 3 VMs

5. **Remplir le fichier `.env.prod`** :
   ```bash
   cp .env.prod.example .env.prod
   # Éditer .env.prod avec vos valeurs
   ```
   Générer le JWT_SECRET :
   ```bash
   openssl rand -hex 64
   ```
   Générer le KAFKA_CLUSTER_ID :
   ```bash
   docker run --rm confluentinc/cp-kafka:7.7.0 kafka-storage random-uuid
   ```

---

## Étapes de déploiement

### Étape 1 — Installer Docker sur chaque VM

```bash
ssh ubuntu@<IP_VM_GATEWAY>  'bash -s' < deploy/setup-vm.sh
ssh ubuntu@<IP_VM_SERVICES> 'bash -s' < deploy/setup-vm.sh
ssh ubuntu@<IP_VM_DATABASE> 'bash -s' < deploy/setup-vm.sh
```

### Étape 2 — Builder et pousser les images Docker

```bash
docker login
./deploy/build-and-push.sh
```

### Étape 3 — Déployer la base de données

```bash
chmod +x deploy/deploy-db.sh
./deploy/deploy-db.sh ubuntu@<IP_VM_DATABASE>
```

### Étape 4 — Déployer les microservices

```bash
chmod +x deploy/deploy-services.sh
./deploy/deploy-services.sh ubuntu@<IP_VM_SERVICES>
```

Vérifier les logs :
```bash
ssh ubuntu@<IP_VM_SERVICES> 'docker compose -f docker-compose.prod.services.yml logs -f --tail=50'
```

### Étape 5 — Déployer Nginx

Remplacer `<PRIV_SERVICES>` dans la commande par l'IP privée vRack de vm-services :
```bash
chmod +x deploy/deploy-gateway.sh
./deploy/deploy-gateway.sh ubuntu@<IP_VM_GATEWAY> <PRIV_SERVICES>
```

### Étape 6 — Vérification post-déploiement

```bash
chmod +x deploy/health-check.sh
./deploy/health-check.sh <IP_PUBLIQUE_VM_GATEWAY>
```

---

## Commandes utiles

```bash
# Logs d'un service spécifique
ssh ubuntu@<IP_VM_SERVICES> 'docker logs auth-service -f --tail=100'

# Redémarrer un service
ssh ubuntu@<IP_VM_SERVICES> 'docker compose -f docker-compose.prod.services.yml restart auth-service'

# Mettre à jour une image
ssh ubuntu@<IP_VM_SERVICES> 'docker compose -f docker-compose.prod.services.yml pull gateway && docker compose -f docker-compose.prod.services.yml up -d gateway'

# Topics Kafka
ssh ubuntu@<IP_VM_SERVICES> 'docker exec kafka kafka-topics.sh --list --bootstrap-server localhost:9092'
```

---

## Activation SSL (après configuration d'un domaine)

1. Pointer le DNS de votre domaine vers l'IP publique de vm-gateway
2. Exécuter sur vm-gateway :
```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d <VOTRE_DOMAINE> --non-interactive --agree-tos -m <EMAIL>
```

---

## Pare-feu recommandé (via OVH Control Panel)

| VM | Port entrant autorisé | Source |
|----|-----------------------|--------|
| vm-gateway | 80, 443 | 0.0.0.0/0 (internet) |
| vm-gateway | 22 | Votre IP uniquement |
| vm-services | 8080 | IP privée vm-gateway uniquement |
| vm-services | 22 | Votre IP uniquement |
| vm-database | 5432 | IP privée vm-services uniquement |
| vm-database | 22 | Votre IP uniquement |
