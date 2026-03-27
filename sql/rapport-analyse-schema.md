# Rapport d'analyse — Schéma CiblOrgaSport

> Base de données : `glop` (PostgreSQL)
> Date d'analyse : 2026-03-25
> Projet : Championnats d'Europe de Natation 2026 — Paris

---

## 1. Liste de toutes les tables (25 tables)

| Service | Entité Java | Table réelle | Remarque |
|---|---|---|---|
| auth-service | User | `users` | @Table(name="users") |
| auth-service | (ElementCollection) | `user_documents` | générée par Hibernate |
| event-service | Event | `event` | pas de @Table → nom classe |
| event-service | Competition | `competition` | @Table(name="competition") |
| event-service | Epreuve | `epreuve` | pas de @Table → nom classe |
| event-service | (ElementCollection) | `epreuve_equipes` | @CollectionTable explicite |
| event-service | (ElementCollection) | `epreuve_athletes` | @CollectionTable explicite |
| participants-service | Equipe | `equipes` | @Table(name="equipes") |
| participants-service | Athlete | `athletes` | @Table(name="athletes") |
| participants-service | EpreuveAthleteAssignment | `epreuve_athlete_assignments` | @Table explicite |
| participants-service | Message | `messages` | @Table(name="messages") |
| resultats-service | Resultat | `resultats` | @Table(name="resultats") |
| lieu-service | Lieu | `lieu` | pas de @Table → nom classe |
| volunteer-service | Volunteer | `volunteers` | @Table explicite |
| volunteer-service | VolunteerTask | `volunteer_tasks` | @Table explicite |
| incident-service | Incident | `incident` | @Entity(name="IncidentModel") sans @Table → nom classe |
| analytics-service | EventLog | `event_log` | @Table explicite |
| analytics-service | DailyStats | `daily_stats` | @Table explicite |
| analytics-service | WeeklyStats | `weekly_stats` | @Table explicite |
| notifications-service | Notification | `notification` | @Table explicite |
| abonnement-service | Abonnement | `abonnements` | @Table explicite |
| billetterie | Ticket | `ticket` | @Entity(name="TicketModel") sans @Table → nom classe |
| geolocation-service | FanZone | `fan_zone` | @Table explicite |
| geolocation-service | FanZoneService | `fan_zone_service` | @Table explicite, FK → fan_zone |
| geolocation-service | AthletePosition | `athlete_position` | @Table explicite |

---

## 2. Colonnes exactes par table (snake_case, Hibernate naming)

### `users`
| Colonne | Type | Nullable | Unique | Remarque |
|---|---|---|---|---|
| id | BIGSERIAL | NO | PK | GenerationType.IDENTITY |
| username | VARCHAR(50) | NO | YES | @Column(unique=true) |
| email | VARCHAR(100) | NO | YES | @Column(unique=true) |
| password | VARCHAR(120) | NO | NO | BCrypt |
| role | VARCHAR | YES | NO | enum: USER, ATHLETE, ADMIN, COMMISSAIRE, VOLONTAIRE |
| created_at | TIMESTAMP | YES | NO | |
| updated_at | TIMESTAMP | YES | NO | |
| validated | BOOLEAN | NO | NO | default false |
> ⚠️ Pas de colonne `enabled` — c'est `isEnabled()`, méthode calculée Java

### `user_documents`
| Colonne | Type |
|---|---|
| user_id | BIGINT FK → users.id |
| documents | VARCHAR |

### `event`
| Colonne | Type |
|---|---|
| id | BIGSERIAL PK |
| name | VARCHAR |
| date_debut | DATE |
| date_fin | DATE |
| description | VARCHAR |
| pays_hote | VARCHAR |

### `competition`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| event_id | BIGINT FK → event.id | |
| discipline | VARCHAR | enum: NATATION, WATER_POLO, NATATION_ARTISTIQUE, PLONGEON, EAU_LIBRE |

