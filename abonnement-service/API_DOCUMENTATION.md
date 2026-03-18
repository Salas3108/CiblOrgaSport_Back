# Documentation API - Service Abonnements

## Endpoints

### 1. Récupérer les abonnements d'un utilisateur
- **Méthode**: GET
- **URL**: `/api/abonnements/user/{userId}`
- **Description**: Retourne la liste des abonnements d'un utilisateur.
- **Réponse**:
  ```json
  [
    {
      "id": "UUID",
      "userId": "Long",
      "competitionId": "Long",
      "dateAbonnement": "LocalDateTime",
      "notificationsActives": "boolean",
      "status": "AbonnementStatus"
    }
  ]
  ```

### 2. S'abonner à une compétition
- **Méthode**: POST
- **URL**: `/api/abonnements/subscribe`
- **Paramètres**:
  - `userId`: ID de l'utilisateur
  - `competitionId`: ID de la compétition
- **Description**: Permet à un utilisateur de s'abonner à une compétition.
- **Réponse**:
  ```json
  {
    "id": "UUID",
    "userId": "Long",
    "competitionId": "Long",
    "dateAbonnement": "LocalDateTime",
    "notificationsActives": "boolean",
    "status": "AbonnementStatus"
  }
  ```

### 3. Se désabonner d'une compétition
- **Méthode**: DELETE
- **URL**: `/api/abonnements/unsubscribe`
- **Paramètres**:
  - `userId`: ID de l'utilisateur
  - `competitionId`: ID de la compétition
- **Description**: Permet à un utilisateur de se désabonner d'une compétition.
- **Réponse**:
  ```json
  {
    "id": "UUID",
    "userId": "Long",
    "competitionId": "Long",
    "dateAbonnement": "LocalDateTime",
    "notificationsActives": "boolean",
    "status": "AbonnementStatus"
  }
  ```

### 4. Récupérer les abonnés d'une compétition (interne)
- **Méthode**: GET
- **URL**: `/api/abonnements/internal/competition/{competitionId}/subscribers`
- **Description**: Retourne les IDs des utilisateurs abonnés à une compétition avec notifications actives.
- **Réponse**:
  ```json
  [
    123,
    456,
    789
  ]
  ```