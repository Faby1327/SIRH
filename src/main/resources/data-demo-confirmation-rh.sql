-- Démonstration : proposition acceptée par le candidat,
\set ON_ERROR_STOP on
-- Pour charger toute la démonstration, lancer setup-demo.sql depuis postgres.
-- en attente de confirmation RH.
--
-- À exécuter après : schema.sql, data.sql et data-bloc6.sql
-- Exemple :
--   psql -h localhost -U postgres -d sirh2 -f data-demo-confirmation-rh.sql
--
-- Compte candidat :
--   email    : candidat.demo@sirh.test
--   mot de passe : hash_demo_candidat
--
-- Après exécution, se connecter en RH avec le compte habituel :
--   hery.rh@sirh.test / hash_test_3
-- puis ouvrir la liste des embauches et confirmer la proposition.
-- Le contrat ne sera créé qu'après la confirmation prévue par l'application.

BEGIN;

-- Identité et compte candidat de démonstration.
INSERT INTO personne (id, nom, prenom, email, telephone, ville)
VALUES (7, 'Demo', 'Candidat', 'candidat.demo@sirh.test', '0340000001', 'Antananarivo')
ON CONFLICT (id) DO NOTHING;

-- Ancien contrat déjà en cours pour ce candidat devenu employé.
-- Lors de la création du nouveau contrat après confirmation RH,
-- celui-ci sera automatiquement terminé.
INSERT INTO employe (
    id, personne_id, matricule, date_embauche, statut,
    departement_id, poste_id, salaire_actuel
)
VALUES (
    2,
    7,
    'EMP-00002',
    DATE '2026-01-01',
    'ACTIF',
    1,
    1,
    350000
)
ON CONFLICT (id) DO NOTHING;

-- Affectation du remplaçant sur les jours ouvrés habituels.
INSERT INTO affectation_horaire (
    id, employe_id, regime_travail_id, modele_horaire_id, date_debut, date_fin, actif
)
VALUES (2, 2, 1, 1, DATE '2026-01-01', NULL, TRUE)
ON CONFLICT (id) DO NOTHING;
INSERT INTO affectation_horaire_jour (affectation_horaire_id, jour_semaine)
VALUES (2, 1), (2, 2), (2, 3), (2, 4), (2, 5)
ON CONFLICT DO NOTHING;

