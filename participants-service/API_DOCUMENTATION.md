# Documentation API - Service Participants

## Endpoints

### 1. Mettre à jour les informations d'un athlète
- **Méthode**: POST
- **URL**: `/athlete/{id}/info`
- **Description**: Permet à un athlète de mettre à jour ses informations personnelles.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "nom": "String",
    "prenom": "String",
    "dateNaissance": "String",
    "nationalite": "String"
  }
  ```

### 2. Valider un athlète (Commissaire)
- **Méthode**: POST
- **URL**: `/commissaire/athletes/{id}/validation`
- **Description**: Permet à un commissaire de valider ou rejeter un athlète.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "nom": "String",
    "prenom": "String",
    "validation": "boolean"
  }
  ```

### 3. Créer une équipe
- **Méthode**: POST
- **URL**: `/api/equipe`
- **Description**: Permet de créer une nouvelle équipe.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "nom": "String",
    "membres": [
      {
        "id": "Long",
        "nom": "String",
        "prenom": "String"
      }
    ]
  }
  ```

### 4. Supprimer une équipe
- **Méthode**: DELETE
- **URL**: `/api/equipe/{id}`
- **Description**: Supprime une équipe existante.
- **Réponse**: HTTP 204 No Content