### `epreuve`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| nom | VARCHAR | |
| description | VARCHAR | |
| date_heure | TIMESTAMP | |
| duree_minutes | INTEGER | |
| competition_id | BIGINT FK → competition.id | |
| lieu_id | BIGINT | référence lieu.id (pas de FK JPA) |
| type_epreuve | VARCHAR | enum: INDIVIDUELLE, COLLECTIVE |
| genre_epreuve | VARCHAR | enum: FEMININ, MASCULIN, MIXTE |
| niveau_epreuve | VARCHAR | enum: QUALIFICATION, QUART_DE_FINALE, DEMI_FINALE, FINALE |
| statut | VARCHAR | enum: PLANIFIE, EN_COURS, TERMINE, REPORTE, ANNULE — default PLANIFIE |

### `epreuve_equipes`
| Colonne | Type |
|---|---|
| epreuve_id | BIGINT FK → epreuve.id |
| equipe_id | BIGINT |

### `epreuve_athletes`
| Colonne | Type |
|---|---|
| epreuve_id | BIGINT FK → epreuve.id |
| athlete_id | BIGINT |

### `lieu`
| Colonne | Type |
|---|---|
| id | BIGSERIAL PK |
| nom | VARCHAR |
| adresse | VARCHAR |
| ville | VARCHAR |
| code_postal | VARCHAR |
| pays | VARCHAR |
| capacite_spectateurs | INTEGER |

### `equipes`
| Colonne | Type |
|---|---|
| id | BIGSERIAL PK |
| nom | VARCHAR |
| pays | VARCHAR |

### `athletes`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGINT PK | **Assigné manuellement** (pas d'auto-increment) — copie de users.id |
| username | VARCHAR | unique |
| nom | VARCHAR | |
| prenom | VARCHAR | |
| date_naissance | DATE | |
| pays | VARCHAR | |
| valide | BOOLEAN | |
| sexe | VARCHAR | enum: MASCULIN, FEMININ |
| observation | VARCHAR | |
| motif_refus | VARCHAR | |
| equipe_id | BIGINT FK → equipes.id | LAZY fetch |
| certificat_medical | bytea | embedded (AthleteDocs) |
| passport | bytea | embedded (AthleteDocs) |
| document_genre | VARCHAR | embedded (AthleteDocs) |

### `epreuve_athlete_assignments`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| epreuve_id | BIGINT | NOT NULL |
| athlete_id | BIGINT | NOT NULL |
| statut_participation | VARCHAR | enum: INSCRIT, EN_COURS, TERMINE, FORFAIT — default INSCRIT |
| date_forfait | TIMESTAMP | nullable |
| details_performance | TEXT | nullable |
> UNIQUE (epreuve_id, athlete_id)

### `messages`
| Colonne | Type |
|---|---|
| id | BIGSERIAL PK |
| athlete_id | BIGINT |
| contenu | VARCHAR |
| created_at | TIMESTAMP |

### `resultats`
| Colonne | Type | Remarque |
|---|---|---|
| id_resultat | BIGSERIAL PK | |
| classement | INTEGER | nullable |
| medaille | VARCHAR | enum: OR, ARGENT, BRONZE — nullable |
| qualification | BOOLEAN | |
| valeur_principale | VARCHAR(50) | nullable |
| unite | VARCHAR(20) | nullable |
| details_performance | jsonb | columnDefinition="jsonb" |
| type_performance | VARCHAR | enum: TEMPS, POINTS, SCORE |
| athlete_id | BIGINT | nullable (pas de FK JPA) |
| equipe_id | BIGINT | nullable (pas de FK JPA) |
| epreuve_id | BIGINT | NOT NULL |
| statut | VARCHAR | enum: EN_ATTENTE, VALIDE |
| published | BOOLEAN | |
> UNIQUE (epreuve_id, athlete_id)
> UNIQUE (epreuve_id, equipe_id)

### `volunteers`
| Colonne | Type | Remarque |
|---|---|---|
| id | UUID PK | GenerationType.AUTO |
| auth_user_id | BIGINT | unique, référence users.id |
| first_name | VARCHAR | NOT NULL |
| last_name | VARCHAR | NOT NULL |
| email | VARCHAR | NOT NULL |
| phone_number | VARCHAR | nullable |
| languages | VARCHAR | CSV (ex: "Français,Anglais") |
| preferred_task_types | VARCHAR | CSV (ex: "ACCUEIL,SECURITE") |
| active | BOOLEAN | default true |
| availabilities_json | TEXT | JSON: [{"dayOfWeek":"MONDAY","startTime":"08:00","endTime":"12:00"}] |

