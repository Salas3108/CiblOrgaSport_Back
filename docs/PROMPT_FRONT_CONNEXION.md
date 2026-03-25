# Prompt Claude — Connexion Frontend ↔ Backend CiblOrgaSport

Copie-colle ce prompt directement dans Claude pour connecter ton frontend au backend déployé.

---

## PROMPT À COPIER

```
Tu vas m'aider à connecter mon frontend au backend CiblOrgaSport déjà déployé en production.

---

## CONTEXTE BACKEND

**URL de production :** http://137.74.133.131
**GitHub backend :** https://github.com/Salas3108/CiblOrgaSport_Back
**Architecture :** Spring Boot microservices derrière un Spring Cloud Gateway + Nginx

Toutes les requêtes passent par un seul point d'entrée : http://137.74.133.131
Le chemin de l'URL détermine quel microservice répond.

---

## AUTHENTIFICATION

Le backend utilise JWT (JSON Web Tokens).

### Se connecter (obtenir un token)
POST http://137.74.133.131/auth/api/auth/signin
Content-Type: application/json

Body :
{
  "username": "admin",
  "password": "password"
}

Réponse :
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "role": "ADMIN"
}

### Utiliser le token
Toutes les requêtes protégées doivent inclure le header :
Authorization: Bearer <token>

### Rôles disponibles
- ADMIN : accès total
- ATHLETE : accès à ses propres données sportives
- COMMISSAIRE : saisie des résultats et validation
- VOLONTAIRE : gestion des programmes de volontariat

### Inscription
POST http://137.74.133.131/auth/api/auth/signup
Content-Type: application/json

Body :
{
  "username": "jean.dupont",
  "email": "jean@example.com",
  "password": "motdepasse123",
  "role": "ATHLETE"
}

---

## ROUTES API PAR FONCTIONNALITÉ

### Authentification → /auth/**
POST   /auth/api/auth/signin          → Connexion, retourne JWT
POST   /auth/api/auth/signup          → Inscription
GET    /auth/api/auth/me              → Profil utilisateur connecté (auth requise)

### Événements, compétitions, épreuves → /events/** /competitions/** /epreuves/**
GET    /events                        → Liste des événements
GET    /events/{id}                   → Détail d'un événement
POST   /events                        → Créer un événement (ADMIN)
PUT    /events/{id}                   → Modifier un événement (ADMIN)
DELETE /events/{id}                   → Supprimer un événement (ADMIN)
GET    /competitions                  → Liste des compétitions
GET    /competitions/{id}             → Détail d'une compétition
GET    /epreuves                      → Liste des épreuves
GET    /epreuves/{id}                 → Détail d'une épreuve

### Lieux → /lieux/**
GET    /lieux                         → Liste des lieux
GET    /lieux/{id}                    → Détail d'un lieu
POST   /lieux                         → Créer un lieu (ADMIN)

### Billets → /billets/** /api/tickets/**
GET    /billets                       → Liste des billets
POST   /billets                       → Acheter un billet
GET    /api/tickets?spectatorId={id}  → Billets d'un spectateur

### Abonnements → /api/abonnements/**
GET    /api/abonnements               → Liste des abonnements
POST   /api/abonnements               → Souscrire à un abonnement
DELETE /api/abonnements/{id}          → Résilier un abonnement

### Participants (athlètes, commissaires) → /athlete/** /commissaire/**
GET    /athlete/api/athlete           → Liste des athlètes
GET    /athlete/api/athlete/{id}      → Profil d'un athlète
POST   /athlete/api/athlete/register  → Inscrire un athlète à une épreuve
GET    /commissaire/api/commissaire   → Liste des commissaires

### Résultats → /resultats/** /api/resultats/**
GET    /resultats                     → Liste des résultats
GET    /resultats/{id}                → Résultat d'une épreuve
POST   /resultats                     → Saisir un résultat (COMMISSAIRE/ADMIN)
PUT    /resultats/{id}                → Modifier un résultat (COMMISSAIRE/ADMIN)

### Incidents → /incidents/** /api/incidents/**
GET    /incidents                     → Liste des incidents
POST   /incidents                     → Déclarer un incident
PUT    /incidents/{id}                → Mettre à jour un incident
DELETE /incidents/{id}                → Supprimer un incident (ADMIN)

### Notifications → /notifications/** /api/notifications/**
GET    /notifications                 → Notifications de l'utilisateur connecté
PUT    /notifications/{id}/lu         → Marquer comme lue

### Volontaires → /api/v1/volunteers/**
GET    /api/v1/volunteers             → Liste des volontaires
POST   /api/v1/volunteers             → Créer un profil volontaire
GET    /api/v1/admin/programs         → Programmes de volontariat (ADMIN)
POST   /api/v1/admin/programs         → Créer un programme (ADMIN)

### Analytics → /api/analytics/**  (ADMIN uniquement)
GET    /api/analytics/events/live     → 50 derniers événements en temps réel
GET    /api/analytics/daily?date=     → Stats journalières
GET    /api/analytics/top/competitions → Top 5 compétitions vues

---

## GESTION DES ERREURS

Le backend retourne des codes HTTP standard :
- 200 OK → succès
- 201 Created → ressource créée
- 400 Bad Request → données invalides
- 401 Unauthorized → token manquant ou expiré → rediriger vers /login
- 403 Forbidden → rôle insuffisant → afficher message d'accès refusé
- 404 Not Found → ressource introuvable
- 500 Internal Server Error → erreur serveur

En cas de 401, le token JWT a expiré (durée de vie : 24h).
Il faut redemander à l'utilisateur de se reconnecter.

---

## CORS

Le gateway gère les CORS. Les origines autorisées incluent localhost en développement.
Si tu obtiens une erreur CORS en production, indique-moi l'URL exacte de ton frontend
déployé pour que je l'ajoute à la configuration.

---

## CE QUE JE VEUX QUE TU FASSES

[DÉCRIS ICI TON FRONTEND]

Exemples :
- "Mon frontend est en React avec Axios. Je veux afficher la liste des événements sur la page d'accueil."
- "Mon frontend est en Angular. Je veux gérer la connexion JWT avec un intercepteur HTTP."
- "Mon frontend est en Vue.js. Je veux que seules les pages admin soient accessibles au rôle ADMIN."
- "Mon frontend est en Next.js. Je veux créer un service d'authentification complet."

Technologie frontend utilisée : [React / Vue / Angular / Next.js / autre]
Ce que je veux connecter en priorité : [authentification / événements / résultats / autre]
```

---

## NOTES D'UTILISATION

- Remplace la section **"CE QUE JE VEUX QUE TU FASSES"** par ta demande spécifique
- Si ton frontend tourne localement, l'URL de base reste `http://137.74.133.131`
- Pour les requêtes authentifiées, stocke le token dans `localStorage` ou un cookie `httpOnly`
- Le token expire après **24 heures** — prévoir une redirection vers /login en cas de 401
- En développement, utilise un proxy dans ta config (Vite, webpack, etc.) pour éviter les CORS
