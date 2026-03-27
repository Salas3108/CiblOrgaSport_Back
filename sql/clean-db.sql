-- ══════════════════════════════════════════════════════════════
-- clean-db.sql — Vider toutes les tables CiblOrgaSport
-- Base : glop (PostgreSQL)
-- Ordre : enfants → parents (inverse des insertions)
-- ══════════════════════════════════════════════════════════════

SET session_replication_role = 'replica';

TRUNCATE TABLE weekly_stats       RESTART IDENTITY CASCADE;
TRUNCATE TABLE daily_stats        RESTART IDENTITY CASCADE;
TRUNCATE TABLE event_log          RESTART IDENTITY CASCADE;
TRUNCATE TABLE notification       RESTART IDENTITY CASCADE;
TRUNCATE TABLE abonnements        RESTART IDENTITY CASCADE;
TRUNCATE TABLE ticket             RESTART IDENTITY CASCADE;
TRUNCATE TABLE athlete_position   RESTART IDENTITY CASCADE;
TRUNCATE TABLE fan_zone_service   RESTART IDENTITY CASCADE;
TRUNCATE TABLE fan_zone           RESTART IDENTITY CASCADE;
TRUNCATE TABLE incident           RESTART IDENTITY CASCADE;
TRUNCATE TABLE volunteer_tasks    RESTART IDENTITY CASCADE;
TRUNCATE TABLE volunteers         RESTART IDENTITY CASCADE;
TRUNCATE TABLE resultats          RESTART IDENTITY CASCADE;
TRUNCATE TABLE epreuve_athlete_assignments RESTART IDENTITY CASCADE;
TRUNCATE TABLE epreuve_athletes   RESTART IDENTITY CASCADE;
TRUNCATE TABLE epreuve_equipes    RESTART IDENTITY CASCADE;
TRUNCATE TABLE epreuve            RESTART IDENTITY CASCADE;
TRUNCATE TABLE competition        RESTART IDENTITY CASCADE;
TRUNCATE TABLE event              RESTART IDENTITY CASCADE;
TRUNCATE TABLE athletes           RESTART IDENTITY CASCADE;
TRUNCATE TABLE equipes            RESTART IDENTITY CASCADE;
TRUNCATE TABLE messages           RESTART IDENTITY CASCADE;
TRUNCATE TABLE user_documents     RESTART IDENTITY CASCADE;
TRUNCATE TABLE users              RESTART IDENTITY CASCADE;
TRUNCATE TABLE lieu               RESTART IDENTITY CASCADE;

SET session_replication_role = 'DEFAULT';

-- Vérification
SELECT tablename, 'OK' as status
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN (
    'lieu','users','user_documents','equipes','athletes',
    'event','competition','epreuve','epreuve_equipes','epreuve_athletes',
    'epreuve_athlete_assignments','messages','resultats',
    'volunteers','volunteer_tasks','incident',
    'fan_zone','fan_zone_service','athlete_position',
    'abonnements','ticket','notification',
    'event_log','daily_stats','weekly_stats'
  )
ORDER BY tablename;
