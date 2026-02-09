# Endpoint : Liste des Athlètes (ADMIN)

**URL** : `/auth/admin/athletes`

**Méthode** : `GET`

**Description** :
Retourne la liste des utilisateurs ayant le rôle ATHLETE. Permet de filtrer par statut de validation.

**Sécurité** :
- Requiert un token JWT d'un utilisateur avec le rôle `ADMIN` (header `Authorization: Bearer <token>`)

**Paramètres Query** :
- `validated` (optionnel, booléen) :
    - `true` : retourne uniquement les athlètes validés
    - `false` : retourne uniquement les athlètes non validés
    - non renseigné : retourne tous les athlètes

**Réponse** :
- 200 OK : Liste JSON des utilisateurs athlètes
- 403 Forbidden : Si l'utilisateur n'est pas admin ou token invalide

**Exemple d'appel (curl)** :
```bash
curl -X GET "http://localhost:8080/auth/admin/athletes?validated=true" \
     -H "Authorization: Bearer <votre_token_admin>"
```

**Exemple de réponse** :
```json
[
  {
    "id": 2,
    "username": "athlete1",
    "email": "athlete1@example.com",
    "role": "ATHLETE",
    "validated": true,
    ...
  },
  ...
]
```
