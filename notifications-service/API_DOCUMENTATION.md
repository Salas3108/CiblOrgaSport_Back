# Documentation API - Service Notifications

## Endpoints

### 1. Historique des notifications d'un spectateur
- **Méthode**: GET
- **URL**: `/api/notifications/spectateur/{spectateurId}`
- **Description**: Retourne toutes les notifications d'un spectateur, triées par date d'envoi (les plus récentes en premier).
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "type": "String",
      "contenu": "String",
      "dateEnvoi": "LocalDateTime",
      "idEvent": "Long",
      "idSpectateur": "Long",
      "sourceEventId": "String",
      "lu": "boolean"
    }
  ]
  ```

### 2. Notifications non lues d'un spectateur
- **Méthode**: GET
- **URL**: `/api/notifications/spectateur/{spectateurId}/non-lues`
- **Description**: Retourne uniquement les notifications non lues d'un spectateur.
- **Réponse**:
  ```json
  [
    {
      "id": "Long",
      "type": "String",
      "contenu": "String",
      "dateEnvoi": "LocalDateTime",
      "idEvent": "Long",
      "idSpectateur": "Long",
      "sourceEventId": "String",
      "lu": "boolean"
    }
  ]
  ```

### 3. Compteur de notifications non lues
- **Méthode**: GET
- **URL**: `/api/notifications/spectateur/{spectateurId}/compteur`
- **Description**: Retourne le nombre de notifications non lues pour un spectateur.
- **Réponse**:
  ```json
  {
    "nonLues": 5
  }
  ```

### 4. Marquer une notification comme lue
- **Méthode**: PATCH
- **URL**: `/api/notifications/{id}/lue`
- **Description**: Marque une notification spécifique comme lue.
- **Réponse**:
  ```json
  {
    "id": "Long",
    "type": "String",
    "contenu": "String",
    "dateEnvoi": "LocalDateTime",
    "idEvent": "Long",
    "idSpectateur": "Long",
    "sourceEventId": "String",
    "lu": true
  }
  ```

### 5. Marquer toutes les notifications comme lues
- **Méthode**: PATCH
- **URL**: `/api/notifications/spectateur/{spectateurId}/tout-lire`
- **Description**: Marque toutes les notifications d'un spectateur comme lues.
- **Réponse**:
  ```json
  {
    "mis_a_jour": 10
  }
  ```

### 6. Supprimer une notification
- **Méthode**: DELETE
- **URL**: `/api/notifications/{id}`
- **Description**: Supprime une notification spécifique.
- **Réponse**: HTTP 204 No Content

## WebSocket STOMP

### 7. Connexion STOMP via SockJS
- **Protocole**: STOMP over SockJS
- **URL**: `http://localhost:8089/ws`
- **Description**: Point d'entrée principal pour les clients front qui veulent recevoir les notifications en temps réel.

### 8. Connexion STOMP via WebSocket natif
- **Protocole**: STOMP over WebSocket
- **URL**: `ws://localhost:8089/ws-native`
- **Description**: Endpoint natif (utile pour tests sans SockJS).

### 9. Abonnement aux notifications d'un spectateur
- **Action STOMP**: SUBSCRIBE
- **Destination**: `/topic/notifications/{spectateurId}`
- **Description**: Le client reçoit en push chaque nouvelle notification adressée à ce spectateur.

### 10. Format du message push
- **Type payload**: `NotificationDTO`
- **Exemple**:
  ```json
  {
    "id": 123,
    "type": "INCIDENT",
    "contenu": "Nouvel incident (CHUTE) à Virage 3: Spectateur tombé",
    "dateEnvoi": "2026-03-15T10:25:30",
    "idEvent": 45,
    "idSpectateur": 1,
    "sourceEventId": "incident-987",
    "lu": false
  }
  ```

### 11. Préfixes STOMP
- **Broker (serveur vers clients)**: `/topic`
- **Application (clients vers serveur)**: `/app`
- **Remarque**: Aucun endpoint `@MessageMapping` n'est exposé actuellement. Le front est en mode écoute (subscribe-only).

### 12. Gateway et authentification
- **REST**: Les routes `/api/notifications/**` passent par le gateway et nécessitent un token JWT (`Authorization: Bearer <token>`).
- **WebSocket**: Les endpoints `/ws` et `/ws-native` sont exposés directement par `notifications-service` (port `8089`).
- **Remarque**: En l'état, le gateway ne route pas `/ws` ni `/ws-native`.