### `volunteer_tasks`
| Colonne | Type | Remarque |
|---|---|---|
| id | UUID PK | GenerationType.AUTO |
| title | VARCHAR | NOT NULL |
| description | VARCHAR(1000) | nullable |
| task_date | DATE | NOT NULL |
| start_time | TIME | NOT NULL |
| end_time | TIME | NOT NULL |
| location | VARCHAR | NOT NULL |
| task_type | VARCHAR | enum TaskType — NOT NULL |
| assigned_volunteer_ids | VARCHAR | UUID stocké en CSV (max 1 volontaire) |
| required_languages | VARCHAR | CSV langues requises |

### `incident`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| description | TEXT | NOT NULL |
| impact_level | VARCHAR | enum: FAIBLE, MOYEN, ELEVE, CRITIQUE — NOT NULL |
| type | VARCHAR | enum: SECURITE, TECHNIQUE, METEO, MEDICAL, AUTRE — NOT NULL |
| lieu_id | BIGINT | NOT NULL, référence lieu.id |
| competition_id | BIGINT | nullable |
| status | VARCHAR | enum: ACTIF, RESOLU — NOT NULL |
| reported_by | VARCHAR | NOT NULL (username du déclarant, pas un FK) |
| reported_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |
| resolved_at | TIMESTAMP | |

### `notification`
| Colonne | Type | Remarque |
|---|---|---|
| id_notification | BIGSERIAL PK | |
| type | VARCHAR | NOT NULL |
| contenu | TEXT | NOT NULL |
| date_envoi | TIMESTAMP | NOT NULL |
| id_event | BIGINT | nullable |
| id_spectateur | BIGINT | nullable |
| source_event_id | VARCHAR | nullable |
| lu | BOOLEAN | default false |
> UNIQUE (source_event_id, id_spectateur)

### `abonnements`
| Colonne | Type | Remarque |
|---|---|---|
| id | UUID PK | GenerationType.UUID |
| user_id | BIGINT | NOT NULL |
| competition_id | BIGINT | NOT NULL |
| date_abonnement | TIMESTAMP | default now() |
| notifications_actives | BOOLEAN | default true |
| status | VARCHAR | enum: ACTIF, DESABONNE, SUSPENDU — default ACTIF |
> UNIQUE (user_id, competition_id)

### `ticket`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| category | VARCHAR | ex: TRIBUNE, VIP, PELOUSE |
| base_price | DOUBLE PRECISION | |
| spectator_id | BIGINT | référence users.id (pas de FK JPA) |
| epreuve_id | BIGINT | référence epreuve.id (pas de FK JPA) |

### `fan_zone`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| nom | VARCHAR | NOT NULL |
| description | TEXT | |
| latitude | DOUBLE PRECISION | NOT NULL |
| longitude | DOUBLE PRECISION | NOT NULL |
| capacite_max | INTEGER | |
| adresse | VARCHAR | |

### `fan_zone_service`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| fan_zone_id | BIGINT FK → fan_zone.id | NOT NULL |
| type_service | VARCHAR | enum: ECRAN_GEANT, RESTAURATION, BOUTIQUE, MEDICAL — NOT NULL |

### `athlete_position`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| athlete_id | BIGINT | NOT NULL, référence athletes.id (pas de FK JPA) |
| latitude | DOUBLE PRECISION | NOT NULL |
| longitude | DOUBLE PRECISION | NOT NULL |
| timestamp | TIMESTAMP | NOT NULL |

### `event_log`
| Colonne | Type | Remarque |
|---|---|---|
| id | BIGSERIAL PK | |
| user_id | BIGINT | nullable |
| user_role | VARCHAR(50) | nullable |
| event_type | VARCHAR(50) | nullable — index: idx_event_log_event_type |
| endpoint | VARCHAR(255) | nullable |
| http_method | VARCHAR(10) | nullable |
| status_code | INTEGER | nullable |
| duration_ms | BIGINT | nullable |
| ip_address | VARCHAR(50) | nullable |
| timestamp | TIMESTAMP | NOT NULL — index: idx_event_log_timestamp |
| metadata | TEXT | nullable (JSON) |

