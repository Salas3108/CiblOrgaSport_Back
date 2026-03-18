# Documentation API - Service Événements

## Endpoints

### 1. Liste des événements
- **Méthode**: GET
- **URL**: `/events`
- **Description**: Retourne la liste de tous les événements.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "name": "String",
      "dateDebut": "String",
      "dateFin": "String",
      "description": "String",
      "paysHote": "String"
    }
  ]
  ```

### 2. Créer un événement
- **Méthode**: POST
- **URL**: `/events`
- **Description**: Permet de créer un nouvel événement (réservé aux administrateurs).
- **Réponse**:
  ```json
  {
    "id": "Long",
    "name": "String",
    "dateDebut": "String",
    "dateFin": "String",
    "description": "String",
    "paysHote": "String"
  }
  ```

### 3. Récupérer un événement par ID
- **Méthode**: GET
- **URL**: `/events/{id}`
- **Description**: Retourne les détails d'un événement spécifique.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "name": "String",
    "dateDebut": "String",
    "dateFin": "String",
    "description": "String",
    "paysHote": "String"
  }
  ```

### 4. Mettre à jour un événement
- **Méthode**: PUT
- **URL**: `/events/{id}`
- **Description**: Met à jour les informations d'un événement existant.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "name": "String",
    "dateDebut": "String",
    "dateFin": "String",
    "description": "String",
    "paysHote": "String"
  }
  ```

### 5. Supprimer un événement
- **Méthode**: DELETE
- **URL**: `/events/{id}`
- **Description**: Supprime un événement spécifique (réservé aux administrateurs).
- **Réponse**: HTTP 204 No Content

### 6. Liste des compétitions
- **Méthode**: GET
- **URL**: `/competitions`
- **Description**: Retourne la liste de toutes les compétitions.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "name": "String",
      "dateDebut": "String",
      "dateFin": "String",
      "description": "String"
    }
  ]
  ```

### 7. Créer une compétition
- **Méthode**: POST
- **URL**: `/competitions`
- **Description**: Permet de créer une nouvelle compétition (réservé aux administrateurs).
- **Réponse**:
  ```json
  {
    "id": "Long",
    "name": "String",
    "dateDebut": "String",
    "dateFin": "String",
    "description": "String"
  }
  ```

### 8. Liste des épreuves
- **Méthode**: GET
- **URL**: `/epreuves`
- **Description**: Retourne la liste de toutes les épreuves.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "name": "String",
      "competitionId": "Long",
      "description": "String"
    }
  ]
  ```

### 9. Créer une épreuve
- **Méthode**: POST
- **URL**: `/epreuves`
- **Description**: Permet de créer une nouvelle épreuve (réservé aux administrateurs).
- **Réponse**:
  ```json
  {
    "id": "Long",
    "name": "String",
    "competitionId": "Long",
    "description": "String"
  }
  ```