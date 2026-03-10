-- ============================================================
-- Requêtes SQL pour les dashboards Metabase — CiblOrgaSport
-- Table source : daily_stats, weekly_stats, event_log
-- ============================================================


-- ============================================================
-- 1. Courbe des connexions sur les 30 derniers jours
--    Type de graphique : Ligne (axe X = stat_date, axe Y = total_connections)
-- ============================================================
SELECT
    stat_date          AS "Date",
    total_connections  AS "Connexions totales",
    unique_users       AS "Utilisateurs uniques"
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '30 days'
ORDER BY stat_date ASC;


-- ============================================================
-- 2. Répartition des connexions par rôle sur les 7 derniers jours
--    Type de graphique : Camembert (pie chart)
--    Chaque ligne = un rôle
-- ============================================================
SELECT
    'Athlètes'      AS "Rôle",
    SUM(connections_athletes)    AS "Connexions"
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '7 days'

UNION ALL

SELECT
    'Spectateurs / Users',
    SUM(connections_spectateurs)
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '7 days'

UNION ALL

SELECT
    'Commissaires',
    SUM(connections_commissaires)
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '7 days'

UNION ALL

SELECT
    'Volontaires',
    SUM(connections_volontaires)
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '7 days'

UNION ALL

SELECT
    'Admins',
    SUM(connections_admins)
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '7 days';


-- ============================================================
-- 3. Top 5 compétitions les plus vues (sur les 30 derniers jours)
--    Type de graphique : Barres horizontales
--    Nécessite que metadata contienne {"competitionId": <id>}
-- ============================================================
SELECT
    CAST(metadata->>'competitionId' AS BIGINT)  AS "ID Compétition",
    COUNT(*)                                     AS "Nombre de vues"
FROM event_log
WHERE event_type = 'COMPETITION_VIEW'
  AND timestamp >= NOW() - INTERVAL '30 days'
  AND metadata IS NOT NULL
  AND jsonb_exists(metadata::jsonb, 'competitionId')
GROUP BY 1
ORDER BY 2 DESC
LIMIT 5;


-- ============================================================
-- 4. Évolution des notifications envoyées par type sur 14 jours
--    Type de graphique : Barres empilées
--    (axe X = date, axe Y = nombre, couleur = type)
-- ============================================================
SELECT
    stat_date                  AS "Date",
    notifications_resultats    AS "Résultats",
    notifications_securite     AS "Sécurité",
    notifications_events       AS "Événements",
    total_notifications_sent   AS "Total"
FROM daily_stats
WHERE stat_date >= CURRENT_DATE - INTERVAL '14 days'
ORDER BY stat_date ASC;


-- ============================================================
-- 5. Tableau récapitulatif hebdomadaire (toutes les semaines)
--    Type de graphique : Tableau (table)
-- ============================================================
SELECT
    TO_CHAR(week_start, 'DD/MM/YYYY')  AS "Semaine du",
    TO_CHAR(week_end,   'DD/MM/YYYY')  AS "Au",
    total_connections                   AS "Connexions totales",
    unique_users                        AS "Utilisateurs uniques",
    ROUND(avg_daily_connections::NUMERIC, 1) AS "Moy. / jour",
    TO_CHAR(peak_day, 'DD/MM/YYYY')     AS "Jour de pic",
    peak_connections                    AS "Pic connexions",
    total_notifications_sent            AS "Notifications envoyées",
    total_new_subscriptions             AS "Nouveaux abonnés",
    CONCAT(ROUND(growth_rate_percent::NUMERIC, 1), ' %') AS "Évolution vs semaine préc."
FROM weekly_stats
ORDER BY week_start DESC;
