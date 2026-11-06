-- SIRH - data-bloc6.sql
-- Données de démonstration du bloc 6 : affectations horaires.
--
-- À exécuter après schema.sql puis data.sql :
--   psql -h localhost -U postgres -d sirh2 -f data-bloc6.sql
--
-- Le premier écran développé sera /rh/planning/affectations.
-- Les régimes et modèles horaires sont les données de référence nécessaires
-- à la création d'une affectation.

BEGIN;

INSERT INTO regime_travail (
    id, nom, description, heures_hebdomadaires,
    jours_travailles_par_semaine, actif
)
VALUES
    (
        1,
        'Temps plein',
        'Régime standard à 40 heures par semaine, du lundi au vendredi.',
        40,
        5,
        TRUE
    ),
    (
        2,
        'Temps partiel',
        'Régime à 20 heures par semaine.',
        20,
        5,
        TRUE
    ),
    (
        3,
        'Travail en équipe',
        'Régime adapté aux horaires d''équipe et aux rotations.',
        40,
        5,
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

INSERT INTO modele_horaire (
    id, nom, heure_debut, heure_fin,
    pause_prevue_minutes, tolerance_retard_minutes, actif
)
VALUES
    (
        1,
        'Horaire normal',
        '08:00',
        '17:00',
        60,
        10,
        TRUE
    ),
    (
        2,
        'Horaire matin',
        '06:00',
        '14:00',
        30,
        10,
        TRUE
    ),
    (
        3,
        'Horaire après-midi',
        '14:00',
        '22:00',
        30,
        10,
        TRUE
    ),
    (
        4,
        'Temps partiel matin',
        '08:00',
        '12:00',
        0,
        10,
        TRUE
    )
ON CONFLICT (id) DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('regime_travail', 'id'),
    GREATEST(3, COALESCE(MAX(id), 1)),
    true
)
FROM regime_travail;

SELECT setval(
    pg_get_serial_sequence('modele_horaire', 'id'),
    GREATEST(4, COALESCE(MAX(id), 1)),
    true
)
FROM modele_horaire;

-- Employé de démonstration utilisé par l'écran des affectations.
INSERT INTO personne (
    id, nom, prenom, email, telephone, ville
)
VALUES (
    6,
    'Demo',
    'Employe',
    'employe.demo@sirh.test',
    '0340000000',
    'Antananarivo'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO employe (
    id, personne_id, matricule, date_embauche, statut
)
VALUES (
    1,
    6,
    'EMP-00001',
    DATE '2026-01-01',
    'ACTIF'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO contrat (
    id, employe_id, proposition_id, poste_id,
    type_contrat, date_debut, salaire_mensuel, devise, statut
)
VALUES (
    1,
    1,
    NULL,
    1,
    'CDI',
    DATE '2026-01-01',
    350000,
    'Ar',
    'EN_COURS'
)
ON CONFLICT (id) DO NOTHING;

-- Affectation de l'horaire normal à l'employé de démonstration.
INSERT INTO affectation_horaire (
    id, employe_id, regime_travail_id, modele_horaire_id,
    date_debut, date_fin, actif
)
VALUES (
    1,
    1,
    1,
    1,
    DATE '2026-01-01',
    NULL,
    TRUE
)
ON CONFLICT (id) DO NOTHING;

-- Du lundi au vendredi.
INSERT INTO affectation_horaire_jour (affectation_horaire_id, jour_semaine)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5)
ON CONFLICT DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('personne', 'id'),
    GREATEST(6, COALESCE(MAX(id), 1)),
    true
)
FROM personne;

SELECT setval(
    pg_get_serial_sequence('employe', 'id'),
    GREATEST(1, COALESCE(MAX(id), 1)),
    true
)
FROM employe;

SELECT setval(
    pg_get_serial_sequence('contrat', 'id'),
    GREATEST(1, COALESCE(MAX(id), 1)),
    true
)
FROM contrat;

SELECT setval(
    pg_get_serial_sequence('affectation_horaire', 'id'),
    GREATEST(1, COALESCE(MAX(id), 1)),
    true
)
FROM affectation_horaire;

COMMIT;
