-- ============================================================
-- seed-analytics.sql — Données historiques event_log (30 jours)
-- Championnat d'Europe de Natation 2026
-- Exécuter : docker exec -i postgres psql -U admin -d glop < analytics-service/seed-analytics.sql
-- ============================================================

-- Nettoyage propre
TRUNCATE event_log, daily_stats, weekly_stats RESTART IDENTITY;

-- ============================================================
-- HELPER : fonction pour générer un timestamp aléatoire dans une journée
-- ============================================================
CREATE OR REPLACE FUNCTION rand_time(base_day TIMESTAMPTZ, hour_min INT, hour_max INT)
RETURNS TIMESTAMPTZ AS $$
BEGIN
    RETURN base_day
        + (floor(random() * (hour_max - hour_min) + hour_min) || ' hours')::INTERVAL
        + (floor(random() * 60) || ' minutes')::INTERVAL
        + (floor(random() * 60) || ' seconds')::INTERVAL;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- INSERTION : ~50 événements par jour sur 30 jours
-- Jour 0 = aujourd'hui, Jour 29 = il y a 29 jours
-- ============================================================

DO $$
DECLARE
    d INT;
    base TIMESTAMPTZ;
BEGIN
FOR d IN 0..29 LOOP
    base := date_trunc('day', NOW() - (d || ' days')::INTERVAL);

    -- ── Connexions admin (matin) ──────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (1, 'ROLE_ADMIN', 'USER_LOGIN', '/auth/login', 'POST', 200, 42 + floor(random()*30)::INT, '192.168.1.10', rand_time(base,7,8)),
        (1, 'ROLE_ADMIN', 'PAGE_VIEW',  '/admin/dashboard', 'GET', 200, 95 + floor(random()*40)::INT, '192.168.1.10', rand_time(base,8,9)),
        (1, 'ROLE_ADMIN', 'COMPETITION_VIEW', '/competitions/1', 'GET', 200, 80 + floor(random()*50)::INT, '192.168.1.10', rand_time(base,8,10)),
        (1, 'ROLE_ADMIN', 'COMPETITION_VIEW', '/competitions/2', 'GET', 200, 75 + floor(random()*45)::INT, '192.168.1.10', rand_time(base,9,11));

    -- ── Connexions commissaires ───────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (10, 'ROLE_COMMISSAIRE', 'USER_LOGIN', '/auth/login', 'POST', 200, 38 + floor(random()*20)::INT, '10.0.0.8', rand_time(base,7,9)),
        (10, 'ROLE_COMMISSAIRE', 'PAGE_VIEW', '/commissaire/dashboard', 'GET', 200, 110 + floor(random()*60)::INT, '10.0.0.8', rand_time(base,8,10)),
        (11, 'ROLE_COMMISSAIRE', 'USER_LOGIN', '/auth/login', 'POST', 200, 44 + floor(random()*25)::INT, '10.0.0.9', rand_time(base,7,9)),
        (11, 'ROLE_COMMISSAIRE', 'RESULT_VIEW', '/epreuves/45', 'GET', 200, 88 + floor(random()*40)::INT, '10.0.0.9', rand_time(base,9,11)),
        (11, 'ROLE_COMMISSAIRE', 'RESULT_VIEW', '/epreuves/46', 'GET', 200, 92 + floor(random()*35)::INT, '10.0.0.9', rand_time(base,10,12));

    -- ── Connexions athlètes (matin) ───────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (2, 'ROLE_ATHLETE', 'USER_LOGIN', '/auth/login', 'POST', 200, 35 + floor(random()*25)::INT, '10.0.0.5', rand_time(base,6,8)),
        (2, 'ROLE_ATHLETE', 'RESULT_VIEW', '/epreuves/45', 'GET', 200, 78 + floor(random()*40)::INT, '10.0.0.5', rand_time(base,8,10)),
        (2, 'ROLE_ATHLETE', 'ATHLETE_PROFILE_VIEW', '/athlete/2', 'GET', 200, 65 + floor(random()*30)::INT, '10.0.0.5', rand_time(base,9,11)),
        (3, 'ROLE_ATHLETE', 'USER_LOGIN', '/auth/login', 'POST', 200, 41 + floor(random()*20)::INT, '10.0.0.6', rand_time(base,7,9)),
        (3, 'ROLE_ATHLETE', 'COMPETITION_VIEW', '/competitions/1', 'GET', 200, 85 + floor(random()*45)::INT, '10.0.0.6', rand_time(base,9,11)),
        (4, 'ROLE_ATHLETE', 'USER_LOGIN', '/auth/login', 'POST', 200, 39 + floor(random()*22)::INT, '10.0.0.7', rand_time(base,6,8)),
        (4, 'ROLE_ATHLETE', 'RESULT_VIEW', '/epreuves/46', 'GET', 200, 91 + floor(random()*38)::INT, '10.0.0.7', rand_time(base,8,10));

    -- ── Connexions volontaires ────────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (20, 'ROLE_VOLONTAIRE', 'USER_LOGIN', '/auth/login', 'POST', 200, 43 + floor(random()*20)::INT, '10.1.0.1', rand_time(base,7,9)),
        (20, 'ROLE_VOLONTAIRE', 'PAGE_VIEW', '/api/abonnements', 'GET', 200, 105 + floor(random()*50)::INT, '10.1.0.1', rand_time(base,9,11)),
        (21, 'ROLE_VOLONTAIRE', 'USER_LOGIN', '/auth/login', 'POST', 200, 37 + floor(random()*18)::INT, '10.1.0.2', rand_time(base,8,10));

    -- ── Spectateurs / users publics (journée) ────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (100, 'ROLE_USER', 'USER_LOGIN', '/auth/login', 'POST', 200, 52 + floor(random()*30)::INT, '88.12.34.56', rand_time(base,10,12)),
        (100, 'ROLE_USER', 'COMPETITION_VIEW', '/competitions/1', 'GET', 200, 120 + floor(random()*60)::INT, '88.12.34.56', rand_time(base,11,13)),
        (100, 'ROLE_USER', 'COMPETITION_VIEW', '/events/3', 'GET', 200, 98 + floor(random()*55)::INT, '88.12.34.56', rand_time(base,12,14)),
        (101, 'ROLE_USER', 'USER_LOGIN', '/auth/login', 'POST', 200, 48 + floor(random()*28)::INT, '88.12.34.57', rand_time(base,10,12)),
        (101, 'ROLE_USER', 'RESULT_VIEW', '/epreuves/47', 'GET', 200, 110 + floor(random()*50)::INT, '88.12.34.57', rand_time(base,12,14)),
        (102, 'ROLE_USER', 'COMPETITION_VIEW', '/competitions/2', 'GET', 200, 135 + floor(random()*65)::INT, '77.22.11.88', rand_time(base,14,16)),
        (103, 'ROLE_USER', 'PAGE_VIEW', '/events', 'GET', 200, 88 + floor(random()*45)::INT, '92.10.5.43', rand_time(base,15,17)),
        (104, 'ROLE_USER', 'FANZONE_VIEW', '/api/geo/fanzones', 'GET', 200, 75 + floor(random()*40)::INT, '90.45.12.3', rand_time(base,13,15)),
        (104, 'ROLE_USER', 'FANZONE_VIEW', '/api/geo/fanzones/nearby', 'GET', 200, 82 + floor(random()*38)::INT, '90.45.12.3', rand_time(base,14,16));

    -- ── Notifications ─────────────────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (1, 'ROLE_ADMIN', 'NOTIFICATION_SENT', '/notifications/', 'POST', 201, 180 + floor(random()*80)::INT, '192.168.1.10', rand_time(base,10,12)),
        (100, 'ROLE_USER', 'NOTIFICATION_SUBSCRIBED', '/abonnements/', 'POST', 201, 95 + floor(random()*40)::INT, '88.12.34.56', rand_time(base,11,13)),
        (101, 'ROLE_USER', 'NOTIFICATION_SUBSCRIBED', '/api/abonnements/subscribe', 'POST', 201, 88 + floor(random()*35)::INT, '88.12.34.57', rand_time(base,12,14));

    -- ── Incidents ─────────────────────────────────────────────
    IF d % 3 = 0 THEN
        INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
        VALUES
            (1, 'ROLE_ADMIN', 'INCIDENT_DECLARED', '/incidents/', 'POST', 201, 210 + floor(random()*90)::INT, '192.168.1.10', rand_time(base,10,14)),
            (10, 'ROLE_COMMISSAIRE', 'INCIDENT_DECLARED', '/api/incidents/', 'POST', 201, 195 + floor(random()*85)::INT, '10.0.0.8', rand_time(base,14,17));
    END IF;

    -- ── Vues résultats après-midi ─────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (2, 'ROLE_ATHLETE', 'RESULT_VIEW', '/epreuves/47', 'GET', 200, 82 + floor(random()*42)::INT, '10.0.0.5', rand_time(base,14,16)),
        (3, 'ROLE_ATHLETE', 'RESULT_VIEW', '/epreuves/45', 'GET', 200, 79 + floor(random()*38)::INT, '10.0.0.6', rand_time(base,15,17)),
        (100, 'ROLE_USER', 'RESULT_VIEW', '/epreuves/48', 'GET', 200, 115 + floor(random()*55)::INT, '88.12.34.56', rand_time(base,16,18)),
        (105, 'ROLE_USER', 'COMPETITION_VIEW', '/competitions/3', 'GET', 200, 128 + floor(random()*60)::INT, '83.44.22.11', rand_time(base,15,17)),
        (106, 'ROLE_USER', 'COMPETITION_VIEW', '/events/4', 'GET', 200, 102 + floor(random()*48)::INT, '72.33.15.9', rand_time(base,16,18));

    -- ── Profils athlètes ──────────────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (100, 'ROLE_USER', 'ATHLETE_PROFILE_VIEW', '/athlete/2', 'GET', 200, 72 + floor(random()*35)::INT, '88.12.34.56', rand_time(base,11,13)),
        (101, 'ROLE_USER', 'ATHLETE_PROFILE_VIEW', '/athlete/3', 'GET', 200, 68 + floor(random()*32)::INT, '88.12.34.57', rand_time(base,12,14)),
        (102, 'ROLE_USER', 'ATHLETE_PROFILE_VIEW', '/api/athlete/4', 'GET', 200, 74 + floor(random()*36)::INT, '77.22.11.88', rand_time(base,14,16));

    -- ── Déconnexions (soir) ───────────────────────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (1,  'ROLE_ADMIN',       'USER_LOGOUT', '/auth/logout', 'POST', 200, 28 + floor(random()*15)::INT, '192.168.1.10', rand_time(base,18,20)),
        (2,  'ROLE_ATHLETE',     'USER_LOGOUT', '/auth/logout', 'POST', 200, 25 + floor(random()*12)::INT, '10.0.0.5',     rand_time(base,17,19)),
        (10, 'ROLE_COMMISSAIRE', 'USER_LOGOUT', '/auth/logout', 'POST', 200, 27 + floor(random()*14)::INT, '10.0.0.8',     rand_time(base,17,20)),
        (20, 'ROLE_VOLONTAIRE',  'USER_LOGOUT', '/auth/logout', 'POST', 200, 24 + floor(random()*13)::INT, '10.1.0.1',     rand_time(base,16,18));

    -- ── Erreurs (quelques 401/404 réalistes) ──────────────────
    INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
    VALUES
        (NULL, 'ANONYMOUS', 'PAGE_VIEW', '/competitions/99', 'GET', 404, 45 + floor(random()*20)::INT, '188.99.44.12', rand_time(base,10,15)),
        (NULL, 'ANONYMOUS', 'USER_LOGIN', '/auth/login', 'POST', 401, 38 + floor(random()*18)::INT, '80.11.22.33', rand_time(base,8,12));

END LOOP;
END $$;

-- ============================================================
-- Nettoyage
-- ============================================================
DROP FUNCTION IF EXISTS rand_time(TIMESTAMPTZ, INT, INT);

-- ============================================================
-- Vérification
-- ============================================================
SELECT
    count(*)                                        AS total_events,
    count(DISTINCT date_trunc('day', timestamp))    AS jours_couverts,
    count(DISTINCT user_id)                         AS utilisateurs_distincts,
    min(timestamp)::date                            AS premier_jour,
    max(timestamp)::date                            AS dernier_jour
FROM event_log;

SELECT event_type, count(*) AS nb
FROM event_log
GROUP BY event_type
ORDER BY nb DESC;