INSERT INTO contrat (
    id, employe_id, proposition_id, poste_id,
    type_contrat, date_debut, date_fin, salaire_mensuel, devise, statut
)
VALUES (
    2,
    2,
    NULL,
    1,
    'CDD',
    DATE '2026-01-01',
    NULL,
    350000,
    'Ar',
    'EN_COURS'
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO utilisateur (id, personne_id, mot_de_passe, role_id, departement_id)
VALUES (7, 7, 'hash_demo_candidat', 5, NULL)
ON CONFLICT (id) DO NOTHING;

-- Second employé de démonstration, disponible pour les affectations de mission.
INSERT INTO personne (id, nom, prenom, email, telephone, ville)
VALUES (8, 'Test', 'Employe', 'employe.test2@sirh.test', '0340000002', 'Antananarivo')
ON CONFLICT (id) DO NOTHING;

INSERT INTO utilisateur (id, personne_id, mot_de_passe, role_id, departement_id)
VALUES (8, 8, 'hash_test_8', 6, NULL)
ON CONFLICT (id) DO NOTHING;

INSERT INTO employe (id, personne_id, matricule, date_embauche, statut, departement_id, poste_id, salaire_actuel)
VALUES (3, 8, 'EMP-00003', DATE '2026-01-01', 'ACTIF', 1, 1, 350000)
ON CONFLICT (id) DO NOTHING;

INSERT INTO affectation_horaire (
    id, employe_id, regime_travail_id, modele_horaire_id, date_debut, date_fin, actif
)
VALUES (3, 3, 1, 1, DATE '2026-01-01', NULL, TRUE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO affectation_horaire_jour (affectation_horaire_id, jour_semaine)
VALUES (3, 1), (3, 2), (3, 3), (3, 4), (3, 5)
ON CONFLICT DO NOTHING;

INSERT INTO candidat (id, personne_id, date_creation)
VALUES (1, 7, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- Demande déjà validée par le circuit de recrutement.
INSERT INTO demande_recrutement (
    id, motif, nombre_personnes, type_recrutement, date_demande,
    date_souhaitee, salaire_mensuel_estime, cout_total_estime,
    poste_id, demandeur_id, statut_id
)
VALUES (
    90,
    'Demande de démonstration pour tester la confirmation RH',
    1,
    'CREATION',
    CURRENT_DATE,
    CURRENT_DATE + 30,
    350000,
    350000,
    1,
    1,
    6
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO offre_emploi (id, demande_id, titre, description, date_publication, statut)
VALUES (
    90,
    90,
    'Développeur - Démonstration audit',
    'Offre préparée pour tester la confirmation RH et la création du contrat.',
    CURRENT_TIMESTAMP - INTERVAL '3 days',
    'EN_ATTENTE_CONFIRMATION'
)
ON CONFLICT (id) DO NOTHING;

-- Candidature déjà retenue.
INSERT INTO candidature (
    id, offre_id, candidat_id, nom, prenom, email, telephone,
    message, date_candidature, statut, date_preselection,
    preselectionne_par, commentaire_preselection
)
VALUES (
    90,
    90,
    1,
    'Demo',
    'Candidat',
    'candidat.demo@sirh.test',
    '0340000001',
    'Candidature de démonstration.',
    CURRENT_TIMESTAMP - INTERVAL '2 days',
    'RETENUE',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    3,
    'Candidat retenu pour le scénario de démonstration.'
)
ON CONFLICT (id) DO NOTHING;

-- Proposition déjà acceptée par le candidat.
-- Le RH doit encore la confirmer.
INSERT INTO proposition_embauche (
    id, candidature_id, offre_id, propose_par, statut,
    date_proposition, date_reponse, commentaire_rh,
    commentaire_candidat, salaire_propose, salaire_demande_candidat,
    type_contrat_propose, type_contrat_demande_candidat,
    salaire_final, date_acceptation_candidat
)
VALUES (
    90,
    90,
    90,
    3,
    'EN_ATTENTE_CONFIRMATION',
    CURRENT_TIMESTAMP - INTERVAL '1 day',
    CURRENT_TIMESTAMP - INTERVAL '2 hours',
    'Proposition de démonstration.',
    'J''accepte la proposition.',
    350000,
    NULL,
    'CE',
    NULL,
    350000,
    CURRENT_TIMESTAMP - INTERVAL '2 hours'
)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('personne', 'id'), GREATEST(8, COALESCE(MAX(id), 1)), true)
FROM personne;
SELECT setval(pg_get_serial_sequence('utilisateur', 'id'), GREATEST(8, COALESCE(MAX(id), 1)), true)
FROM utilisateur;
SELECT setval(pg_get_serial_sequence('candidat', 'id'), GREATEST(1, COALESCE(MAX(id), 1)), true)
FROM candidat;
SELECT setval(pg_get_serial_sequence('employe', 'id'), GREATEST(3, COALESCE(MAX(id), 1)), true)
FROM employe;
SELECT setval(pg_get_serial_sequence('contrat', 'id'), GREATEST(2, COALESCE(MAX(id), 1)), true)
FROM contrat;
SELECT setval(pg_get_serial_sequence('demande_recrutement', 'id'), GREATEST(90, COALESCE(MAX(id), 1)), true)
FROM demande_recrutement;
SELECT setval(pg_get_serial_sequence('offre_emploi', 'id'), GREATEST(90, COALESCE(MAX(id), 1)), true)
FROM offre_emploi;
SELECT setval(pg_get_serial_sequence('candidature', 'id'), GREATEST(90, COALESCE(MAX(id), 1)), true)
FROM candidature;
SELECT setval(pg_get_serial_sequence('proposition_embauche', 'id'), GREATEST(90, COALESCE(MAX(id), 1)), true)
FROM proposition_embauche;

SELECT setval(
    pg_get_serial_sequence('affectation_horaire', 'id'),
    GREATEST(3, COALESCE(MAX(id), 1)),
    true
)
FROM affectation_horaire;

COMMIT;
