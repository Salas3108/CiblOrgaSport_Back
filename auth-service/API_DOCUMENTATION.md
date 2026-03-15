# Documentation API - Service Authentification

## Endpoints

### 1. Upload de documents (Athlète)
- **Méthode**: POST
- **URL**: `/auth/user/upload-documents`
- **Description**: Permet à un athlète de soumettre des documents pour validation.
- **Réponse**:
  ```json
  "Documents envoyés pour validation."
  ```

### 2. Validation d'un athlète (Admin)
- **Méthode**: POST
- **URL**: `/auth/admin/validate-athlete`
- **Description**: Permet à un administrateur de valider ou rejeter un athlète.
- **Réponse**:
  ```json
  "Athlète validé." ou "Athlète rejeté."
  ```

### 3. Liste des athlètes (Admin)
- **Méthode**: GET
- **URL**: `/auth/admin/athletes`
- **Description**: Retourne la liste des athlètes, avec possibilité de filtrer par statut de validation.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "username": "String",
      "email": "String",
      "role": "String",
      "validated": "boolean"
    }
  ]
  ```

### 4. Récupérer un utilisateur par username
- **Méthode**: GET
- **URL**: `/auth/user/username/{username}`
- **Description**: Retourne les informations d'un utilisateur par son username.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "username": "String",
    "email": "String",
    "role": "String",
    "validated": "boolean"
  }
  ```

### 5. Vérifier l'existence d'un utilisateur
- **Méthode**: GET
- **URL**: `/auth/user/exists/{id}`
- **Description**: Vérifie si un utilisateur existe par son ID.
- **Réponse**:
  ```json
  true ou false
  ```