# Documentation API - Service Résultats

## Endpoints

### 1. Récupérer le classement d'une épreuve
- **Méthode**: GET
- **URL**: `/api/public/resultats/epreuves/{epreuveId}`
- **Description**: Retourne le classement pour une épreuve spécifique.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "classement": "Integer",
      "medaille": "String",
      "qualification": "boolean",
      "valeurPrincipale": "String",
      "unite": "String"
    }
  ]
  ```

### 2. Créer ou mettre à jour un résultat (Commissaire)
- **Méthode**: POST
- **URL**: `/api/commissaire/resultats`
- **Description**: Permet à un commissaire de créer ou mettre à jour un résultat.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "classement": "Integer",
    "medaille": "String",
    "qualification": "boolean",
    "valeurPrincipale": "String",
    "unite": "String"
  }
  ```

### 3. Publier un résultat (Commissaire)
- **Méthode**: POST
- **URL**: `/api/commissaire/resultats/{id}/publier`
- **Description**: Permet à un commissaire de publier un résultat.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "classement": "Integer",
    "medaille": "String",
    "qualification": "boolean",
    "valeurPrincipale": "String",
    "unite": "String",
    "published": "boolean"
  }
  ```

### 4. Supprimer un résultat
- **Méthode**: DELETE
- **URL**: `/api/commissaire/resultats/{id}`
- **Description**: Supprime un résultat spécifique.
- **Réponse**: HTTP 204 No Content