-- ══════════════════════════════════════════════════════════════
-- data-demo.sql — Données de démonstration CiblOrgaSport
-- Base : glop (PostgreSQL)
-- Événement : Championnats d'Europe de Natation 2026 — Paris
-- ══════════════════════════════════════════════════════════════

-- ══════════════════════════════
-- 1. LIEUX
-- ══════════════════════════════
INSERT INTO lieu (nom, adresse, ville, code_postal, pays, capacite_spectateurs) VALUES
('Centre Aquatique Olympique de la Métropole du Grand Paris', '1 Rue des Championnats', 'Saint-Denis', '93200', 'France', 15000),
('La Seine — Bras de Grenelle', 'Pont de Grenelle', 'Paris', '75015', 'France', 30000);
-- lieu.id : 1 = CAOMGP, 2 = La Seine

-- ══════════════════════════════
-- 2. USERS & AUTH
-- ══════════════════════════════
-- Hash BCrypt de "password123"
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.

-- 1 ADMIN (id=1)
INSERT INTO users (id, username, email, password, role, created_at, updated_at, validated) VALUES
(1, 'marius.admin', 'marius.admin@ciblorgasport.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ADMIN', '2026-01-10 08:00:00', '2026-01-10 08:00:00', true);

-- 40 ATHLETES (ids 2-41)
INSERT INTO users (id, username, email, password, role, created_at, updated_at, validated) VALUES
-- France (ids 2-6)
(2,  'leon.marc',      'leon.marc@equipe-france.fr',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-01 09:00:00', '2026-02-01 09:00:00', true),
(3,  'marie.claire',   'marie.claire@equipe-france.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-01 09:05:00', '2026-02-01 09:05:00', true),
(4,  'florent.manaud', 'florent.manaud@equipe-france.fr','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-01 09:10:00', '2026-02-01 09:10:00', true),
(5,  'pauline.duval',  'pauline.duval@equipe-france.fr', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-01 09:15:00', '2026-02-01 09:15:00', true),
(6,  'hugo.bernard',   'hugo.bernard@equipe-france.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-01 09:20:00', '2026-02-01 09:20:00', true),
-- Italie (ids 7-11)
(7,  'fede.pellegrini',   'fede.pellegrini@teamitalia.it',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-02 09:00:00', '2026-02-02 09:00:00', true),
(8,  'marco.orsi',        'marco.orsi@teamitalia.it',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-02 09:05:00', '2026-02-02 09:05:00', true),
(9,  'simona.quadarella', 'simona.quad@teamitalia.it',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-02 09:10:00', '2026-02-02 09:10:00', true),
(10, 'luca.dotto',        'luca.dotto@teamitalia.it',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-02 09:15:00', '2026-02-02 09:15:00', true),
(11, 'ilaria.bianchi',    'ilaria.bianchi@teamitalia.it',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-02 09:20:00', '2026-02-02 09:20:00', true),
-- Hongrie (ids 12-16)
(12, 'kristof.milak',  'kristof.milak@teamhungary.hu',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-03 09:00:00', '2026-02-03 09:00:00', true),
(13, 'katinka.hosszu', 'katinka.hosszu@teamhungary.hu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-03 09:05:00', '2026-02-03 09:05:00', true),
(14, 'dominik.kozma',  'dominik.kozma@teamhungary.hu',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-03 09:10:00', '2026-02-03 09:10:00', true),
(15, 'boglarka.kapas', 'boglarka.kapas@teamhungary.hu',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-03 09:15:00', '2026-02-03 09:15:00', true),
(16, 'david.nemeth',   'david.nemeth@teamhungary.hu',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-03 09:20:00', '2026-02-03 09:20:00', true),
-- Allemagne (ids 17-21)
(17, 'florian.wellbrock', 'florian.wellbrock@teamgermany.de', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-04 09:00:00', '2026-02-04 09:00:00', true),
(18, 'sarah.kohler',      'sarah.kohler@teamgermany.de',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-04 09:05:00', '2026-02-04 09:05:00', true),
(19, 'marco.fischer',     'marco.fischer@teamgermany.de',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-04 09:10:00', '2026-02-04 09:10:00', true),
(20, 'anna.egorova',      'anna.egorova@teamgermany.de',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-04 09:15:00', '2026-02-04 09:15:00', true),
(21, 'lukas.martens',     'lukas.martens@teamgermany.de',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-04 09:20:00', '2026-02-04 09:20:00', true),
-- Pays-Bas (ids 22-26)
(22, 'kyle.chalmers.nl',  'kyle.vanderburg@teamnl.nl',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-05 09:00:00', '2026-02-05 09:00:00', true),
(23, 'ranomi.krom',       'ranomi.krom@teamnl.nl',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-05 09:05:00', '2026-02-05 09:05:00', true),
(24, 'ferry.weertman',    'ferry.weertman@teamnl.nl',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-05 09:10:00', '2026-02-05 09:10:00', true),
(25, 'arno.kamminga',     'arno.kamminga@teamnl.nl',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-05 09:15:00', '2026-02-05 09:15:00', true),
(26, 'shayna.jack.nl',    'shayna.vanderberg@teamnl.nl', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-05 09:20:00', '2026-02-05 09:20:00', true),
-- Espagne (ids 27-31)
(27, 'miguel.duran',     'miguel.duran@teamspain.es',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-06 09:00:00', '2026-02-06 09:00:00', true),
(28, 'jessica.vall',     'jessica.vall@teamspain.es',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-06 09:05:00', '2026-02-06 09:05:00', true),
(29, 'david.castro',     'david.castro@teamspain.es',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-06 09:10:00', '2026-02-06 09:10:00', true),
(30, 'alba.vazquez',     'alba.vazquez@teamspain.es',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-06 09:15:00', '2026-02-06 09:15:00', true),
(31, 'fernando.olmedo',  'fernando.olmedo@teamspain.es', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-06 09:20:00', '2026-02-06 09:20:00', true),
-- Grande-Bretagne (ids 32-36)
(32, 'adam.peaty',       'adam.peaty@teamgb.co.uk',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-07 09:00:00', '2026-02-07 09:00:00', true),
(33, 'anna.ekins',       'anna.ekins@teamgb.co.uk',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-07 09:05:00', '2026-02-07 09:05:00', true),
(34, 'james.wilby',      'james.wilby@teamgb.co.uk',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-07 09:10:00', '2026-02-07 09:10:00', true),
(35, 'freya.anderson',   'freya.anderson@teamgb.co.uk',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-07 09:15:00', '2026-02-07 09:15:00', true),
(36, 'tom.dean',         'tom.dean@teamgb.co.uk',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-07 09:20:00', '2026-02-07 09:20:00', true),
-- Suède (ids 37-41)
(37, 'sarah.sjostrom',   'sarah.sjostrom@teamsweden.se', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-08 09:00:00', '2026-02-08 09:00:00', true),
(38, 'louise.hansson',   'louise.hansson@teamsweden.se', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-08 09:05:00', '2026-02-08 09:05:00', true),
(39, 'bjorn.larsson',    'bjorn.larsson@teamsweden.se',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-08 09:10:00', '2026-02-08 09:10:00', true),
(40, 'erik.persson',     'erik.persson@teamsweden.se',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-08 09:15:00', '2026-02-08 09:15:00', true),
(41, 'linn.sjoberg',     'linn.sjoberg@teamsweden.se',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'ATHLETE', '2026-02-08 09:20:00', '2026-02-08 09:20:00', true);

-- 5 COMMISSAIRES (ids 42-46, 1 par discipline)
INSERT INTO users (id, username, email, password, role, created_at, updated_at, validated) VALUES
(42, 'comm.natation',          'comm.natation@ciblorgasport.fr',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'COMMISSAIRE', '2026-01-15 08:00:00', '2026-01-15 08:00:00', true),
(43, 'comm.waterpolo',         'comm.waterpolo@ciblorgasport.fr',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'COMMISSAIRE', '2026-01-15 08:05:00', '2026-01-15 08:05:00', true),
(44, 'comm.natation.artis',    'comm.natartis@ciblorgasport.fr',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'COMMISSAIRE', '2026-01-15 08:10:00', '2026-01-15 08:10:00', true),
(45, 'comm.plongeon',          'comm.plongeon@ciblorgasport.fr',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'COMMISSAIRE', '2026-01-15 08:15:00', '2026-01-15 08:15:00', true),
(46, 'comm.eau.libre',         'comm.eaulibre@ciblorgasport.fr',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'COMMISSAIRE', '2026-01-15 08:20:00', '2026-01-15 08:20:00', true);

-- 12 VOLONTAIRES (ids 47-58) — étape 1 : validated=false
INSERT INTO users (id, username, email, password, role, created_at, updated_at, validated) VALUES
(47, 'vol.sophie.martin',   'sophie.martin@volunteer.fr',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-01 10:00:00', '2026-03-01 10:00:00', false),
(48, 'vol.thomas.leclerc',  'thomas.leclerc@volunteer.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-01 10:05:00', '2026-03-01 10:05:00', false),
(49, 'vol.amelia.lambert',  'amelia.lambert@volunteer.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-02 10:00:00', '2026-03-02 10:00:00', false),
(50, 'vol.nicolas.girard',  'nicolas.girard@volunteer.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-02 10:05:00', '2026-03-02 10:05:00', false),
(51, 'vol.camille.robert',  'camille.robert@volunteer.fr',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-03 10:00:00', '2026-03-03 10:00:00', false),
(52, 'vol.julien.petit',    'julien.petit@volunteer.fr',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-03 10:05:00', '2026-03-03 10:05:00', false),
(53, 'vol.lea.moreau',      'lea.moreau@volunteer.fr',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-04 10:00:00', '2026-03-04 10:00:00', false),
(54, 'vol.marc.simon',      'marc.simon@volunteer.fr',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-04 10:05:00', '2026-03-04 10:05:00', false),
(55, 'vol.elena.garcia',    'elena.garcia@volunteer.fr',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-05 10:00:00', '2026-03-05 10:00:00', false),
(56, 'vol.pierre.henry',    'pierre.henry@volunteer.fr',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-05 10:05:00', '2026-03-05 10:05:00', false),
(57, 'vol.sarah.david',     'sarah.david@volunteer.fr',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-06 10:00:00', '2026-03-06 10:00:00', false),
(58, 'vol.antoine.blanc',   'antoine.blanc@volunteer.fr',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'VOLONTAIRE', '2026-03-06 10:05:00', '2026-03-06 10:05:00', false);

-- Étape 2 : Validation des volontaires par l'admin Marius
UPDATE users SET validated = true, updated_at = '2026-03-15 09:00:00' WHERE id BETWEEN 47 AND 58;

-- 22 SPECTATEURS (ids 59-80, role=USER)
INSERT INTO users (id, username, email, password, role, created_at, updated_at, validated) VALUES
(59, 'alice.dupont',     'alice.dupont@gmail.com',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-01 10:00:00', '2026-04-01 10:00:00', true),
(60, 'bob.martin',       'bob.martin@gmail.com',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-01 10:05:00', '2026-04-01 10:05:00', true),
(61, 'claire.leroy',     'claire.leroy@outlook.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-02 10:00:00', '2026-04-02 10:00:00', true),
(62, 'david.thomas',     'david.thomas@yahoo.fr',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-02 10:05:00', '2026-04-02 10:05:00', true),
(63, 'emma.garcia',      'emma.garcia@gmail.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-03 10:00:00', '2026-04-03 10:00:00', true),
(64, 'franck.roux',      'franck.roux@gmail.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-03 10:05:00', '2026-04-03 10:05:00', true),
(65, 'grace.lopez',      'grace.lopez@hotmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-04 10:00:00', '2026-04-04 10:00:00', true),
(66, 'henri.bertrand',   'henri.bertrand@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-04 10:05:00', '2026-04-04 10:05:00', true),
(67, 'isabelle.simon',   'isabelle.simon@orange.fr',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-05 10:00:00', '2026-04-05 10:00:00', true),
(68, 'jean.moulin',      'jean.moulin@gmail.com',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-05 10:05:00', '2026-04-05 10:05:00', true),
(69, 'karine.michel',    'karine.michel@sfr.fr',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-06 10:00:00', '2026-04-06 10:00:00', true),
(70, 'laurent.durand',   'laurent.durand@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-06 10:05:00', '2026-04-06 10:05:00', true),
(71, 'marie.chevalier',  'marie.chevalier@gmail.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-07 10:00:00', '2026-04-07 10:00:00', true),
(72, 'nicolas.faure',    'nicolas.faure@free.fr',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-07 10:05:00', '2026-04-07 10:05:00', true),
(73, 'olivia.bonnet',    'olivia.bonnet@gmail.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-08 10:00:00', '2026-04-08 10:00:00', true),
(74, 'pascal.giraud',    'pascal.giraud@yahoo.fr',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-08 10:05:00', '2026-04-08 10:05:00', true),
(75, 'quentin.lebrun',   'quentin.lebrun@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-09 10:00:00', '2026-04-09 10:00:00', true),
(76, 'rachel.vidal',     'rachel.vidal@hotmail.fr',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-09 10:05:00', '2026-04-09 10:05:00', true),
(77, 'samuel.roy',       'samuel.roy@gmail.com',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-10 10:00:00', '2026-04-10 10:00:00', true),
(78, 'therese.fernandez','therese.fernandez@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-10 10:05:00', '2026-04-10 10:05:00', true),
(79, 'ugo.lambert',      'ugo.lambert@orange.fr',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-11 10:00:00', '2026-04-11 10:00:00', true),
(80, 'valerie.perrin',   'valerie.perrin@gmail.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhu.', 'USER', '2026-04-11 10:05:00', '2026-04-11 10:05:00', true);

-- Réinitialisation de la séquence users après insertions manuelles
SELECT setval('users_id_seq', 80);

-- ══════════════════════════════
-- 3. ÉQUIPES NATIONALES
-- ══════════════════════════════
INSERT INTO equipes (nom, pays) VALUES
('Équipe de France', 'France'),          -- id=1
('Squadra Azzurra', 'Italie'),           -- id=2
('Magyar Válogatott', 'Hongrie'),        -- id=3
('Deutsche Nationalmannschaft', 'Allemagne'), -- id=4
('Oranje', 'Pays-Bas'),                 -- id=5
('Selección Española', 'Espagne'),       -- id=6
('Team Great Britain', 'Grande-Bretagne'), -- id=7
('Blågult', 'Suède');                   -- id=8

-- ══════════════════════════════
-- 4. ATHLÈTES
-- ══════════════════════════════
-- athletes.id = users.id (PK assignée manuellement)
INSERT INTO athletes (id, username, nom, prenom, date_naissance, pays, valide, sexe, equipe_id) VALUES
-- France (equipe_id=1)
(2,  'leon.marc',      'Marc',      'Léon',    '1997-04-22', 'France',          true, 'MASCULIN', 1),
(3,  'marie.claire',   'Claire',    'Marie',   '2000-06-15', 'France',          true, 'FEMININ',  1),
(4,  'florent.manaud', 'Manaud',    'Florent', '1995-09-08', 'France',          true, 'MASCULIN', 1),
(5,  'pauline.duval',  'Duval',     'Pauline', '2002-03-19', 'France',          true, 'FEMININ',  1),
(6,  'hugo.bernard',   'Bernard',   'Hugo',    '1999-11-30', 'France',          true, 'MASCULIN', 1),
-- Italie (equipe_id=2)
(7,  'fede.pellegrini',   'Pellegrini', 'Federica', '1988-08-05', 'Italie',    true, 'FEMININ',  2),
(8,  'marco.orsi',        'Orsi',       'Marco',    '1995-12-20', 'Italie',    true, 'MASCULIN', 2),
(9,  'simona.quadarella', 'Quadarella', 'Simona',   '1998-07-18', 'Italie',    true, 'FEMININ',  2),
(10, 'luca.dotto',        'Dotto',      'Luca',     '1997-03-12', 'Italie',    true, 'MASCULIN', 2),
(11, 'ilaria.bianchi',    'Bianchi',    'Ilaria',   '1990-01-25', 'Italie',    true, 'FEMININ',  2),
-- Hongrie (equipe_id=3)
(12, 'kristof.milak',  'Milak',    'Kristóf',  '2000-02-20', 'Hongrie',        true, 'MASCULIN', 3),
(13, 'katinka.hosszu', 'Hosszú',   'Katinka',  '1989-05-03', 'Hongrie',        true, 'FEMININ',  3),
(14, 'dominik.kozma',  'Kozma',    'Dominik',  '1991-09-28', 'Hongrie',        true, 'MASCULIN', 3),
(15, 'boglarka.kapas', 'Kapas',    'Boglárka', '1993-04-12', 'Hongrie',        true, 'FEMININ',  3),
(16, 'david.nemeth',   'Németh',   'Dávid',    '2001-07-07', 'Hongrie',        true, 'MASCULIN', 3),
-- Allemagne (equipe_id=4)
(17, 'florian.wellbrock', 'Wellbrock', 'Florian', '1997-08-19', 'Allemagne',   true, 'MASCULIN', 4),
(18, 'sarah.kohler',      'Köhler',    'Sarah',   '1995-03-27', 'Allemagne',   true, 'FEMININ',  4),
(19, 'marco.fischer',     'Fischer',   'Marco',   '1999-11-14', 'Allemagne',   true, 'MASCULIN', 4),
(20, 'anna.egorova',      'Egorova',   'Anna',    '2000-06-01', 'Allemagne',   true, 'FEMININ',  4),
(21, 'lukas.martens',     'Martens',   'Lukas',   '2002-01-17', 'Allemagne',   true, 'MASCULIN', 4),
-- Pays-Bas (equipe_id=5)
(22, 'kyle.chalmers.nl',  'Van der Burg', 'Kyle',   '1998-05-25', 'Pays-Bas',  true, 'MASCULIN', 5),
(23, 'ranomi.krom',       'Kromowidjojo','Ranomi',  '1990-08-20', 'Pays-Bas',  true, 'FEMININ',  5),
(24, 'ferry.weertman',    'Weertman',    'Ferry',   '1992-04-07', 'Pays-Bas',  true, 'MASCULIN', 5),
(25, 'arno.kamminga',     'Kamminga',    'Arno',    '1995-11-22', 'Pays-Bas',  true, 'MASCULIN', 5),
(26, 'shayna.jack.nl',    'Van den Berg','Shayna',  '2000-03-10', 'Pays-Bas',  true, 'FEMININ',  5),
-- Espagne (equipe_id=6)
(27, 'miguel.duran',     'Durán',     'Miguel',   '1996-07-14', 'Espagne',     true, 'MASCULIN', 6),
(28, 'jessica.vall',     'Vall',      'Jessica',  '1988-11-22', 'Espagne',     true, 'FEMININ',  6),
(29, 'david.castro',     'Castro',    'David',    '1994-02-09', 'Espagne',     true, 'MASCULIN', 6),
(30, 'alba.vazquez',     'Vázquez',   'Alba',     '2001-08-30', 'Espagne',     true, 'FEMININ',  6),
(31, 'fernando.olmedo',  'Olmedo',    'Fernando', '1999-04-18', 'Espagne',     true, 'MASCULIN', 6),
-- Grande-Bretagne (equipe_id=7)
(32, 'adam.peaty',       'Peaty',     'Adam',     '1994-12-28', 'Grande-Bretagne', true, 'MASCULIN', 7),
(33, 'anna.ekins',       'Ekins',     'Anna',     '1997-10-15', 'Grande-Bretagne', true, 'FEMININ',  7),
(34, 'james.wilby',      'Wilby',     'James',    '1993-03-04', 'Grande-Bretagne', true, 'MASCULIN', 7),
(35, 'freya.anderson',   'Anderson',  'Freya',    '2001-06-27', 'Grande-Bretagne', true, 'FEMININ',  7),
(36, 'tom.dean',         'Dean',      'Tom',      '2000-05-02', 'Grande-Bretagne', true, 'MASCULIN', 7),
-- Suède (equipe_id=8)
(37, 'sarah.sjostrom',   'Sjöström',  'Sarah',    '1993-08-17', 'Suède',       true, 'FEMININ',  8),
(38, 'louise.hansson',   'Hansson',   'Louise',   '1996-11-12', 'Suède',       true, 'FEMININ',  8),
(39, 'bjorn.larsson',    'Larsson',   'Björn',    '1994-04-08', 'Suède',       true, 'MASCULIN', 8),
(40, 'erik.persson',     'Persson',   'Erik',     '2000-09-21', 'Suède',       true, 'MASCULIN', 8),
(41, 'linn.sjoberg',     'Sjöberg',   'Linn',     '2003-02-14', 'Suède',       true, 'FEMININ',  8);

-- ══════════════════════════════
-- 5. ÉVÉNEMENT
-- ══════════════════════════════
INSERT INTO event (name, date_debut, date_fin, description, pays_hote) VALUES
('Championnats d''Europe de Natation 2026',
 '2026-07-31', '2026-08-16',
 'Championnats d''Europe de Natation 2026 organisés à Paris et Saint-Denis',
 'France');
-- event.id = 1

-- ══════════════════════════════
-- 6. COMPÉTITIONS
-- ══════════════════════════════
INSERT INTO competition (event_id, discipline) VALUES
(1, 'NATATION_ARTISTIQUE'), -- id=1
(1, 'PLONGEON'),            -- id=2
(1, 'EAU_LIBRE'),           -- id=3
(1, 'WATER_POLO'),          -- id=4
(1, 'NATATION');            -- id=5

-- ══════════════════════════════
-- 7. ÉPREUVES
-- ══════════════════════════════
-- NATATION (competition_id=5, lieu_id=1)
INSERT INTO epreuve (nom, description, date_heure, duree_minutes, competition_id, lieu_id, type_epreuve, genre_epreuve, niveau_epreuve, statut) VALUES

-- 100m Nage Libre Hommes
('100m Nage Libre Hommes - Qualification',
 'Séries qualificatives 100m Nage Libre Hommes',
 '2026-08-10 09:00:00', 60, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'QUALIFICATION', 'TERMINE'),    -- id=1
('100m Nage Libre Hommes - Finale',
 'Finale 100m Nage Libre Hommes',
 '2026-08-11 18:00:00', 30, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'TERMINE'),            -- id=2

-- 100m Nage Libre Femmes
('100m Nage Libre Femmes - Qualification',
 'Séries qualificatives 100m Nage Libre Femmes',
 '2026-08-10 10:00:00', 60, 5, 1, 'INDIVIDUELLE', 'FEMININ', 'QUALIFICATION', 'TERMINE'),      -- id=3
('100m Nage Libre Femmes - Finale',
 'Finale 100m Nage Libre Femmes',
 '2026-08-11 19:00:00', 30, 5, 1, 'INDIVIDUELLE', 'FEMININ', 'FINALE', 'TERMINE'),             -- id=4

-- 200m Papillon Hommes
('200m Papillon Hommes - Qualification',
 'Séries qualificatives 200m Papillon Hommes',
 '2026-08-12 09:00:00', 60, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'QUALIFICATION', 'TERMINE'),    -- id=5
('200m Papillon Hommes - Finale',
 'Finale 200m Papillon Hommes',
 '2026-08-13 18:00:00', 30, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'PLANIFIE'),           -- id=6

-- 100m Brasse Femmes
('100m Brasse Femmes - Qualification',
 'Séries qualificatives 100m Brasse Femmes',
 '2026-08-12 10:00:00', 60, 5, 1, 'INDIVIDUELLE', 'FEMININ', 'QUALIFICATION', 'TERMINE'),      -- id=7
('100m Brasse Femmes - Finale',
 'Finale 100m Brasse Femmes',
 '2026-08-13 19:00:00', 30, 5, 1, 'INDIVIDUELLE', 'FEMININ', 'FINALE', 'PLANIFIE'),            -- id=8

-- 400m 4 Nages Hommes
('400m 4 Nages Hommes - Qualification',
 'Séries qualificatives 400m 4 Nages Hommes',
 '2026-08-14 09:00:00', 90, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'QUALIFICATION', 'PLANIFIE'),   -- id=9
('400m 4 Nages Hommes - Finale',
 'Finale 400m 4 Nages Hommes',
 '2026-08-15 18:00:00', 45, 5, 1, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'PLANIFIE'),           -- id=10

-- WATER POLO (competition_id=4, lieu_id=1)
('Water-Polo Hommes - Phase de groupes',
 'Phase de groupes Water-Polo Hommes',
 '2026-07-31 10:00:00', 90, 4, 1, 'COLLECTIVE', 'MASCULIN', 'QUALIFICATION', 'TERMINE'),      -- id=11
('Water-Polo Femmes - Phase de groupes',
 'Phase de groupes Water-Polo Femmes',
 '2026-07-31 14:00:00', 90, 4, 1, 'COLLECTIVE', 'FEMININ', 'QUALIFICATION', 'TERMINE'),       -- id=12
('Water-Polo Hommes - Quarts de finale',
 'Quarts de finale Water-Polo Hommes',
 '2026-08-04 16:00:00', 90, 4, 1, 'COLLECTIVE', 'MASCULIN', 'QUART_DE_FINALE', 'TERMINE'),    -- id=13
('Water-Polo Femmes - Quarts de finale',
 'Quarts de finale Water-Polo Femmes',
 '2026-08-04 18:00:00', 90, 4, 1, 'COLLECTIVE', 'FEMININ', 'QUART_DE_FINALE', 'TERMINE'),     -- id=14
('Water-Polo Hommes - Demi-finales',
 'Demi-finales Water-Polo Hommes',
 '2026-08-06 16:00:00', 90, 4, 1, 'COLLECTIVE', 'MASCULIN', 'DEMI_FINALE', 'EN_COURS'),       -- id=15
('Water-Polo Femmes - Demi-finales',
 'Demi-finales Water-Polo Femmes',
 '2026-08-06 18:00:00', 90, 4, 1, 'COLLECTIVE', 'FEMININ', 'DEMI_FINALE', 'PLANIFIE'),        -- id=16
('Water-Polo Hommes - Finale',
 'Finale Water-Polo Hommes',
 '2026-08-08 16:00:00', 90, 4, 1, 'COLLECTIVE', 'MASCULIN', 'FINALE', 'PLANIFIE'),            -- id=17
('Water-Polo Femmes - Finale',
 'Finale Water-Polo Femmes',
 '2026-08-08 18:00:00', 90, 4, 1, 'COLLECTIVE', 'FEMININ', 'FINALE', 'PLANIFIE'),             -- id=18

-- NATATION ARTISTIQUE (competition_id=1, lieu_id=1)
('Solo Femmes - Qualification',
 'Qualification Solo Natation Artistique Femmes',
 '2026-07-31 10:00:00', 60, 1, 1, 'INDIVIDUELLE', 'FEMININ', 'QUALIFICATION', 'TERMINE'),     -- id=19
('Solo Femmes - Finale',
 'Finale Solo Natation Artistique Femmes',
 '2026-08-01 18:00:00', 60, 1, 1, 'INDIVIDUELLE', 'FEMININ', 'FINALE', 'TERMINE'),            -- id=20
('Solo Hommes - Qualification',
 'Qualification Solo Natation Artistique Hommes',
 '2026-08-02 10:00:00', 60, 1, 1, 'INDIVIDUELLE', 'MASCULIN', 'QUALIFICATION', 'REPORTE'),    -- id=21
('Solo Hommes - Finale',
 'Finale Solo Natation Artistique Hommes',
 '2026-08-03 18:00:00', 60, 1, 1, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'ANNULE'),            -- id=22

-- PLONGEON (competition_id=2, lieu_id=1)
('Tremplin 3m Hommes - Qualification',
 'Qualification Tremplin 3m Hommes',
 '2026-07-31 14:00:00', 90, 2, 1, 'INDIVIDUELLE', 'MASCULIN', 'QUALIFICATION', 'TERMINE'),    -- id=23
('Tremplin 3m Hommes - Finale',
 'Finale Tremplin 3m Hommes',
 '2026-08-02 18:00:00', 60, 2, 1, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'EN_COURS'),          -- id=24
('Plateforme 10m Femmes - Qualification',
 'Qualification Plateforme 10m Femmes',
 '2026-08-01 14:00:00', 90, 2, 1, 'INDIVIDUELLE', 'FEMININ', 'QUALIFICATION', 'REPORTE'),     -- id=25
('Plateforme 10m Femmes - Finale',
 'Finale Plateforme 10m Femmes',
 '2026-08-03 18:00:00', 60, 2, 1, 'INDIVIDUELLE', 'FEMININ', 'FINALE', 'PLANIFIE'),           -- id=26

-- EAU LIBRE (competition_id=3, lieu_id=2 — La Seine)
('10km Eau Libre Hommes - Finale',
 'Finale 10km Eau Libre Hommes — La Seine',
 '2026-08-05 08:00:00', 130, 3, 2, 'INDIVIDUELLE', 'MASCULIN', 'FINALE', 'TERMINE'),          -- id=27
('10km Eau Libre Femmes - Finale',
 'Finale 10km Eau Libre Femmes — La Seine',
 '2026-08-06 08:00:00', 130, 3, 2, 'INDIVIDUELLE', 'FEMININ', 'FINALE', 'TERMINE');           -- id=28

-- ══════════════════════════════
-- 8. INSCRIPTIONS ÉPREUVES
-- ══════════════════════════════

-- epreuve_athletes : athlètes individuels inscrits aux épreuves
-- 100m NL Hommes QUALIFICATION (epreuve_id=1) : H ids 2,4,6,10,12,14,17,19,22,24,25,27,29,31,32,34,36,39,40
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(1,2),(1,4),(1,6),(1,8),(1,10),(1,12),(1,14),(1,17),(1,19),(1,22),(1,24),(1,25),(1,27),(1,29),(1,32),(1,34),(1,36),(1,39),(1,40);
-- 100m NL Hommes FINALE (epreuve_id=2) : top 8 qualifiés
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(2,12),(2,2),(2,17),(2,32),(2,10),(2,22),(2,39),(2,36);

-- 100m NL Femmes QUALIFICATION (epreuve_id=3)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(3,3),(3,5),(3,7),(3,9),(3,11),(3,13),(3,15),(3,18),(3,20),(3,23),(3,26),(3,28),(3,30),(3,33),(3,35),(3,37),(3,38),(3,41);
-- 100m NL Femmes FINALE (epreuve_id=4) : top 8
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(4,37),(4,7),(4,13),(4,3),(4,23),(4,33),(4,9),(4,18);

-- 200m Papillon Hommes QUALIFICATION (epreuve_id=5)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(5,12),(5,2),(5,6),(5,14),(5,16),(5,17),(5,19),(5,21),(5,25),(5,27),(5,29),(5,31),(5,34),(5,36),(5,39),(5,40);
-- 200m Papillon Hommes FINALE (epreuve_id=6) : top 8 planifié
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(6,12),(6,17),(6,2),(6,14),(6,36),(6,39),(6,25),(6,31);

-- 100m Brasse Femmes QUALIFICATION (epreuve_id=7)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(7,13),(7,3),(7,5),(7,9),(7,11),(7,15),(7,18),(7,20),(7,23),(7,26),(7,28),(7,30),(7,33),(7,35),(7,37),(7,38),(7,41);
-- 100m Brasse Femmes FINALE (epreuve_id=8) : top 8 planifié
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(8,13),(8,3),(8,28),(8,15),(8,37),(8,9),(8,33),(8,18);

-- 400m 4 Nages Hommes QUALIFICATION (epreuve_id=9) planifié
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(9,12),(9,13),(9,14),(9,17),(9,19),(9,22),(9,24),(9,25),(9,27),(9,29),(9,32),(9,34),(9,36),(9,39),(9,40);
-- 400m 4 Nages Hommes FINALE (epreuve_id=10) planifié
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(10,12),(10,17),(10,22),(10,36),(10,14),(10,24),(10,39),(10,27);

-- Natation Artistique Solo Femmes QUALIFICATION (epreuve_id=19)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(19,3),(19,5),(19,7),(19,9),(19,13),(19,15),(19,18),(19,20),(19,26),(19,30),(19,33),(19,35),(19,37),(19,38),(19,41);
-- Natation Artistique Solo Femmes FINALE (epreuve_id=20)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(20,37),(20,13),(20,7),(20,3),(20,9),(20,33),(20,15),(20,38);

-- Plongeon Tremplin 3m Hommes QUALIFICATION (epreuve_id=23)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(23,2),(23,4),(23,6),(23,8),(23,10),(23,12),(23,14),(23,16),(23,17),(23,19),(23,21);
-- Plongeon Tremplin 3m Hommes FINALE EN_COURS (epreuve_id=24)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(24,2),(24,12),(24,4),(24,17),(24,6),(24,14),(24,10),(24,21);

-- Eau Libre 10km Hommes FINALE (epreuve_id=27)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(27,17),(27,24),(27,14),(27,4),(27,39),(27,27),(27,32),(27,22);
-- Eau Libre 10km Femmes FINALE (epreuve_id=28)
INSERT INTO epreuve_athletes (epreuve_id, athlete_id) VALUES
(28,37),(28,13),(28,3),(28,9),(28,18),(28,38),(28,26),(28,33);

-- epreuve_equipes : water polo COLLECTIVE (équipes 1-8 inscrites aux phases)
-- Phase groupes H (epreuve_id=11) + QdF H (epreuve_id=13) + Demi H (epreuve_id=15) + Finale H (epreuve_id=17)
INSERT INTO epreuve_equipes (epreuve_id, equipe_id) VALUES
(11,1),(11,2),(11,3),(11,4),(11,5),(11,6),(11,7),(11,8),
(12,1),(12,2),(12,3),(12,4),(12,5),(12,6),(12,7),(12,8),
(13,1),(13,3),(13,5),(13,7),
(14,2),(14,4),(14,6),(14,8),
(15,1),(15,3),
(16,2),(16,6),
(17,1),(17,3),
(18,2),(18,6);

-- epreuve_athlete_assignments
-- TERMINE QUALIFICATION (100m NL H, 100m NL F, 200m Pap H, 100m Br F, Water Polo phases/QdF, Nat.Art., Eau Libre)
INSERT INTO epreuve_athlete_assignments (epreuve_id, athlete_id, statut_participation) VALUES
-- 100m NL Hommes Qual (id=1)
(1,2,'TERMINE'),(1,4,'TERMINE'),(1,6,'TERMINE'),(1,8,'FORFAIT'),(1,10,'TERMINE'),
(1,12,'TERMINE'),(1,14,'TERMINE'),(1,17,'TERMINE'),(1,19,'TERMINE'),(1,22,'TERMINE'),
(1,24,'TERMINE'),(1,25,'TERMINE'),(1,27,'TERMINE'),(1,29,'TERMINE'),(1,32,'TERMINE'),
(1,34,'TERMINE'),(1,36,'TERMINE'),(1,39,'TERMINE'),(1,40,'TERMINE'),
-- 100m NL Hommes Finale (id=2)
(2,12,'TERMINE'),(2,2,'TERMINE'),(2,17,'TERMINE'),(2,32,'TERMINE'),
(2,10,'TERMINE'),(2,22,'TERMINE'),(2,39,'TERMINE'),(2,36,'TERMINE'),
-- 100m NL Femmes Qual (id=3)
(3,3,'TERMINE'),(3,5,'FORFAIT'),(3,7,'TERMINE'),(3,9,'TERMINE'),(3,11,'TERMINE'),
(3,13,'TERMINE'),(3,15,'TERMINE'),(3,18,'TERMINE'),(3,20,'TERMINE'),(3,23,'TERMINE'),
(3,26,'TERMINE'),(3,28,'TERMINE'),(3,30,'TERMINE'),(3,33,'TERMINE'),(3,35,'TERMINE'),
(3,37,'TERMINE'),(3,38,'TERMINE'),(3,41,'TERMINE'),
-- 100m NL Femmes Finale (id=4)
(4,37,'TERMINE'),(4,7,'TERMINE'),(4,13,'TERMINE'),(4,3,'TERMINE'),
(4,23,'TERMINE'),(4,33,'TERMINE'),(4,9,'TERMINE'),(4,18,'TERMINE'),
-- 200m Papillon Hommes Qual (id=5)
(5,12,'TERMINE'),(5,2,'TERMINE'),(5,6,'TERMINE'),(5,14,'TERMINE'),(5,16,'TERMINE'),
(5,17,'TERMINE'),(5,19,'TERMINE'),(5,21,'TERMINE'),(5,25,'TERMINE'),(5,27,'TERMINE'),
(5,29,'TERMINE'),(5,31,'TERMINE'),(5,34,'TERMINE'),(5,36,'TERMINE'),(5,39,'TERMINE'),(5,40,'TERMINE'),
-- 200m Papillon Hommes Finale (id=6) PLANIFIE
(6,12,'INSCRIT'),(6,17,'INSCRIT'),(6,2,'INSCRIT'),(6,14,'INSCRIT'),
(6,36,'INSCRIT'),(6,39,'INSCRIT'),(6,25,'INSCRIT'),(6,31,'INSCRIT'),
-- 100m Brasse Femmes Qual (id=7)
(7,13,'TERMINE'),(7,3,'TERMINE'),(7,5,'TERMINE'),(7,9,'TERMINE'),(7,11,'TERMINE'),
(7,15,'TERMINE'),(7,18,'TERMINE'),(7,20,'TERMINE'),(7,23,'TERMINE'),(7,26,'TERMINE'),
(7,28,'TERMINE'),(7,30,'TERMINE'),(7,33,'TERMINE'),(7,35,'TERMINE'),(7,37,'TERMINE'),
(7,38,'TERMINE'),(7,41,'TERMINE'),
-- 100m Brasse Femmes Finale (id=8) PLANIFIE
(8,13,'INSCRIT'),(8,3,'INSCRIT'),(8,28,'INSCRIT'),(8,15,'INSCRIT'),
(8,37,'INSCRIT'),(8,9,'INSCRIT'),(8,33,'INSCRIT'),(8,18,'INSCRIT'),
-- 400m 4 Nages Hommes Qual (id=9) PLANIFIE
(9,12,'INSCRIT'),(9,14,'INSCRIT'),(9,17,'INSCRIT'),(9,19,'INSCRIT'),(9,22,'INSCRIT'),
(9,24,'INSCRIT'),(9,25,'INSCRIT'),(9,27,'INSCRIT'),(9,29,'INSCRIT'),(9,32,'INSCRIT'),
(9,34,'INSCRIT'),(9,36,'INSCRIT'),(9,39,'INSCRIT'),(9,40,'INSCRIT'),
-- 400m 4 Nages Hommes Finale (id=10) PLANIFIE
(10,12,'INSCRIT'),(10,17,'INSCRIT'),(10,22,'INSCRIT'),(10,36,'INSCRIT'),
(10,14,'INSCRIT'),(10,24,'INSCRIT'),(10,39,'INSCRIT'),(10,27,'INSCRIT'),
-- Nat Art Solo F Qual (id=19)
(19,3,'TERMINE'),(19,5,'TERMINE'),(19,7,'TERMINE'),(19,9,'TERMINE'),(19,13,'TERMINE'),
(19,15,'TERMINE'),(19,18,'TERMINE'),(19,20,'TERMINE'),(19,26,'TERMINE'),(19,30,'TERMINE'),
(19,33,'TERMINE'),(19,35,'TERMINE'),(19,37,'TERMINE'),(19,38,'TERMINE'),(19,41,'TERMINE'),
-- Nat Art Solo F Finale (id=20)
(20,37,'TERMINE'),(20,13,'TERMINE'),(20,7,'TERMINE'),(20,3,'TERMINE'),
(20,9,'TERMINE'),(20,33,'TERMINE'),(20,15,'TERMINE'),(20,38,'TERMINE'),
-- Plongeon 3m H Qual (id=23)
(23,2,'TERMINE'),(23,4,'TERMINE'),(23,6,'TERMINE'),(23,8,'TERMINE'),(23,10,'TERMINE'),
(23,12,'TERMINE'),(23,14,'TERMINE'),(23,16,'TERMINE'),(23,17,'TERMINE'),(23,19,'TERMINE'),(23,21,'TERMINE'),
-- Plongeon 3m H Finale EN_COURS (id=24)
(24,2,'EN_COURS'),(24,12,'EN_COURS'),(24,4,'EN_COURS'),(24,17,'EN_COURS'),
(24,6,'EN_COURS'),(24,14,'EN_COURS'),(24,10,'EN_COURS'),(24,21,'EN_COURS'),
-- Eau Libre 10km H Finale (id=27)
(27,17,'TERMINE'),(27,24,'TERMINE'),(27,14,'TERMINE'),(27,4,'TERMINE'),
(27,39,'TERMINE'),(27,27,'TERMINE'),(27,32,'TERMINE'),(27,22,'TERMINE'),
-- Eau Libre 10km F Finale (id=28)
(28,37,'TERMINE'),(28,13,'TERMINE'),(28,3,'TERMINE'),(28,9,'TERMINE'),
(28,18,'TERMINE'),(28,38,'TERMINE'),(28,26,'TERMINE'),(28,33,'TERMINE');

-- ══════════════════════════════
-- 9. RÉSULTATS
-- ══════════════════════════════

-- 100m NL Hommes QUALIFICATION (epreuve_id=1) : TEMPS en secondes
-- 8 premiers → qualification=true
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '47.23', 's', '{}'::jsonb, 'TEMPS', 12, 1, 'VALIDE', true),
(2,  NULL, true,  '47.51', 's', '{}'::jsonb, 'TEMPS', 2,  1, 'VALIDE', true),
(3,  NULL, true,  '47.78', 's', '{}'::jsonb, 'TEMPS', 17, 1, 'VALIDE', true),
(4,  NULL, true,  '47.99', 's', '{}'::jsonb, 'TEMPS', 32, 1, 'VALIDE', true),
(5,  NULL, true,  '48.21', 's', '{}'::jsonb, 'TEMPS', 10, 1, 'VALIDE', true),
(6,  NULL, true,  '48.43', 's', '{}'::jsonb, 'TEMPS', 22, 1, 'VALIDE', true),
(7,  NULL, true,  '48.67', 's', '{}'::jsonb, 'TEMPS', 39, 1, 'VALIDE', true),
(8,  NULL, true,  '48.89', 's', '{}'::jsonb, 'TEMPS', 36, 1, 'VALIDE', true),
(9,  NULL, false, '49.02', 's', '{}'::jsonb, 'TEMPS', 4,  1, 'VALIDE', true),
(10, NULL, false, '49.15', 's', '{}'::jsonb, 'TEMPS', 6,  1, 'VALIDE', true),
(11, NULL, false, '49.28', 's', '{}'::jsonb, 'TEMPS', 14, 1, 'VALIDE', true),
(12, NULL, false, '49.41', 's', '{}'::jsonb, 'TEMPS', 19, 1, 'VALIDE', true),
(13, NULL, false, '49.55', 's', '{}'::jsonb, 'TEMPS', 24, 1, 'VALIDE', true),
(14, NULL, false, '49.68', 's', '{}'::jsonb, 'TEMPS', 25, 1, 'VALIDE', true),
(15, NULL, false, '49.81', 's', '{}'::jsonb, 'TEMPS', 27, 1, 'VALIDE', true),
(16, NULL, false, '49.94', 's', '{}'::jsonb, 'TEMPS', 29, 1, 'VALIDE', true),
(17, NULL, false, '50.12', 's', '{}'::jsonb, 'TEMPS', 34, 1, 'VALIDE', true),
(18, NULL, false, '50.35', 's', '{}'::jsonb, 'TEMPS', 40, 1, 'VALIDE', true);

-- 100m NL Hommes FINALE (epreuve_id=2) : médailles
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1, 'OR',     false, '47.08', 's', '{}'::jsonb, 'TEMPS', 12, 2, 'VALIDE', true),
(2, 'ARGENT', false, '47.31', 's', '{}'::jsonb, 'TEMPS', 2,  2, 'VALIDE', true),
(3, 'BRONZE', false, '47.55', 's', '{}'::jsonb, 'TEMPS', 17, 2, 'VALIDE', true),
(4, NULL,     false, '47.72', 's', '{}'::jsonb, 'TEMPS', 32, 2, 'VALIDE', true),
(5, NULL,     false, '47.89', 's', '{}'::jsonb, 'TEMPS', 10, 2, 'VALIDE', true),
(6, NULL,     false, '48.01', 's', '{}'::jsonb, 'TEMPS', 22, 2, 'VALIDE', true),
(7, NULL,     false, '48.18', 's', '{}'::jsonb, 'TEMPS', 39, 2, 'VALIDE', true),
(8, NULL,     false, '48.42', 's', '{}'::jsonb, 'TEMPS', 36, 2, 'VALIDE', true);

-- 100m NL Femmes QUALIFICATION (epreuve_id=3)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '53.12', 's', '{}'::jsonb, 'TEMPS', 37, 3, 'VALIDE', true),
(2,  NULL, true,  '53.44', 's', '{}'::jsonb, 'TEMPS', 7,  3, 'VALIDE', true),
(3,  NULL, true,  '53.71', 's', '{}'::jsonb, 'TEMPS', 13, 3, 'VALIDE', true),
(4,  NULL, true,  '53.88', 's', '{}'::jsonb, 'TEMPS', 3,  3, 'VALIDE', true),
(5,  NULL, true,  '54.02', 's', '{}'::jsonb, 'TEMPS', 23, 3, 'VALIDE', true),
(6,  NULL, true,  '54.19', 's', '{}'::jsonb, 'TEMPS', 33, 3, 'VALIDE', true),
(7,  NULL, true,  '54.38', 's', '{}'::jsonb, 'TEMPS', 9,  3, 'VALIDE', true),
(8,  NULL, true,  '54.55', 's', '{}'::jsonb, 'TEMPS', 18, 3, 'VALIDE', true),
(9,  NULL, false, '54.72', 's', '{}'::jsonb, 'TEMPS', 7,  3, 'VALIDE', true),
(10, NULL, false, '54.89', 's', '{}'::jsonb, 'TEMPS', 11, 3, 'VALIDE', true),
(11, NULL, false, '55.01', 's', '{}'::jsonb, 'TEMPS', 15, 3, 'VALIDE', true),
(12, NULL, false, '55.18', 's', '{}'::jsonb, 'TEMPS', 20, 3, 'VALIDE', true),
(13, NULL, false, '55.34', 's', '{}'::jsonb, 'TEMPS', 26, 3, 'VALIDE', true),
(14, NULL, false, '55.47', 's', '{}'::jsonb, 'TEMPS', 28, 3, 'VALIDE', true),
(15, NULL, false, '55.61', 's', '{}'::jsonb, 'TEMPS', 30, 3, 'VALIDE', true),
(16, NULL, false, '55.78', 's', '{}'::jsonb, 'TEMPS', 35, 3, 'VALIDE', true),
(17, NULL, false, '55.92', 's', '{}'::jsonb, 'TEMPS', 38, 3, 'VALIDE', true),
(18, NULL, false, '56.12', 's', '{}'::jsonb, 'TEMPS', 41, 3, 'VALIDE', true);

-- 100m NL Femmes FINALE (epreuve_id=4)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1, 'OR',     false, '52.98', 's', '{}'::jsonb, 'TEMPS', 37, 4, 'VALIDE', true),
(2, 'ARGENT', false, '53.21', 's', '{}'::jsonb, 'TEMPS', 7,  4, 'VALIDE', true),
(3, 'BRONZE', false, '53.49', 's', '{}'::jsonb, 'TEMPS', 13, 4, 'VALIDE', true),
(4, NULL,     false, '53.66', 's', '{}'::jsonb, 'TEMPS', 3,  4, 'VALIDE', true),
(5, NULL,     false, '53.83', 's', '{}'::jsonb, 'TEMPS', 23, 4, 'VALIDE', true),
(6, NULL,     false, '54.01', 's', '{}'::jsonb, 'TEMPS', 33, 4, 'VALIDE', true),
(7, NULL,     false, '54.18', 's', '{}'::jsonb, 'TEMPS', 9,  4, 'VALIDE', true),
(8, NULL,     false, '54.35', 's', '{}'::jsonb, 'TEMPS', 18, 4, 'VALIDE', true);

-- 200m Papillon Hommes QUALIFICATION (epreuve_id=5)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '1:52.41', 'min:s', '{}'::jsonb, 'TEMPS', 12, 5, 'VALIDE', true),
(2,  NULL, true,  '1:52.88', 'min:s', '{}'::jsonb, 'TEMPS', 17, 5, 'VALIDE', true),
(3,  NULL, true,  '1:53.12', 'min:s', '{}'::jsonb, 'TEMPS', 2,  5, 'VALIDE', true),
(4,  NULL, true,  '1:53.45', 'min:s', '{}'::jsonb, 'TEMPS', 14, 5, 'VALIDE', true),
(5,  NULL, true,  '1:53.78', 'min:s', '{}'::jsonb, 'TEMPS', 36, 5, 'VALIDE', true),
(6,  NULL, true,  '1:54.01', 'min:s', '{}'::jsonb, 'TEMPS', 39, 5, 'VALIDE', true),
(7,  NULL, true,  '1:54.33', 'min:s', '{}'::jsonb, 'TEMPS', 25, 5, 'VALIDE', true),
(8,  NULL, true,  '1:54.67', 'min:s', '{}'::jsonb, 'TEMPS', 31, 5, 'VALIDE', true),
(9,  NULL, false, '1:54.89', 'min:s', '{}'::jsonb, 'TEMPS', 6,  5, 'VALIDE', true),
(10, NULL, false, '1:55.12', 'min:s', '{}'::jsonb, 'TEMPS', 16, 5, 'VALIDE', true),
(11, NULL, false, '1:55.44', 'min:s', '{}'::jsonb, 'TEMPS', 19, 5, 'VALIDE', true),
(12, NULL, false, '1:55.78', 'min:s', '{}'::jsonb, 'TEMPS', 21, 5, 'VALIDE', true),
(13, NULL, false, '1:56.01', 'min:s', '{}'::jsonb, 'TEMPS', 27, 5, 'VALIDE', true),
(14, NULL, false, '1:56.34', 'min:s', '{}'::jsonb, 'TEMPS', 29, 5, 'VALIDE', true),
(15, NULL, false, '1:56.67', 'min:s', '{}'::jsonb, 'TEMPS', 34, 5, 'VALIDE', true),
(16, NULL, false, '1:57.02', 'min:s', '{}'::jsonb, 'TEMPS', 40, 5, 'VALIDE', true);

-- 100m Brasse Femmes QUALIFICATION (epreuve_id=7)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '58.61', 's', '{}'::jsonb, 'TEMPS', 13, 7, 'VALIDE', true),
(2,  NULL, true,  '58.89', 's', '{}'::jsonb, 'TEMPS', 3,  7, 'VALIDE', true),
(3,  NULL, true,  '59.12', 's', '{}'::jsonb, 'TEMPS', 28, 7, 'VALIDE', true),
(4,  NULL, true,  '59.34', 's', '{}'::jsonb, 'TEMPS', 15, 7, 'VALIDE', true),
(5,  NULL, true,  '59.58', 's', '{}'::jsonb, 'TEMPS', 37, 7, 'VALIDE', true),
(6,  NULL, true,  '59.78', 's', '{}'::jsonb, 'TEMPS', 9,  7, 'VALIDE', true),
(7,  NULL, true,  '1:00.01', 's', '{}'::jsonb, 'TEMPS', 33, 7, 'VALIDE', true),
(8,  NULL, true,  '1:00.24', 's', '{}'::jsonb, 'TEMPS', 18, 7, 'VALIDE', true),
(9,  NULL, false, '1:00.45', 's', '{}'::jsonb, 'TEMPS', 5,  7, 'VALIDE', true),
(10, NULL, false, '1:00.68', 's', '{}'::jsonb, 'TEMPS', 11, 7, 'VALIDE', true),
(11, NULL, false, '1:00.91', 's', '{}'::jsonb, 'TEMPS', 20, 7, 'VALIDE', true),
(12, NULL, false, '1:01.14', 's', '{}'::jsonb, 'TEMPS', 23, 7, 'VALIDE', true),
(13, NULL, false, '1:01.38', 's', '{}'::jsonb, 'TEMPS', 26, 7, 'VALIDE', true),
(14, NULL, false, '1:01.61', 's', '{}'::jsonb, 'TEMPS', 30, 7, 'VALIDE', true),
(15, NULL, false, '1:01.85', 's', '{}'::jsonb, 'TEMPS', 35, 7, 'VALIDE', true),
(16, NULL, false, '1:02.09', 's', '{}'::jsonb, 'TEMPS', 38, 7, 'VALIDE', true),
(17, NULL, false, '1:02.34', 's', '{}'::jsonb, 'TEMPS', 41, 7, 'VALIDE', true);

-- Water Polo Hommes Phase groupes (epreuve_id=11) — SCORE par équipe
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, equipe_id, epreuve_id, statut, published) VALUES
(1, NULL, true,  '3', 'victoires', '{}'::jsonb, 'SCORE', 1, 11, 'VALIDE', true),
(2, NULL, true,  '3', 'victoires', '{}'::jsonb, 'SCORE', 3, 11, 'VALIDE', true),
(3, NULL, true,  '2', 'victoires', '{}'::jsonb, 'SCORE', 5, 11, 'VALIDE', true),
(4, NULL, true,  '2', 'victoires', '{}'::jsonb, 'SCORE', 7, 11, 'VALIDE', true),
(5, NULL, false, '1', 'victoires', '{}'::jsonb, 'SCORE', 2, 11, 'VALIDE', true),
(6, NULL, false, '1', 'victoires', '{}'::jsonb, 'SCORE', 4, 11, 'VALIDE', true),
(7, NULL, false, '0', 'victoires', '{}'::jsonb, 'SCORE', 6, 11, 'VALIDE', true),
(8, NULL, false, '0', 'victoires', '{}'::jsonb, 'SCORE', 8, 11, 'VALIDE', true);

-- Water Polo Femmes Phase groupes (epreuve_id=12)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, equipe_id, epreuve_id, statut, published) VALUES
(1, NULL, true,  '3', 'victoires', '{}'::jsonb, 'SCORE', 2, 12, 'VALIDE', true),
(2, NULL, true,  '3', 'victoires', '{}'::jsonb, 'SCORE', 6, 12, 'VALIDE', true),
(3, NULL, true,  '2', 'victoires', '{}'::jsonb, 'SCORE', 4, 12, 'VALIDE', true),
(4, NULL, true,  '2', 'victoires', '{}'::jsonb, 'SCORE', 8, 12, 'VALIDE', true),
(5, NULL, false, '1', 'victoires', '{}'::jsonb, 'SCORE', 1, 12, 'VALIDE', true),
(6, NULL, false, '1', 'victoires', '{}'::jsonb, 'SCORE', 3, 12, 'VALIDE', true),
(7, NULL, false, '0', 'victoires', '{}'::jsonb, 'SCORE', 5, 12, 'VALIDE', true),
(8, NULL, false, '0', 'victoires', '{}'::jsonb, 'SCORE', 7, 12, 'VALIDE', true);

-- Water Polo Hommes Quarts de finale (epreuve_id=13)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, equipe_id, epreuve_id, statut, published) VALUES
(1, NULL, true,  '1', 'victoire', '{}'::jsonb, 'SCORE', 1, 13, 'VALIDE', true),
(2, NULL, true,  '1', 'victoire', '{}'::jsonb, 'SCORE', 3, 13, 'VALIDE', true),
(3, NULL, false, '0', 'victoire', '{}'::jsonb, 'SCORE', 5, 13, 'VALIDE', true),
(4, NULL, false, '0', 'victoire', '{}'::jsonb, 'SCORE', 7, 13, 'VALIDE', true);

-- Water Polo Femmes Quarts de finale (epreuve_id=14)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, equipe_id, epreuve_id, statut, published) VALUES
(1, NULL, true,  '1', 'victoire', '{}'::jsonb, 'SCORE', 2, 14, 'VALIDE', true),
(2, NULL, true,  '1', 'victoire', '{}'::jsonb, 'SCORE', 6, 14, 'VALIDE', true),
(3, NULL, false, '0', 'victoire', '{}'::jsonb, 'SCORE', 4, 14, 'VALIDE', true),
(4, NULL, false, '0', 'victoire', '{}'::jsonb, 'SCORE', 8, 14, 'VALIDE', true);

-- Water Polo Hommes Demi-finales EN_COURS (epreuve_id=15)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, equipe_id, epreuve_id, statut, published) VALUES
(NULL, NULL, false, 'En cours', NULL, '{}'::jsonb, 'SCORE', 1, 15, 'EN_ATTENTE', false),
(NULL, NULL, false, 'En cours', NULL, '{}'::jsonb, 'SCORE', 3, 15, 'EN_ATTENTE', false);

-- Natation Artistique Solo Femmes QUALIFICATION (epreuve_id=19)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '89.0', 'points', '{}'::jsonb, 'POINTS', 37, 19, 'VALIDE', true),
(2,  NULL, true,  '87.5', 'points', '{}'::jsonb, 'POINTS', 13, 19, 'VALIDE', true),
(3,  NULL, true,  '86.2', 'points', '{}'::jsonb, 'POINTS', 7,  19, 'VALIDE', true),
(4,  NULL, true,  '85.1', 'points', '{}'::jsonb, 'POINTS', 3,  19, 'VALIDE', true),
(5,  NULL, true,  '84.0', 'points', '{}'::jsonb, 'POINTS', 9,  19, 'VALIDE', true),
(6,  NULL, true,  '83.2', 'points', '{}'::jsonb, 'POINTS', 33, 19, 'VALIDE', true),
(7,  NULL, true,  '82.5', 'points', '{}'::jsonb, 'POINTS', 15, 19, 'VALIDE', true),
(8,  NULL, true,  '81.8', 'points', '{}'::jsonb, 'POINTS', 38, 19, 'VALIDE', true),
(9,  NULL, false, '80.9', 'points', '{}'::jsonb, 'POINTS', 5,  19, 'VALIDE', true),
(10, NULL, false, '80.1', 'points', '{}'::jsonb, 'POINTS', 18, 19, 'VALIDE', true),
(11, NULL, false, '79.4', 'points', '{}'::jsonb, 'POINTS', 20, 19, 'VALIDE', true),
(12, NULL, false, '78.8', 'points', '{}'::jsonb, 'POINTS', 26, 19, 'VALIDE', true),
(13, NULL, false, '78.2', 'points', '{}'::jsonb, 'POINTS', 30, 19, 'VALIDE', true),
(14, NULL, false, '77.6', 'points', '{}'::jsonb, 'POINTS', 35, 19, 'VALIDE', true),
(15, NULL, false, '77.0', 'points', '{}'::jsonb, 'POINTS', 41, 19, 'VALIDE', true);

-- Natation Artistique Solo Femmes FINALE (epreuve_id=20)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1, 'OR',     false, '91.2', 'points', '{}'::jsonb, 'POINTS', 37, 20, 'VALIDE', true),
(2, 'ARGENT', false, '89.8', 'points', '{}'::jsonb, 'POINTS', 13, 20, 'VALIDE', true),
(3, 'BRONZE', false, '88.5', 'points', '{}'::jsonb, 'POINTS', 7,  20, 'VALIDE', true),
(4, NULL,     false, '87.1', 'points', '{}'::jsonb, 'POINTS', 3,  20, 'VALIDE', true),
(5, NULL,     false, '85.9', 'points', '{}'::jsonb, 'POINTS', 9,  20, 'VALIDE', true),
(6, NULL,     false, '84.6', 'points', '{}'::jsonb, 'POINTS', 33, 20, 'VALIDE', true),
(7, NULL,     false, '83.2', 'points', '{}'::jsonb, 'POINTS', 15, 20, 'VALIDE', true),
(8, NULL,     false, '82.1', 'points', '{}'::jsonb, 'POINTS', 38, 20, 'VALIDE', true);

-- Plongeon 3m Hommes QUALIFICATION (epreuve_id=23)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1,  NULL, true,  '92.5', 'points', '{}'::jsonb, 'POINTS', 2,  23, 'VALIDE', true),
(2,  NULL, true,  '91.2', 'points', '{}'::jsonb, 'POINTS', 12, 23, 'VALIDE', true),
(3,  NULL, true,  '89.8', 'points', '{}'::jsonb, 'POINTS', 4,  23, 'VALIDE', true),
(4,  NULL, true,  '88.4', 'points', '{}'::jsonb, 'POINTS', 17, 23, 'VALIDE', true),
(5,  NULL, true,  '87.1', 'points', '{}'::jsonb, 'POINTS', 6,  23, 'VALIDE', true),
(6,  NULL, true,  '85.9', 'points', '{}'::jsonb, 'POINTS', 14, 23, 'VALIDE', true),
(7,  NULL, true,  '84.7', 'points', '{}'::jsonb, 'POINTS', 10, 23, 'VALIDE', true),
(8,  NULL, true,  '83.6', 'points', '{}'::jsonb, 'POINTS', 21, 23, 'VALIDE', true),
(9,  NULL, false, '82.5', 'points', '{}'::jsonb, 'POINTS', 8,  23, 'VALIDE', true),
(10, NULL, false, '81.3', 'points', '{}'::jsonb, 'POINTS', 19, 23, 'VALIDE', true),
(11, NULL, false, '80.2', 'points', '{}'::jsonb, 'POINTS', 16, 23, 'VALIDE', true);

-- Plongeon 3m Hommes FINALE EN_COURS (epreuve_id=24) — pas de résultats published
-- (athlètes EN_COURS, pas de résultats définitifs)

-- Eau Libre 10km Hommes FINALE (epreuve_id=27) — temps en h:mm:ss
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1, 'OR',     false, '1:50:42', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 17, 27, 'VALIDE', true),
(2, 'ARGENT', false, '1:51:08', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 24, 27, 'VALIDE', true),
(3, 'BRONZE', false, '1:51:35', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 14, 27, 'VALIDE', true),
(4, NULL,     false, '1:52:01', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 4,  27, 'VALIDE', true),
(5, NULL,     false, '1:53:18', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 39, 27, 'VALIDE', true),
(6, NULL,     false, '1:54:45', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 27, 27, 'VALIDE', true),
(7, NULL,     false, '1:55:59', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 32, 27, 'VALIDE', true),
(8, NULL,     false, '1:57:12', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 22, 27, 'VALIDE', true);

-- Eau Libre 10km Femmes FINALE (epreuve_id=28)
INSERT INTO resultats (classement, medaille, qualification, valeur_principale, unite, details_performance, type_performance, athlete_id, epreuve_id, statut, published) VALUES
(1, 'OR',     false, '1:51:14', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 37, 28, 'VALIDE', true),
(2, 'ARGENT', false, '1:51:52', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 13, 28, 'VALIDE', true),
(3, 'BRONZE', false, '1:52:31', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 3,  28, 'VALIDE', true),
(4, NULL,     false, '1:53:08', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 9,  28, 'VALIDE', true),
(5, NULL,     false, '1:54:22', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 18, 28, 'VALIDE', true),
(6, NULL,     false, '1:55:41', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 38, 28, 'VALIDE', true),
(7, NULL,     false, '1:56:58', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 26, 28, 'VALIDE', true),
(8, NULL,     false, '1:58:03', 'h:mm:ss', '{}'::jsonb, 'TEMPS', 33, 28, 'VALIDE', true);

-- ══════════════════════════════
-- 10. VOLONTAIRES & TÂCHES
-- ══════════════════════════════
-- Étape 3 : INSERT profils volunteers (auth_user_id = users.id correspondant)
-- UUIDs fixes pour pouvoir les référencer dans volunteer_tasks

INSERT INTO volunteers (id, auth_user_id, first_name, last_name, email, phone_number, languages, preferred_task_types, active, availabilities_json) VALUES
(gen_random_uuid(), 47, 'Sophie', 'Martin',   'sophie.martin@volunteer.fr',  '0612345601', 'Français,Anglais',   'ACCUEIL,ORIENTATION,INFORMATION',              true, '[{"dayOfWeek":"MONDAY","startTime":"08:00","endTime":"16:00"},{"dayOfWeek":"TUESDAY","startTime":"08:00","endTime":"16:00"},{"dayOfWeek":"WEDNESDAY","startTime":"08:00","endTime":"16:00"}]'),
(gen_random_uuid(), 48, 'Thomas', 'Leclerc',  'thomas.leclerc@volunteer.fr', '0612345602', 'Français,Espagnol',  'SECURITE,BILLETTERIE,ACCUEIL',                 true, '[{"dayOfWeek":"THURSDAY","startTime":"08:00","endTime":"20:00"},{"dayOfWeek":"FRIDAY","startTime":"08:00","endTime":"20:00"}]'),
(gen_random_uuid(), 49, 'Amelia', 'Lambert',  'amelia.lambert@volunteer.fr', '0612345603', 'Français,Anglais',   'PREMIERS_SECOURS,DISTRIBUTION_EAU,SECURITE',   true, '[{"dayOfWeek":"SATURDAY","startTime":"06:00","endTime":"18:00"},{"dayOfWeek":"SUNDAY","startTime":"06:00","endTime":"18:00"}]'),
(gen_random_uuid(), 50, 'Nicolas','Girard',   'nicolas.girard@volunteer.fr', '0612345604', 'Français,Allemand',  'ORIENTATION,INFORMATION,ACCUEIL',              true, '[{"dayOfWeek":"MONDAY","startTime":"09:00","endTime":"17:00"},{"dayOfWeek":"WEDNESDAY","startTime":"09:00","endTime":"17:00"},{"dayOfWeek":"FRIDAY","startTime":"09:00","endTime":"17:00"}]'),
(gen_random_uuid(), 51, 'Camille','Robert',   'camille.robert@volunteer.fr', '0612345605', 'Français',           'ACCOMPAGNEMENT_ATHLETES,INFORMATION,ACCUEIL',  true, '[{"dayOfWeek":"TUESDAY","startTime":"07:00","endTime":"19:00"},{"dayOfWeek":"THURSDAY","startTime":"07:00","endTime":"19:00"}]'),
(gen_random_uuid(), 52, 'Julien', 'Petit',    'julien.petit@volunteer.fr',   '0612345606', 'Français,Anglais',   'SUPPORT_LOGISTIQUE,NETTOYAGE,DISTRIBUTION_EAU', true, '[{"dayOfWeek":"MONDAY","startTime":"06:00","endTime":"14:00"},{"dayOfWeek":"TUESDAY","startTime":"06:00","endTime":"14:00"},{"dayOfWeek":"WEDNESDAY","startTime":"06:00","endTime":"14:00"}]'),
(gen_random_uuid(), 53, 'Léa',    'Moreau',   'lea.moreau@volunteer.fr',     '0612345607', 'Français,Espagnol',  'ACCUEIL,BILLETTERIE,ORIENTATION',              true, '[{"dayOfWeek":"FRIDAY","startTime":"10:00","endTime":"20:00"},{"dayOfWeek":"SATURDAY","startTime":"10:00","endTime":"20:00"}]'),
(gen_random_uuid(), 54, 'Marc',   'Simon',    'marc.simon@volunteer.fr',     '0612345608', 'Français',           'SECURITE,ACCUEIL,NETTOYAGE',                   true, '[{"dayOfWeek":"SATURDAY","startTime":"08:00","endTime":"20:00"},{"dayOfWeek":"SUNDAY","startTime":"08:00","endTime":"20:00"}]'),
(gen_random_uuid(), 55, 'Elena',  'Garcia',   'elena.garcia@volunteer.fr',   '0612345609', 'Français,Anglais,Espagnol', 'ACCOMPAGNEMENT_ATHLETES,ORIENTATION,ACCUEIL', true, '[{"dayOfWeek":"MONDAY","startTime":"08:00","endTime":"18:00"},{"dayOfWeek":"TUESDAY","startTime":"08:00","endTime":"18:00"},{"dayOfWeek":"THURSDAY","startTime":"08:00","endTime":"18:00"}]'),
(gen_random_uuid(), 56, 'Pierre', 'Henry',    'pierre.henry@volunteer.fr',   '0612345610', 'Français,Allemand',  'SUPPORT_LOGISTIQUE,SECURITE,DISTRIBUTION_EAU', true, '[{"dayOfWeek":"WEDNESDAY","startTime":"07:00","endTime":"15:00"},{"dayOfWeek":"FRIDAY","startTime":"07:00","endTime":"15:00"}]'),
(gen_random_uuid(), 57, 'Sarah',  'David',    'sarah.david@volunteer.fr',    '0612345611', 'Français,Anglais',   'PREMIERS_SECOURS,ACCOMPAGNEMENT_ATHLETES,INFORMATION', true, '[{"dayOfWeek":"TUESDAY","startTime":"08:00","endTime":"16:00"},{"dayOfWeek":"WEDNESDAY","startTime":"08:00","endTime":"16:00"},{"dayOfWeek":"THURSDAY","startTime":"08:00","endTime":"16:00"}]'),
(gen_random_uuid(), 58, 'Antoine','Blanc',    'antoine.blanc@volunteer.fr',  '0612345612', 'Français,Espagnol',  'BILLETTERIE,ACCUEIL,ORIENTATION',              true, '[{"dayOfWeek":"THURSDAY","startTime":"10:00","endTime":"22:00"},{"dayOfWeek":"FRIDAY","startTime":"10:00","endTime":"22:00"},{"dayOfWeek":"SATURDAY","startTime":"10:00","endTime":"22:00"}]');

-- Récupérer les UUIDs des volontaires pour les tâches
-- (on utilise les auth_user_id pour matcher)

-- volunteer_tasks — 17 tâches couvrant tous les TaskType
-- assigned_volunteer_ids : UUID du volontaire assigné (stocké comme string)
INSERT INTO volunteer_tasks (id, title, description, task_date, start_time, end_time, location, task_type, assigned_volunteer_ids, required_languages) VALUES

-- ACCUEIL (3 tâches)
(gen_random_uuid(), 'Accueil entrée principale Nord',
 'Accueillir les visiteurs à l''entrée nord du CAOMGP, vérifier les accréditations',
 '2026-08-10', '08:00', '14:00', 'CAOMGP — Entrée Nord', 'ACCUEIL',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 47), 'Français,Anglais'),

(gen_random_uuid(), 'Accueil entrée principale Sud',
 'Accueillir les visiteurs à l''entrée sud, distribuer les programmes',
 '2026-08-10', '08:00', '14:00', 'CAOMGP — Entrée Sud', 'ACCUEIL',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 53), 'Français'),

(gen_random_uuid(), 'Accueil délégations sportives',
 'Accueil et orientation des délégations nationales à leur arrivée',
 '2026-07-31', '07:00', '13:00', 'CAOMGP — Hall VIP', 'ACCUEIL',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 51), 'Français,Anglais'),

-- SECURITE (3 tâches)
(gen_random_uuid(), 'Contrôle périmètre zone compétition',
 'Surveillance et contrôle d''accès au périmètre de compétition',
 '2026-08-11', '07:00', '19:00', 'CAOMGP — Zone bassin', 'SECURITE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 48), 'Français'),

(gen_random_uuid(), 'Sécurité tribune Est',
 'Maintien de l''ordre et sécurité dans la tribune Est',
 '2026-08-11', '14:00', '22:00', 'CAOMGP — Tribune Est', 'SECURITE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 54), 'Français'),

(gen_random_uuid(), 'Sécurité site eau libre',
 'Surveillance du périmètre de l''épreuve eau libre sur la Seine',
 '2026-08-05', '06:00', '14:00', 'La Seine — Bras de Grenelle', 'SECURITE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 49), 'Français,Anglais'),

-- ORIENTATION (2 tâches)
(gen_random_uuid(), 'Orientation interne CAOMGP',
 'Guider les spectateurs vers leurs places et les différents espaces du site',
 '2026-08-12', '09:00', '17:00', 'CAOMGP — Couloirs', 'ORIENTATION',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 50), 'Français,Allemand'),

(gen_random_uuid(), 'Orientation transports publics',
 'Indiquer les accès transports et stationnements aux visiteurs',
 '2026-08-12', '09:00', '17:00', 'CAOMGP — Parvis', 'ORIENTATION',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 55), 'Français,Anglais'),

-- ACCOMPAGNEMENT_ATHLETES (2 tâches)
(gen_random_uuid(), 'Accompagnement athlètes zone échauffement',
 'Accompagner les athlètes de leur vestiaire à la zone d''échauffement',
 '2026-08-10', '07:00', '15:00', 'CAOMGP — Zone échauffement', 'ACCOMPAGNEMENT_ATHLETES',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 51), 'Français,Anglais'),

(gen_random_uuid(), 'Liaison athlètes — podium',
 'Accompagner les athlètes médaillés vers la cérémonie de remise des prix',
 '2026-08-11', '18:00', '22:00', 'CAOMGP — Podium', 'ACCOMPAGNEMENT_ATHLETES',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 57), 'Français,Anglais'),

-- INFORMATION (2 tâches)
(gen_random_uuid(), 'Point information presse',
 'Accueil et information des journalistes et médias accrédités',
 '2026-08-10', '08:00', '20:00', 'CAOMGP — Centre de presse', 'INFORMATION',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 55), 'Français,Anglais'),

(gen_random_uuid(), 'Point information spectateurs',
 'Répondre aux questions des spectateurs sur le programme et les épreuves',
 '2026-08-13', '09:00', '19:00', 'CAOMGP — Hall principal', 'INFORMATION',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 50), 'Français,Anglais'),

-- SUPPORT_LOGISTIQUE (1 tâche)
(gen_random_uuid(), 'Gestion du matériel technique',
 'Réception, stockage et distribution du matériel de compétition',
 '2026-07-30', '07:00', '15:00', 'CAOMGP — Zone logistique', 'SUPPORT_LOGISTIQUE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 52), 'Français'),

-- PREMIERS_SECOURS (1 tâche)
(gen_random_uuid(), 'Poste de premiers secours bassin',
 'Surveillance médicale et premiers secours au bord du bassin',
 '2026-08-10', '08:00', '20:00', 'CAOMGP — Poste médical bassin', 'PREMIERS_SECOURS',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 49), 'Français,Anglais'),

-- DISTRIBUTION_EAU (1 tâche)
(gen_random_uuid(), 'Distribution eau et ravitaillement',
 'Distribution de bouteilles d''eau aux spectateurs et bénévoles',
 '2026-08-11', '10:00', '18:00', 'CAOMGP — Zones spectateurs', 'DISTRIBUTION_EAU',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 56), 'Français'),

-- BILLETTERIE (1 tâche)
(gen_random_uuid(), 'Contrôle billets entrée VIP',
 'Vérification et contrôle des billets et accréditations espace VIP',
 '2026-08-08', '14:00', '22:00', 'CAOMGP — Entrée VIP', 'BILLETTERIE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 58), 'Français,Espagnol'),

-- NETTOYAGE (1 tâche)
(gen_random_uuid(), 'Nettoyage fin de journée',
 'Nettoyage des tribunes et des espaces communs après les épreuves du soir',
 '2026-08-11', '21:00', '23:30', 'CAOMGP — Tribunes et couloirs', 'NETTOYAGE',
 (SELECT id::text FROM volunteers WHERE auth_user_id = 52), 'Français');

-- ══════════════════════════════
-- 11. INCIDENTS
-- ══════════════════════════════
INSERT INTO incident (description, impact_level, type, lieu_id, competition_id, status, reported_by, reported_at, updated_at, resolved_at) VALUES
('Défaillance du système de chronométrage du bassin 2 — arrêt momentané pendant 15 minutes',
 'MOYEN', 'TECHNIQUE', 1, 5, 'RESOLU', 'comm.natation',
 '2026-08-02 10:15:00', '2026-08-02 10:45:00', '2026-08-02 10:45:00'),

('Incident médical bénin : malaise d''un athlète lors de l''échauffement, prise en charge rapide',
 'FAIBLE', 'MEDICAL', 1, 4, 'RESOLU', 'comm.waterpolo',
 '2026-08-04 09:30:00', '2026-08-04 09:55:00', '2026-08-04 09:55:00'),

('Fausse alerte sécurité : déclenchement intempestif du périmètre de sécurité côté ouest',
 'ELEVE', 'SECURITE', 1, 4, 'RESOLU', 'comm.waterpolo',
 '2026-08-06 14:20:00', '2026-08-06 16:00:00', '2026-08-06 16:00:00'),

('Conditions météorologiques dangereuses — vents forts et houle — épreuve eau libre reportée',
 'CRITIQUE', 'METEO', 2, 3, 'ACTIF', 'comm.eau.libre',
 '2026-08-09 06:00:00', '2026-08-09 07:00:00', NULL),

('Problème d''accréditation de la délégation allemande — accès refusé à certaines zones',
 'FAIBLE', 'TECHNIQUE', 1, NULL, 'ACTIF', 'marius.admin',
 '2026-08-11 08:45:00', '2026-08-11 09:00:00', NULL);

-- ══════════════════════════════
-- 12. FAN ZONES
-- ══════════════════════════════
INSERT INTO fan_zone (nom, description, latitude, longitude, capacite_max, adresse) VALUES
('Fan Zone Trocadéro',
 'Fan zone officielle au Trocadéro avec grand écran et animations',
 48.8614, 2.2886, 5000, 'Place du Trocadéro, Paris 75016'),       -- id=1
('Fan Zone Saint-Denis',
 'Fan zone officielle devant le Centre Aquatique Olympique',
 48.9244, 2.3601, 8000, 'Parvis du CAOMGP, Saint-Denis 93200'),   -- id=2
('Fan Zone Champs-Élysées',
 'Fan zone sur les Champs-Élysées avec boutique officielle',
 48.8698, 2.3078, 10000, 'Avenue des Champs-Élysées, Paris 75008'); -- id=3

INSERT INTO fan_zone_service (fan_zone_id, type_service) VALUES
-- Trocadéro (id=1)
(1, 'ECRAN_GEANT'), (1, 'RESTAURATION'), (1, 'BOUTIQUE'),
-- Saint-Denis (id=2)
(2, 'ECRAN_GEANT'), (2, 'RESTAURATION'), (2, 'MEDICAL'),
-- Champs-Élysées (id=3)
(3, 'ECRAN_GEANT'), (3, 'BOUTIQUE'), (3, 'MEDICAL');

-- Positions GPS des athlètes eau libre (~5 par athlète, epreuve_id=27 H et 28 F)
INSERT INTO athlete_position (athlete_id, latitude, longitude, timestamp) VALUES
-- 10km H 2026-08-05 : athlètes 17,24,14,4,39,27,32,22
(17, 48.8512, 2.2830, '2026-08-05 08:00:00'), (17, 48.8530, 2.2910, '2026-08-05 08:30:00'),
(17, 48.8548, 2.3010, '2026-08-05 09:00:00'), (17, 48.8522, 2.3090, '2026-08-05 09:30:00'),
(17, 48.8510, 2.2870, '2026-08-05 10:10:00'),
(24, 48.8510, 2.2828, '2026-08-05 08:00:00'), (24, 48.8528, 2.2908, '2026-08-05 08:31:00'),
(24, 48.8546, 2.3008, '2026-08-05 09:01:00'), (24, 48.8520, 2.3088, '2026-08-05 09:31:00'),
(24, 48.8509, 2.2868, '2026-08-05 10:12:00'),
(14, 48.8508, 2.2826, '2026-08-05 08:00:00'), (14, 48.8526, 2.2906, '2026-08-05 08:32:00'),
(14, 48.8544, 2.3006, '2026-08-05 09:02:00'), (14, 48.8518, 2.3086, '2026-08-05 09:32:00'),
(14, 48.8508, 2.2866, '2026-08-05 10:14:00'),
-- 10km F 2026-08-06 : athlètes 37,13,3,9,18,38,26,33
(37, 48.8514, 2.2832, '2026-08-06 08:00:00'), (37, 48.8532, 2.2912, '2026-08-06 08:32:00'),
(37, 48.8550, 2.3012, '2026-08-06 09:04:00'), (37, 48.8524, 2.3092, '2026-08-06 09:36:00'),
(37, 48.8512, 2.2872, '2026-08-06 10:15:00'),
(13, 48.8512, 2.2830, '2026-08-06 08:00:00'), (13, 48.8530, 2.2910, '2026-08-06 08:33:00'),
(13, 48.8548, 2.3010, '2026-08-06 09:05:00'), (13, 48.8522, 2.3090, '2026-08-06 09:37:00'),
(13, 48.8511, 2.2870, '2026-08-06 10:16:00');

-- ══════════════════════════════
-- 13. ABONNEMENTS
-- ══════════════════════════════
-- 35 abonnements : UNIQUE (user_id, competition_id)
-- Spectateurs (59-80) et quelques autres utilisateurs
INSERT INTO abonnements (id, user_id, competition_id, date_abonnement, notifications_actives, status) VALUES
-- ACTIF (28 abonnements)
(gen_random_uuid(), 59, 5, '2026-05-10 10:00:00', true, 'ACTIF'),
(gen_random_uuid(), 59, 4, '2026-05-11 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 60, 5, '2026-05-12 09:00:00', true, 'ACTIF'),
(gen_random_uuid(), 60, 3, '2026-05-13 14:00:00', true, 'ACTIF'),
(gen_random_uuid(), 61, 5, '2026-05-14 10:30:00', true, 'ACTIF'),
(gen_random_uuid(), 61, 1, '2026-05-15 11:30:00', true, 'ACTIF'),
(gen_random_uuid(), 62, 4, '2026-05-16 15:00:00', true, 'ACTIF'),
(gen_random_uuid(), 62, 2, '2026-05-17 16:00:00', true, 'ACTIF'),
(gen_random_uuid(), 63, 5, '2026-06-01 09:00:00', true, 'ACTIF'),
(gen_random_uuid(), 63, 3, '2026-06-02 10:00:00', true, 'ACTIF'),
(gen_random_uuid(), 64, 5, '2026-06-03 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 64, 4, '2026-06-04 12:00:00', true, 'ACTIF'),
(gen_random_uuid(), 65, 1, '2026-06-05 13:00:00', true, 'ACTIF'),
(gen_random_uuid(), 65, 2, '2026-06-06 14:00:00', true, 'ACTIF'),
(gen_random_uuid(), 66, 5, '2026-06-07 09:30:00', true, 'ACTIF'),
(gen_random_uuid(), 66, 4, '2026-06-08 10:30:00', true, 'ACTIF'),
(gen_random_uuid(), 67, 3, '2026-06-15 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 68, 5, '2026-06-20 09:00:00', true, 'ACTIF'),
(gen_random_uuid(), 69, 4, '2026-06-25 10:00:00', true, 'ACTIF'),
(gen_random_uuid(), 70, 5, '2026-07-01 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 71, 1, '2026-07-05 09:00:00', true, 'ACTIF'),
(gen_random_uuid(), 72, 2, '2026-07-10 10:00:00', true, 'ACTIF'),
(gen_random_uuid(), 73, 5, '2026-07-15 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 74, 4, '2026-07-20 12:00:00', true, 'ACTIF'),
(gen_random_uuid(), 75, 5, '2026-07-22 09:00:00', true, 'ACTIF'),
(gen_random_uuid(), 76, 3, '2026-07-24 10:00:00', true, 'ACTIF'),
(gen_random_uuid(), 77, 5, '2026-07-26 11:00:00', true, 'ACTIF'),
(gen_random_uuid(), 78, 4, '2026-07-28 12:00:00', true, 'ACTIF'),
-- DESABONNE (5 abonnements)
(gen_random_uuid(), 79, 5, '2026-05-20 09:00:00', false, 'DESABONNE'),
(gen_random_uuid(), 79, 4, '2026-05-21 10:00:00', false, 'DESABONNE'),
(gen_random_uuid(), 80, 5, '2026-05-22 11:00:00', false, 'DESABONNE'),
(gen_random_uuid(), 80, 3, '2026-05-23 12:00:00', false, 'DESABONNE'),
(gen_random_uuid(), 75, 4, '2026-06-10 09:00:00', false, 'DESABONNE'),
-- SUSPENDU (2 abonnements)
(gen_random_uuid(), 76, 5, '2026-06-12 10:00:00', false, 'SUSPENDU'),
(gen_random_uuid(), 77, 4, '2026-06-14 11:00:00', false, 'SUSPENDU');

-- ══════════════════════════════
-- 14. NOTIFICATIONS
-- ══════════════════════════════
-- source_event_id doit être UNIQUE par (source_event_id, id_spectateur)
INSERT INTO notification (type, contenu, date_envoi, id_event, id_spectateur, source_event_id, lu) VALUES
-- Résultats finales terminées (epreuve id=2,4,20,27,28)
('RESULTAT', 'Résultats disponibles : 100m Nage Libre Hommes — Finale. Vainqueur : Kristóf Milak (Hongrie) 47.08s',
 '2026-08-11 20:30:00', 1, 59, 'RESULTAT-EPC2-U59', true),
('RESULTAT', 'Résultats disponibles : 100m Nage Libre Hommes — Finale. Vainqueur : Kristóf Milak (Hongrie) 47.08s',
 '2026-08-11 20:30:00', 1, 60, 'RESULTAT-EPC2-U60', true),
('RESULTAT', 'Résultats disponibles : 100m Nage Libre Femmes — Finale. Vainqueure : Sarah Sjöström (Suède) 52.98s',
 '2026-08-11 21:00:00', 1, 59, 'RESULTAT-EPC4-U59', true),
('RESULTAT', 'Résultats disponibles : 100m Nage Libre Femmes — Finale. Vainqueure : Sarah Sjöström (Suède) 52.98s',
 '2026-08-11 21:00:00', 1, 61, 'RESULTAT-EPC4-U61', false),
('RESULTAT', 'Résultats disponibles : Natation Artistique Solo Femmes — Finale. Vainqueure : Sarah Sjöström 91.2pts',
 '2026-08-01 20:00:00', 1, 63, 'RESULTAT-EPC20-U63', true),
('RESULTAT', 'Résultats disponibles : 10km Eau Libre Hommes — Finale. Vainqueur : Florian Wellbrock 1h50m42',
 '2026-08-05 10:30:00', 1, 64, 'RESULTAT-EPC27-U64', false),
('RESULTAT', 'Résultats disponibles : 10km Eau Libre Femmes — Finale. Vainqueure : Sarah Sjöström 1h51m14',
 '2026-08-06 10:30:00', 1, 65, 'RESULTAT-EPC28-U65', true),
-- Notifications SECURITE
('SECURITE', 'Information sécurité : fausse alerte sécurité résolue. Accès rétabli côté ouest du CAOMGP.',
 '2026-08-06 16:05:00', 1, 66, 'SECU-INC3-U66', true),
('SECURITE', 'Information sécurité : conditions météo défavorables — épreuve eau libre 2026-08-09 reportée.',
 '2026-08-09 06:30:00', 1, 67, 'SECU-INC4-U67', false),
('SECURITE', 'Information sécurité : conditions météo défavorables — épreuve eau libre 2026-08-09 reportée.',
 '2026-08-09 06:30:00', 1, 68, 'SECU-INC4-U68', true),
-- Notifications EVENEMENT
('EVENEMENT', 'Annonce : les accréditations presse pour les Championnats d''Europe 2026 sont ouvertes.',
 '2026-05-01 09:00:00', 1, 69, 'EVENT-PRESS-U69', true),
('EVENEMENT', 'Annonce : programme complet des épreuves disponible sur l''application officielle.',
 '2026-06-15 10:00:00', 1, 70, 'EVENT-PROG-U70', true),
('EVENEMENT', 'Annonce : ouverture de la billetterie grand public le 1er juillet 2026.',
 '2026-06-01 08:00:00', 1, 71, 'EVENT-BILL-U71', false),
('EVENEMENT', 'Annonce : cérémonie d''ouverture des Championnats d''Europe le 31 juillet 2026 à 19h.',
 '2026-07-20 09:00:00', 1, 72, 'EVENT-OPEN-U72', true),
('EVENEMENT', 'Annonce : les fan zones officielles ouvrent le 28 juillet 2026.',
 '2026-07-25 10:00:00', 1, 73, 'EVENT-FANZ-U73', false);

-- ══════════════════════════════
-- 15. TICKETS
-- ══════════════════════════════
INSERT INTO ticket (category, base_price, spectator_id, epreuve_id) VALUES
-- Spectateurs 59-80 achètent des billets
('VIP',     150.0, 59, 2),  -- 100m NL H Finale
('TRIBUNE',  75.0, 59, 4),  -- 100m NL F Finale
('PELOUSE',  25.0, 60, 1),  -- 100m NL H Qual
('TRIBUNE',  75.0, 60, 2),  -- 100m NL H Finale
('VIP',     150.0, 61, 4),  -- 100m NL F Finale
('TRIBUNE',  75.0, 61, 20), -- Nat Art Solo F Finale
('PELOUSE',  25.0, 62, 11), -- WP H Phase groupes
('TRIBUNE',  75.0, 62, 13), -- WP H Quarts
('VIP',     150.0, 63, 20), -- Nat Art Solo F Finale
('PELOUSE',  25.0, 63, 19), -- Nat Art Solo F Qual
('TRIBUNE',  75.0, 64, 27), -- EL 10km H Finale
('VIP',     150.0, 64, 28), -- EL 10km F Finale
('PELOUSE',  25.0, 65, 23), -- Plongeon 3m H Qual
('TRIBUNE',  75.0, 65, 24), -- Plongeon 3m H Finale
('VIP',     150.0, 66, 2),  -- 100m NL H Finale
('PELOUSE',  25.0, 66, 3),  -- 100m NL F Qual
('TRIBUNE',  75.0, 67, 5),  -- 200m Pap H Qual
('PELOUSE',  25.0, 67, 7),  -- 100m Brasse F Qual
('VIP',     150.0, 68, 4),  -- 100m NL F Finale
('TRIBUNE',  75.0, 68, 12), -- WP F Phase groupes
('PELOUSE',  25.0, 69, 11), -- WP H Phase groupes
('VIP',     150.0, 69, 17), -- WP H Finale
('TRIBUNE',  75.0, 70, 2),  -- 100m NL H Finale
('PELOUSE',  25.0, 70, 27), -- EL 10km H Finale
('VIP',     150.0, 71, 20), -- Nat Art Solo F Finale
('TRIBUNE',  75.0, 72, 24), -- Plongeon 3m H Finale
('PELOUSE',  25.0, 73, 1),  -- 100m NL H Qual
('VIP',     150.0, 73, 4),  -- 100m NL F Finale
('TRIBUNE',  75.0, 74, 28), -- EL 10km F Finale
('PELOUSE',  25.0, 74, 5),  -- 200m Pap H Qual
('VIP',     150.0, 75, 2),  -- 100m NL H Finale
('TRIBUNE',  75.0, 76, 7),  -- 100m Brasse F Qual
('PELOUSE',  25.0, 77, 11), -- WP H Phase groupes
('VIP',     150.0, 78, 28), -- EL 10km F Finale
('TRIBUNE',  75.0, 79, 4),  -- 100m NL F Finale
('PELOUSE',  25.0, 80, 3),  -- 100m NL F Qual
('VIP',     150.0, 59, 28), -- EL 10km F Finale
('TRIBUNE',  75.0, 60, 20), -- Nat Art Solo F Finale
('PELOUSE',  25.0, 61, 23), -- Plongeon 3m H Qual
('VIP',     150.0, 62, 27), -- EL 10km H Finale
('TRIBUNE',  75.0, 63, 2),  -- 100m NL H Finale
('PELOUSE',  25.0, 64, 19), -- Nat Art Solo F Qual
('VIP',     150.0, 65, 4),  -- 100m NL F Finale
('TRIBUNE',  75.0, 66, 11), -- WP H Phase groupes
('PELOUSE',  25.0, 67, 12), -- WP F Phase groupes
('VIP',     150.0, 68, 20), -- Nat Art Solo F Finale
('TRIBUNE',  75.0, 69, 23), -- Plongeon 3m H Qual
('PELOUSE',  25.0, 70, 3),  -- 100m NL F Qual
('VIP',     150.0, 71, 27), -- EL 10km H Finale
('TRIBUNE',  75.0, 72, 28); -- EL 10km F Finale

-- ══════════════════════════════
-- 16. EVENT_LOG (~10 000+ lignes)
-- ══════════════════════════════
-- Généré via generate_series pour couvrir 2026-05-01 → 2026-08-16

-- Période préparation : 2026-05-01 → 2026-07-30 (~100-350/jour)
INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp, metadata)
SELECT
  -- user_id : alternance spectateurs (59-80) et admins/commissaires
  CASE (floor(random() * 10)::int)
    WHEN 0 THEN 1
    WHEN 1 THEN (42 + floor(random() * 5)::int)
    ELSE (59 + floor(random() * 22)::int)
  END AS user_id,
  -- user_role cohérent
  CASE (floor(random() * 10)::int)
    WHEN 0 THEN 'ADMIN'
    WHEN 1 THEN 'COMMISSAIRE'
    ELSE 'USER'
  END AS user_role,
  -- event_type
  CASE (floor(random() * 20)::int)
    WHEN 0  THEN 'USER_LOGIN'
    WHEN 1  THEN 'USER_LOGOUT'
    WHEN 2  THEN 'USER_REGISTER'
    WHEN 3  THEN 'COMPETITION_VIEW'
    WHEN 4  THEN 'COMPETITION_VIEW'
    WHEN 5  THEN 'COMPETITION_VIEW'
    WHEN 6  THEN 'EPREUVE_VIEW'
    WHEN 7  THEN 'EPREUVE_VIEW'
    WHEN 8  THEN 'EPREUVE_VIEW'
    WHEN 9  THEN 'EVENT_VIEW'
    WHEN 10 THEN 'EVENT_VIEW'
    WHEN 11 THEN 'ATHLETE_PROFILE_VIEW'
    WHEN 12 THEN 'NOTIFICATION_SUBSCRIBED'
    WHEN 13 THEN 'NOTIFICATION_SUBSCRIBED'
    WHEN 14 THEN 'PAGE_VIEW'
    WHEN 15 THEN 'PAGE_VIEW'
    WHEN 16 THEN 'PAGE_VIEW'
    WHEN 17 THEN 'PAGE_VIEW'
    WHEN 18 THEN 'RESULT_VIEW'
    ELSE         'NOTIFICATION_SENT'
  END AS event_type,
  -- endpoint
  CASE (floor(random() * 8)::int)
    WHEN 0 THEN '/api/competitions'
    WHEN 1 THEN '/api/epreuves'
    WHEN 2 THEN '/api/athletes'
    WHEN 3 THEN '/api/events'
    WHEN 4 THEN '/api/resultats'
    WHEN 5 THEN '/api/notifications'
    WHEN 6 THEN '/api/abonnements'
    ELSE        '/api/users/login'
  END AS endpoint,
  'GET' AS http_method,
  CASE (floor(random() * 20)::int)
    WHEN 0 THEN 404
    WHEN 1 THEN 500
    ELSE        200
  END AS status_code,
  (50 + floor(random() * 1950)::int) AS duration_ms,
  -- ip_address variées
  ('192.168.' || (floor(random() * 255)::int)::text || '.' || (floor(random() * 255)::int)::text) AS ip_address,
  -- timestamp : 2026-05-01 à 2026-07-30, heures 07:00-23:00 principalement
  (DATE '2026-05-01' + (floor(random() * 90)::int) * INTERVAL '1 day'
   + (7 + floor(random() * 16)::int) * INTERVAL '1 hour'
   + floor(random() * 60)::int * INTERVAL '1 minute') AS timestamp,
  -- metadata
  CASE (floor(random() * 5)::int)
    WHEN 0 THEN '{"type": "RESULTAT"}'
    WHEN 1 THEN '{"type": "SECURITE"}'
    WHEN 2 THEN '{"type": "EVENEMENT"}'
    ELSE        NULL
  END AS metadata
FROM generate_series(1, 4500) gs;

-- Période championnats : 2026-07-31 → 2026-08-16 (~500-1200/jour)
INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp, metadata)
SELECT
  CASE (floor(random() * 10)::int)
    WHEN 0 THEN 1
    WHEN 1 THEN (42 + floor(random() * 5)::int)
    WHEN 2 THEN (2 + floor(random() * 40)::int)
    WHEN 3 THEN (47 + floor(random() * 12)::int)
    ELSE        (59 + floor(random() * 22)::int)
  END AS user_id,
  CASE (floor(random() * 10)::int)
    WHEN 0 THEN 'ADMIN'
    WHEN 1 THEN 'COMMISSAIRE'
    WHEN 2 THEN 'ATHLETE'
    WHEN 3 THEN 'VOLONTAIRE'
    ELSE        'USER'
  END AS user_role,
  CASE (floor(random() * 25)::int)
    WHEN 0  THEN 'USER_LOGIN'
    WHEN 1  THEN 'USER_LOGOUT'
    WHEN 2  THEN 'COMPETITION_VIEW'
    WHEN 3  THEN 'COMPETITION_VIEW'
    WHEN 4  THEN 'COMPETITION_VIEW'
    WHEN 5  THEN 'COMPETITION_VIEW'
    WHEN 6  THEN 'EPREUVE_VIEW'
    WHEN 7  THEN 'EPREUVE_VIEW'
    WHEN 8  THEN 'EPREUVE_VIEW'
    WHEN 9  THEN 'RESULT_VIEW'
    WHEN 10 THEN 'RESULT_VIEW'
    WHEN 11 THEN 'RESULT_VIEW'
    WHEN 12 THEN 'RESULT_VIEW'
    WHEN 13 THEN 'ATHLETE_PROFILE_VIEW'
    WHEN 14 THEN 'ATHLETE_PROFILE_VIEW'
    WHEN 15 THEN 'EVENT_VIEW'
    WHEN 16 THEN 'NOTIFICATION_SENT'
    WHEN 17 THEN 'PAGE_VIEW'
    WHEN 18 THEN 'PAGE_VIEW'
    WHEN 19 THEN 'PAGE_VIEW'
    WHEN 20 THEN 'VOLUNTEER_VALIDATED'
    WHEN 21 THEN 'INCIDENT_DECLARED'
    WHEN 22 THEN 'NOTIFICATION_SUBSCRIBED'
    WHEN 23 THEN 'RESULT_VIEW'
    ELSE         'PAGE_VIEW'
  END AS event_type,
  CASE (floor(random() * 8)::int)
    WHEN 0 THEN '/api/competitions'
    WHEN 1 THEN '/api/epreuves'
    WHEN 2 THEN '/api/resultats'
    WHEN 3 THEN '/api/athletes'
    WHEN 4 THEN '/api/events'
    WHEN 5 THEN '/api/notifications/send'
    WHEN 6 THEN '/api/incidents'
    ELSE        '/api/users/login'
  END AS endpoint,
  'GET' AS http_method,
  CASE (floor(random() * 20)::int)
    WHEN 0 THEN 404
    WHEN 1 THEN 500
    ELSE        200
  END AS status_code,
  (50 + floor(random() * 1950)::int) AS duration_ms,
  ('10.' || (floor(random() * 255)::int)::text || '.' || (floor(random() * 255)::int)::text || '.' || (floor(random() * 255)::int)::text) AS ip_address,
  -- timestamp : 2026-07-31 à 2026-08-16, pics aux heures de compétition
  (DATE '2026-07-31' + (floor(random() * 17)::int) * INTERVAL '1 day'
   + CASE (floor(random() * 4)::int)
       WHEN 0 THEN (8  + floor(random() * 4)::int)  -- matin compétition
       WHEN 1 THEN (14 + floor(random() * 3)::int)  -- après-midi
       WHEN 2 THEN (18 + floor(random() * 3)::int)  -- soirée résultats
       ELSE        (9  + floor(random() * 14)::int)  -- reste journée
     END * INTERVAL '1 hour'
   + floor(random() * 60)::int * INTERVAL '1 minute') AS timestamp,
  CASE (floor(random() * 5)::int)
    WHEN 0 THEN '{"type": "RESULTAT"}'
    WHEN 1 THEN '{"type": "SECURITE"}'
    WHEN 2 THEN '{"type": "EVENEMENT"}'
    ELSE        NULL
  END AS metadata
FROM generate_series(1, 6500) gs;

-- Enregistrements USER_REGISTER (10 en mai)
INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
SELECT
  (59 + gs - 1) AS user_id,
  'USER' AS user_role,
  'USER_REGISTER' AS event_type,
  '/api/users/register' AS endpoint,
  'POST' AS http_method,
  200 AS status_code,
  (200 + floor(random() * 500)::int) AS duration_ms,
  ('172.16.' || gs::text || '.1') AS ip_address,
  (DATE '2026-05-01' + gs * INTERVAL '3 days') AS timestamp
FROM generate_series(1, 10) gs;

-- VOLUNTEER_VALIDATED (12 logs, 1 par volontaire, mars 2026)
INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp)
SELECT
  1 AS user_id,
  'ADMIN' AS user_role,
  'VOLUNTEER_VALIDATED' AS event_type,
  '/api/volunteers/validate' AS endpoint,
  'PUT' AS http_method,
  200 AS status_code,
  150 AS duration_ms,
  '192.168.1.100' AS ip_address,
  (DATE '2026-03-15' + (gs - 1) * INTERVAL '30 minutes') AS timestamp
FROM generate_series(1, 12) gs;

-- INCIDENT_DECLARED (5 logs, 1 par incident)
INSERT INTO event_log (user_id, user_role, event_type, endpoint, http_method, status_code, duration_ms, ip_address, timestamp, metadata) VALUES
(42, 'COMMISSAIRE', 'INCIDENT_DECLARED', '/api/incidents', 'POST', 200, 320, '10.0.1.42', '2026-08-02 10:15:00', '{"incident_type":"TECHNIQUE"}'),
(43, 'COMMISSAIRE', 'INCIDENT_DECLARED', '/api/incidents', 'POST', 200, 280, '10.0.1.43', '2026-08-04 09:30:00', '{"incident_type":"MEDICAL"}'),
(43, 'COMMISSAIRE', 'INCIDENT_DECLARED', '/api/incidents', 'POST', 200, 310, '10.0.1.43', '2026-08-06 14:20:00', '{"incident_type":"SECURITE"}'),
(46, 'COMMISSAIRE', 'INCIDENT_DECLARED', '/api/incidents', 'POST', 200, 295, '10.0.1.46', '2026-08-09 06:00:00', '{"incident_type":"METEO"}'),
(1,  'ADMIN',       'INCIDENT_DECLARED', '/api/incidents', 'POST', 200, 260, '192.168.1.100', '2026-08-11 08:45:00', '{"incident_type":"TECHNIQUE"}');

-- ══════════════════════════════
-- 17. DAILY_STATS
-- ══════════════════════════════
-- Couvre 2026-05-01 → 2026-08-16 (108 jours)
INSERT INTO daily_stats (
  stat_date, total_connections, unique_users,
  connections_athletes, connections_spectateurs, connections_commissaires,
  connections_volontaires, connections_admins,
  total_page_views, total_notifications_sent,
  notifications_resultats, notifications_securite, notifications_events,
  total_subscriptions, total_competition_views, total_result_views,
  avg_session_duration_ms, avg_response_time_ms, total_incidents, calculated_at
)
SELECT
  d AS stat_date,
  -- Connexions totales selon période
  CASE
    WHEN d >= DATE '2026-07-31' THEN
      CASE EXTRACT(DOW FROM d)
        WHEN 0 THEN 900 + floor(random() * 300)::int   -- dimanche
        WHEN 6 THEN 850 + floor(random() * 350)::int   -- samedi
        ELSE        650 + floor(random() * 400)::int   -- semaine
      END
    WHEN d >= DATE '2026-06-01' THEN
      CASE EXTRACT(DOW FROM d)
        WHEN 0 THEN 280 + floor(random() * 70)::int
        WHEN 6 THEN 250 + floor(random() * 100)::int
        ELSE        140 + floor(random() * 80)::int
      END
    ELSE  -- mai
      CASE EXTRACT(DOW FROM d)
        WHEN 0 THEN 220 + floor(random() * 80)::int
        WHEN 6 THEN 200 + floor(random() * 80)::int
        ELSE        110 + floor(random() * 90)::int
      END
  END AS total_connections,
  -- unique_users ≈ 70% du total
  CASE
    WHEN d >= DATE '2026-07-31' THEN 480 + floor(random() * 200)::int
    WHEN d >= DATE '2026-06-01' THEN  90 + floor(random() * 60)::int
    ELSE                               70 + floor(random() * 50)::int
  END AS unique_users,
  -- répartition par rôle
  CASE WHEN d >= DATE '2026-07-31' THEN 80 + floor(random() * 40)::int  ELSE 10 + floor(random() * 15)::int END AS connections_athletes,
  CASE WHEN d >= DATE '2026-07-31' THEN 380 + floor(random() * 200)::int ELSE 60 + floor(random() * 80)::int  END AS connections_spectateurs,
  CASE WHEN d >= DATE '2026-07-31' THEN 35 + floor(random() * 20)::int  ELSE 8  + floor(random() * 10)::int  END AS connections_commissaires,
  CASE WHEN d >= DATE '2026-07-31' THEN 45 + floor(random() * 25)::int  ELSE 10 + floor(random() * 15)::int  END AS connections_volontaires,
  CASE WHEN d >= DATE '2026-07-31' THEN 10 + floor(random() * 8)::int   ELSE 3  + floor(random() * 5)::int   END AS connections_admins,
  -- page views
  CASE WHEN d >= DATE '2026-07-31' THEN 2000 + floor(random() * 1500)::int ELSE 400 + floor(random() * 400)::int END AS total_page_views,
  -- notifications
  CASE WHEN d >= DATE '2026-07-31' AND EXTRACT(DOW FROM d) IN (0,6) THEN 80 + floor(random() * 40)::int
       WHEN d >= DATE '2026-07-31' THEN 40 + floor(random() * 30)::int
       ELSE 5 + floor(random() * 15)::int END AS total_notifications_sent,
  CASE WHEN d >= DATE '2026-07-31' THEN 20 + floor(random() * 30)::int ELSE 1 + floor(random() * 5)::int END AS notifications_resultats,
  CASE WHEN d >= DATE '2026-07-31' THEN 3  + floor(random() * 7)::int  ELSE 0 + floor(random() * 3)::int  END AS notifications_securite,
  CASE WHEN d >= DATE '2026-07-31' THEN 5  + floor(random() * 15)::int ELSE 2 + floor(random() * 8)::int  END AS notifications_events,
  -- abonnements (plus nombreux en phase préparation)
  CASE WHEN d BETWEEN DATE '2026-05-01' AND DATE '2026-07-30' THEN floor(random() * 3)::int ELSE 0 END AS total_subscriptions,
  -- vues compétition / résultats
  CASE WHEN d >= DATE '2026-07-31' THEN 300 + floor(random() * 300)::int ELSE 50 + floor(random() * 80)::int END AS total_competition_views,
  CASE WHEN d >= DATE '2026-07-31' THEN 200 + floor(random() * 400)::int ELSE 10 + floor(random() * 40)::int END AS total_result_views,
  -- sessions et temps de réponse
  (180000 + floor(random() * 120000)::int) AS avg_session_duration_ms,
  (120 + floor(random() * 200)::int) AS avg_response_time_ms,
  -- incidents (seulement pendant les championnats)
  CASE
    WHEN d = DATE '2026-08-02' THEN 1
    WHEN d = DATE '2026-08-04' THEN 1
    WHEN d = DATE '2026-08-06' THEN 1
    WHEN d = DATE '2026-08-09' THEN 1
    WHEN d = DATE '2026-08-11' THEN 1
    ELSE 0
  END AS total_incidents,
  (d + INTERVAL '23 hours 55 minutes') AS calculated_at
FROM generate_series(DATE '2026-05-01', DATE '2026-08-16', INTERVAL '1 day') d;

-- ══════════════════════════════
-- 18. WEEKLY_STATS
-- ══════════════════════════════
-- Agrégation des daily_stats par semaine ISO (lundi → dimanche)
INSERT INTO weekly_stats (
  week_start, week_end, total_connections, unique_users,
  peak_day, peak_connections, top_competition_id, top_competition_views,
  total_notifications_sent, total_new_subscriptions,
  avg_daily_connections, growth_rate_percent, calculated_at
)
WITH weekly_agg AS (
  SELECT
    date_trunc('week', stat_date)::date                    AS week_start,
    (date_trunc('week', stat_date) + INTERVAL '6 days')::date AS week_end,
    SUM(total_connections)                                 AS total_connections,
    MAX(unique_users)                                      AS unique_users,
    stat_date                                              AS peak_day_candidate,
    total_connections                                      AS day_connections,
    SUM(total_notifications_sent)                          AS total_notif,
    SUM(total_subscriptions)                               AS total_subs,
    ROUND(AVG(total_connections)::numeric, 1)              AS avg_daily,
    SUM(total_competition_views)                           AS comp_views
  FROM daily_stats
  GROUP BY date_trunc('week', stat_date), stat_date, total_connections
),
week_summary AS (
  SELECT
    week_start,
    week_end,
    SUM(total_connections) AS total_connections,
    MAX(unique_users)      AS unique_users,
    SUM(total_notif)       AS total_notifications_sent,
    SUM(total_subs)        AS total_new_subscriptions,
    ROUND(AVG(avg_daily)::numeric, 1) AS avg_daily_connections,
    SUM(comp_views)        AS total_competition_views
  FROM weekly_agg
  GROUP BY week_start, week_end
),
peak_days AS (
  SELECT DISTINCT ON (week_start)
    week_start,
    peak_day_candidate AS peak_day,
    day_connections    AS peak_connections
  FROM weekly_agg
  ORDER BY week_start, day_connections DESC
)
SELECT
  ws.week_start,
  ws.week_end,
  ws.total_connections,
  ws.unique_users,
  pd.peak_day,
  pd.peak_connections,
  5 AS top_competition_id,  -- NATATION toujours la plus populaire
  ws.total_competition_views,
  ws.total_notifications_sent,
  ws.total_new_subscriptions,
  ws.avg_daily_connections::double precision,
  CASE
    WHEN LAG(ws.total_connections) OVER (ORDER BY ws.week_start) IS NULL THEN 0.0
    ELSE ROUND(
      ((ws.total_connections - LAG(ws.total_connections) OVER (ORDER BY ws.week_start))::numeric
       / NULLIF(LAG(ws.total_connections) OVER (ORDER BY ws.week_start), 0) * 100), 1
    )
  END::double precision AS growth_rate_percent,
  (ws.week_end + INTERVAL '23 hours 55 minutes') AS calculated_at
FROM week_summary ws
JOIN peak_days pd ON ws.week_start = pd.week_start
ORDER BY ws.week_start;

-- ══════════════════════════════════════════════════════════════
-- REQUÊTE DE VÉRIFICATION FINALE
-- ══════════════════════════════════════════════════════════════
SELECT 'users'                       AS table_name, COUNT(*) AS total FROM users
UNION ALL SELECT 'athletes',                         COUNT(*) FROM athletes
UNION ALL SELECT 'equipes',                          COUNT(*) FROM equipes
UNION ALL SELECT 'event',                            COUNT(*) FROM event
UNION ALL SELECT 'competition',                      COUNT(*) FROM competition
UNION ALL SELECT 'epreuve',                          COUNT(*) FROM epreuve
UNION ALL SELECT 'epreuve_athlete_assignments',      COUNT(*) FROM epreuve_athlete_assignments
UNION ALL SELECT 'epreuve_athletes',                 COUNT(*) FROM epreuve_athletes
UNION ALL SELECT 'epreuve_equipes',                  COUNT(*) FROM epreuve_equipes
UNION ALL SELECT 'resultats',                        COUNT(*) FROM resultats
UNION ALL SELECT 'volunteers',                       COUNT(*) FROM volunteers
UNION ALL SELECT 'volunteer_tasks',                  COUNT(*) FROM volunteer_tasks
UNION ALL SELECT 'incident',                         COUNT(*) FROM incident
UNION ALL SELECT 'fan_zone',                         COUNT(*) FROM fan_zone
UNION ALL SELECT 'fan_zone_service',                 COUNT(*) FROM fan_zone_service
UNION ALL SELECT 'athlete_position',                 COUNT(*) FROM athlete_position
UNION ALL SELECT 'abonnements',                      COUNT(*) FROM abonnements
UNION ALL SELECT 'notification',                     COUNT(*) FROM notification
UNION ALL SELECT 'ticket',                           COUNT(*) FROM ticket
UNION ALL SELECT 'event_log',                        COUNT(*) FROM event_log
UNION ALL SELECT 'daily_stats',                      COUNT(*) FROM daily_stats
UNION ALL SELECT 'weekly_stats',                     COUNT(*) FROM weekly_stats
ORDER BY table_name;