### `daily_stats`
| Colonne | Type | Default |
|---|---|---|
| id | BIGSERIAL PK | |
| stat_date | DATE | unique |
| total_connections | INTEGER | 0 |
| unique_users | INTEGER | 0 |
| connections_athletes | INTEGER | 0 |
| connections_spectateurs | INTEGER | 0 |
| connections_commissaires | INTEGER | 0 |
| connections_volontaires | INTEGER | 0 |
| connections_admins | INTEGER | 0 |
| total_page_views | INTEGER | 0 |
| total_notifications_sent | INTEGER | 0 |
| notifications_resultats | INTEGER | 0 |
| notifications_securite | INTEGER | 0 |
| notifications_events | INTEGER | 0 |
| total_subscriptions | INTEGER | 0 |
| total_competition_views | INTEGER | 0 |
| total_result_views | INTEGER | 0 |
| avg_session_duration_ms | BIGINT | 0 |
| avg_response_time_ms | BIGINT | 0 |
| total_incidents | INTEGER | 0 |
| calculated_at | TIMESTAMP | |

### `weekly_stats`
| Colonne | Type | Default |
|---|---|---|
| id | BIGSERIAL PK | |
| week_start | DATE | unique |
| week_end | DATE | |
| total_connections | INTEGER | 0 |
| unique_users | INTEGER | 0 |
| peak_day | DATE | |
| peak_connections | INTEGER | 0 |
| top_competition_id | BIGINT | |
| top_competition_views | INTEGER | 0 |
| total_notifications_sent | INTEGER | 0 |
| total_new_subscriptions | INTEGER | 0 |
| avg_daily_connections | DOUBLE PRECISION | 0.0 |
| growth_rate_percent | DOUBLE PRECISION | 0.0 |
| calculated_at | TIMESTAMP | |

---

## 3. FK et dépendances entre tables

```
event
└── competition (FK: event_id)
    └── epreuve (FK: competition_id)
        ├── epreuve_equipes (FK: epreuve_id)
        ├── epreuve_athletes (FK: epreuve_id)
        └── epreuve_athlete_assignments (FK: epreuve_id)

equipes
└── athletes (FK: equipe_id)

lieu
├── epreuve (lieu_id — pas de FK JPA)
└── incident (lieu_id — FK NOT NULL)

users
└── user_documents (FK: user_id)

fan_zone
└── fan_zone_service (FK: fan_zone_id)

-- Références logiques sans FK JPA :
volunteers.auth_user_id → users.id
abonnements.user_id → users.id
abonnements.competition_id → competition.id
ticket.spectator_id → users.id
ticket.epreuve_id → epreuve.id
athlete_position.athlete_id → athletes.id
notification.id_spectateur → users.id
incident.competition_id → competition.id (nullable)
resultats.athlete_id → athletes.id
resultats.equipe_id → equipes.id
resultats.epreuve_id → epreuve.id
```

---

## 4. Tous les enums avec leurs valeurs exactes

| Enum | Service | Valeurs |
|---|---|---|
| Role | auth-service | USER, ATHLETE, ADMIN, COMMISSAIRE, VOLONTAIRE |
| Discipline | event-service | NATATION, WATER_POLO, NATATION_ARTISTIQUE, PLONGEON, EAU_LIBRE |
| NiveauEpreuve | event-service | QUALIFICATION, QUART_DE_FINALE, DEMI_FINALE, FINALE |
| TypeEpreuve | event-service | INDIVIDUELLE, COLLECTIVE |
| GenreEpreuve | event-service | FEMININ, MASCULIN, MIXTE |
| StatutEpreuve | event-service | PLANIFIE, EN_COURS, TERMINE, REPORTE, ANNULE |
| Sexe | participants-service | MASCULIN, FEMININ |
| StatutParticipation | participants-service | INSCRIT, EN_COURS, TERMINE, FORFAIT |
| Medaille | resultats-service | OR, ARGENT, BRONZE |
| ResultatStatut | resultats-service | EN_ATTENTE, VALIDE |
| TypePerformance | resultats-service | TEMPS, POINTS, SCORE |
| TaskType | volunteer-service | ACCUEIL, ORIENTATION, SUPPORT_LOGISTIQUE, SECURITE, PREMIERS_SECOURS, ACCOMPAGNEMENT_ATHLETES, DISTRIBUTION_EAU, NETTOYAGE, BILLETTERIE, INFORMATION, AUTRE |
| ImpactLevel | incident-service | FAIBLE, MOYEN, ELEVE, CRITIQUE |
| IncidentType | incident-service | SECURITE, TECHNIQUE, METEO, MEDICAL, AUTRE |
| IncidentStatus | incident-service | ACTIF, RESOLU |
| AbonnementStatus | abonnement-service | ACTIF, DESABONNE, SUSPENDU |
| TypeService | geolocation-service | ECRAN_GEANT, RESTAURATION, BOUTIQUE, MEDICAL |
| EventType | analytics-service | USER_LOGIN, USER_LOGOUT, USER_REGISTER, EVENT_VIEW, COMPETITION_VIEW, EPREUVE_VIEW, RESULT_VIEW, ATHLETE_PROFILE_VIEW, NOTIFICATION_SENT, NOTIFICATION_SUBSCRIBED, VOLUNTEER_VALIDATED, INCIDENT_DECLARED, PAGE_VIEW |

