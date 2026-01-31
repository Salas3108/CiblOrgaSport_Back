# Microservice Résultats

## Description
Microservice pour la saisie manuelle et la gestion des résultats d'épreuves. Permet aux commissaires de saisir les résultats et assure la traçabilité de chaque saisie.

## Fonctionnalités
- ✅ Saisie manuelle des résultats d'épreuves par les commissaires
- ✅ Modification des résultats en attente de validation
- ✅ Validation des résultats par les administrateurs
- ✅ Historique complet des modifications pour traçabilité
- ✅ Gestion des statuts (SAISI, VALIDE, REJET, EN_CORRECTION)
- ✅ Sécurité basée sur JWT avec rôles (COMMISSAIRE, ADMIN)

## Architecture
```
resultat-service/
├── pom.xml
├── src/main/
│   ├── java/com/ciblorgasport/resultatservice/
│   │   ├── controller/       # Contrôleurs REST
│   │   ├── service/          # Logique métier
│   │   ├── entity/           # Entités JPA
│   │   ├── repository/       # Accès données
│   │   ├── dto/              # Objets de transfert
│   │   ├── security/         # Configuration JWT
│   │   └── ResultatServiceApplication.java
│   └── resources/
│       └── application.properties
└── src/test/
```

## Configuration
- **Port**: 8085
- **Database**: PostgreSQL (glop)
- **ORM**: Spring Data JPA
- **Authentification**: JWT Bearer Token
- **Rolisation**: COMMISSAIRE, ADMIN

## API Endpoints

### Saisie de résultats
- `POST /api/resultats` - Saisir un résultat
  - Rôles: COMMISSAIRE, ADMIN
  - Request body: `CreerResultatRequest`
  - Retour: `ResultatDTO`

### Consultation
- `GET /api/resultats/{id}` - Récupérer un résultat
- `GET /api/resultats/epreuve/{epreuveId}` - Tous les résultats d'une épreuve
- `GET /api/resultats/commissaire/{commissaireId}` - Résultats d'un commissaire (ADMIN)
- `GET /api/resultats/en-attente` - Résultats en attente de validation (ADMIN)

### Modification
- `PUT /api/resultats/{id}` - Modifier un résultat
  - Rôles: COMMISSAIRE, ADMIN
  - Statuts modifiables: SAISI, EN_CORRECTION

### Validation
- `PATCH /api/resultats/{id}/valider` - Valider un résultat
  - Rôles: ADMIN
  - Paramètres: nouveau statut, raison

### Suppression
- `DELETE /api/resultats/{id}` - Supprimer un résultat
  - Rôles: COMMISSAIRE, ADMIN
  - Non supprimable si statut = VALIDE

### Historique
- `GET /api/resultats/{id}/historique` - Historique des modifications (ADMIN)

## DTOs

### CreerResultatRequest
```json
{
  "epreuveId": 1,
  "athleteId": 5,
  "classement": 1,
  "temps": 45.32,
  "distance": 100.50,
  "points": 100,
  "observations": "Athlète en excellente forme"
}
```

### ModifierResultatRequest
```json
{
  "classement": 1,
  "temps": 45.30,
  "distance": null,
  "points": 100,
  "observations": "Temps corrigé"
}
```

### ValiderResultatRequest
```json
{
  "status": "VALIDE",
  "raison": "Résultat validé par l'administrateur"
}
```

### ResultatDTO
```json
{
  "id": 1,
  "epreuveId": 1,
  "athleteId": 5,
  "classement": 1,
  "temps": 45.32,
  "distance": 100.50,
  "points": 100,
  "status": "VALIDE",
  "saisieParId": 10,
  "observations": "Athlète en excellente forme",
  "dateCreation": "2025-01-31T10:30:00",
  "dateModification": "2025-01-31T11:00:00"
}
```

## Entités

### Resultat
- `id`: Long (clé primaire)
- `epreuveId`: Long (référence à Event Service)
- `athleteId`: Long (référence à Athlete)
- `classement`: Integer (rang final)
- `temps`: Double (en secondes)
- `distance`: Double (en mètres)
- `points`: Double (score)
- `status`: StatusResultat enum
- `saisieParId`: Long (ID du commissaire)
- `observations`: String
- `dateCreation`: LocalDateTime
- `dateModification`: LocalDateTime
- `version`: Long (optimistic locking)

### StatusResultat
- `SAISI`: Résultat saisi mais non validé
- `VALIDE`: Résultat validé par admin
- `REJET`: Résultat rejeté
- `EN_CORRECTION`: En attente de correction

### HistoriqueResultat
- Enregistre chaque changement de statut
- Inclut l'ancien statut, le nouveau, la raison et qui a modifié

## Sécurité
- **JWT Bearer Token** requis pour tous les endpoints
- **Validation des rôles** avec `@PreAuthorize`
- **Extraction de l'ID utilisateur** via header `X-User-Id`
- **Validation des entrées** avec Jakarta Validation

## Exemples de requêtes cURL

### Saisir un résultat
```bash
curl -X POST http://localhost:8085/api/resultats \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-User-Id: 10" \
  -H "Content-Type: application/json" \
  -d '{
    "epreuveId": 1,
    "athleteId": 5,
    "classement": 1,
    "temps": 45.32,
    "distance": 100.50,
    "points": 100,
    "observations": "Bonne performance"
  }'
```

### Récupérer les résultats d'une épreuve
```bash
curl -X GET http://localhost:8085/api/resultats/epreuve/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Valider un résultat
```bash
curl -X PATCH http://localhost:8085/api/resultats/1/valider \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "VALIDE",
    "raison": "Validation administrative"
  }'
```

## Tests
```bash
# Build
cd resultat-service
mvn clean package

# Tests unitaires
mvn test

# Lancer le service
mvn spring-boot:run
```

## Intégration avec autres services
- **Event Service**: Référence des épreuves
- **Auth Service**: Validation JWT
- **Gateway**: Route des requêtes

## Cas d'usage
1. **Commissaire saisit un résultat** → POST /api/resultats
2. **Administrateur consulte** → GET /api/resultats/en-attente
3. **Administrateur valide** → PATCH /api/resultats/{id}/valider
4. **Traçabilité disponible** → GET /api/resultats/{id}/historique

## Notes
- Optimistic locking via `@Version` sur `Resultat`
- Timestamps automatiques (`@PrePersist`, `@PreUpdate`)
- Permissions granulaires par endpoint
- Logs complets des opérations
