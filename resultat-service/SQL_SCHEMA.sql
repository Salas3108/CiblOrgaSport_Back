-- Schéma de base de données pour le microservice Resultat
-- Exécuter ces requêtes dans PostgreSQL pour initialiser les tables

-- Table des résultats
CREATE TABLE IF NOT EXISTS resultats (
    id BIGSERIAL PRIMARY KEY,
    epreuve_id BIGINT NOT NULL,
    athlete_id BIGINT NOT NULL,
    classement INTEGER NOT NULL,
    temps DECIMAL(5, 2),
    distance DECIMAL(8, 2),
    points DECIMAL(8, 2),
    status VARCHAR(50) NOT NULL DEFAULT 'SAISI',
    saisie_par_id BIGINT NOT NULL,
    observations TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    UNIQUE(epreuve_id, athlete_id),
    INDEX idx_epreuve_id (epreuve_id),
    INDEX idx_athlete_id (athlete_id),
    INDEX idx_status (status),
    INDEX idx_saisie_par_id (saisie_par_id),
    INDEX idx_date_creation (date_creation)
);

-- Table d'historique pour traçabilité
CREATE TABLE IF NOT EXISTS historique_resultats (
    id BIGSERIAL PRIMARY KEY,
    resultat_id BIGINT NOT NULL,
    ancien_status VARCHAR(50) NOT NULL,
    nouveau_status VARCHAR(50) NOT NULL,
    modifie_par BIGINT NOT NULL,
    raison TEXT,
    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resultat_id) REFERENCES resultats(id) ON DELETE CASCADE,
    INDEX idx_resultat_id (resultat_id),
    INDEX idx_modifie_par (modifie_par),
    INDEX idx_date_modification (date_modification)
);

-- Exemples de données d'insertion

-- Exemple 1: Saisir un résultat pour une épreuve
INSERT INTO resultats (
    epreuve_id, athlete_id, classement, temps, distance, points, 
    status, saisie_par_id, observations
) VALUES (
    1, 5, 1, 45.32, 100.50, 100,
    'SAISI', 10, 'Athlète en excellente forme'
);

-- Exemple 2: Historique d'un changement de statut
INSERT INTO historique_resultats (
    resultat_id, ancien_status, nouveau_status, modifie_par, raison
) VALUES (
    1, 'SAISI', 'VALIDE', 1, 'Validation administrative'
);

-- Requêtes utiles

-- Récupérer tous les résultats d'une épreuve (triés par classement)
SELECT * FROM resultats 
WHERE epreuve_id = 1 
ORDER BY classement ASC;

-- Récupérer les résultats en attente de validation
SELECT * FROM resultats 
WHERE status = 'SAISI'
ORDER BY date_creation DESC;

-- Récupérer les résultats saisis par un commissaire
SELECT * FROM resultats 
WHERE saisie_par_id = 10
ORDER BY date_creation DESC;

-- Récupérer l'historique d'un résultat
SELECT * FROM historique_resultats 
WHERE resultat_id = 1
ORDER BY date_modification DESC;

-- Compter les résultats par statut
SELECT status, COUNT(*) as total 
FROM resultats 
GROUP BY status;

-- Résultats d'une épreuve avec leur statut
SELECT 
    r.id,
    r.epreuve_id,
    r.athlete_id,
    r.classement,
    r.temps,
    r.distance,
    r.points,
    r.status,
    r.date_creation,
    COUNT(h.id) as modifications
FROM resultats r
LEFT JOIN historique_resultats h ON r.id = h.resultat_id
WHERE r.epreuve_id = 1
GROUP BY r.id
ORDER BY r.classement ASC;

-- Derniers résultats saisis (les 10 derniers)
SELECT * FROM resultats 
ORDER BY date_creation DESC
LIMIT 10;

-- Résultats modifiés aujourd'hui
SELECT * FROM resultats 
WHERE DATE(date_modification) = CURRENT_DATE
ORDER BY date_modification DESC;

-- Résultats avec nombre de modifications
SELECT 
    r.id,
    r.classement,
    r.status,
    COUNT(h.id) as nb_modifications
FROM resultats r
LEFT JOIN historique_resultats h ON r.id = h.resultat_id
GROUP BY r.id
HAVING COUNT(h.id) > 0
ORDER BY COUNT(h.id) DESC;

-- Détails complets d'un résultat avec historique
SELECT 
    r.id,
    r.epreuve_id,
    r.athlete_id,
    r.classement,
    r.status,
    r.date_creation,
    (
        SELECT json_agg(
            json_build_object(
                'ancien_status', ancien_status,
                'nouveau_status', nouveau_status,
                'modifie_par', modifie_par,
                'date', date_modification
            )
        )
        FROM historique_resultats 
        WHERE resultat_id = r.id
        ORDER BY date_modification DESC
    ) as historique
FROM resultats r
WHERE r.id = 1;