---

## 5. Ordre d'insertion (parents → enfants)

```
1.  lieu
2.  users + user_documents
3.  equipes
4.  athletes              (FK → equipes)
5.  event
6.  competition           (FK → event)
7.  epreuve               (FK → competition, lieu_id)
8.  epreuve_equipes       (FK → epreuve)
9.  epreuve_athletes      (FK → epreuve)
10. epreuve_athlete_assignments (FK → epreuve, athletes)
11. resultats             (FK → epreuve, ref athletes/equipes)
12. volunteers            (ref users.id via auth_user_id)
13. volunteer_tasks
14. incident              (FK lieu_id, ref competition)
15. fan_zone
16. fan_zone_service      (FK → fan_zone)
17. athlete_position      (ref athletes.id)
18. abonnements           (ref users.id, competition.id)
19. ticket                (ref users.id, epreuve.id)
20. notification          (ref users.id, event.id)
21. event_log
22. daily_stats
23. weekly_stats
```

## 6. Ordre TRUNCATE pour clean-db.sql (inverse)

```sql
weekly_stats → daily_stats → event_log → notification → ticket
→ abonnements → athlete_position → fan_zone_service → fan_zone
→ incident → volunteer_tasks → volunteers → resultats
→ epreuve_athlete_assignments → epreuve_athletes → epreuve_equipes
→ epreuve → competition → event → athletes → equipes
→ user_documents → users → lieu
```

---

## 7. Points techniques critiques

| Point | Détail |
|---|---|
| `athletes.id` | PK assignée manuellement = même valeur que `users.id` (pas d'auto-increment) |
| `incident.reported_by` | VARCHAR — stocker le username (String), pas un Long FK |
| `abonnements.id` | UUID — utiliser `gen_random_uuid()` |
| `volunteers.id` | UUID — utiliser `gen_random_uuid()` |
| `resultats.details_performance` | jsonb — utiliser `'{}'::jsonb` |
| `users.isEnabled()` | méthode calculée, pas de colonne en base |
| Role SPECTATEUR | N'existe PAS → utiliser `VOLONTAIRE`→`VOLONTAIRE`, spectateurs → `USER` |
| Role COMMISSAIRE_SPORTIF | N'existe PAS → utiliser `COMMISSAIRE` |

---

## 8. Flux de validation métier

### Enregistrement utilisateur
1. Vérifier unicité username + email
2. Encoder password (BCrypt)
3. Définir `validated` :
   - ATHLETE / VOLONTAIRE → `false` (attente validation admin)
   - Autres → `true`
4. `isEnabled()` = `role == ADMIN` OU `validated == true`

### Profil volontaire
1. INSERT user avec `validated = false`
2. UPDATE `validated = true` (par admin)
3. INSERT profil volunteer (languages CSV, preferred_task_types CSV, availabilities_json)

### Résultats
- `qualification = true` pour les 8 premiers d'une QUALIFICATION
- `medaille` : OR (1er), ARGENT (2e), BRONZE (3e) dans les FINALE uniquement
- `statut = VALIDE`, `published = true` pour simulation complète
