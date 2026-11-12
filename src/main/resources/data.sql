-- SIRH - data.sql
\set ON_ERROR_STOP on
-- Pour charger toute la demonstration, lancer setup-demo.sql depuis postgres.
-- Donnees de reference et comptes de test.
-- A executer apres schema.sql dans la base sirh2.
--
-- Les seuls utilisateurs conserves sont les comptes de test de l application.

BEGIN;

INSERT INTO role (id, nom, description) VALUES
    (1, 'MANAGER', 'Exprime et evalue les besoins de son service'),
    (2, 'RH', 'Gere les demandes de recrutement et les candidatures'),
    (3, 'FINANCIER', 'Verifie la disponibilite et la conformite du budget'),
    (4, 'DIRECTEUR', 'Prend les decisions finales de validation'),
    (5, 'CANDIDAT', 'Consulte les offres et postule'),
    (6, 'EMPLOYE', 'Accede aux fonctionnalites internes');

INSERT INTO type_document (id, code, libelle) VALUES
    (1, 'CIN', 'Carte d identite nationale'),
    (2, 'DIPLOME', 'Diplome'),
    (3, 'CV', 'Curriculum vitae'),
    (4, 'CERTIFICAT', 'Certificat'),
    (5, 'AUTRE', 'Autre document');

INSERT INTO statut (id, libelle, description) VALUES
    (1, 'BROUILLON', 'Demande en cours de preparation'),
    (2, 'SOUMISE', 'Demande transmise pour verification'),
    (3, 'EN_VERIFICATION_FINANCIERE', 'Verification budgetaire en cours'),
    (4, 'BUDGET_INSUFFISANT', 'Le budget disponible ne suffit pas'),
    (5, 'EN_ATTENTE_VALIDATION', 'En attente de la decision finale'),
    (6, 'VALIDEE', 'Demande validee'),
    (7, 'REFUSEE', 'Demande refusee'),
    (8, 'ANNULEE', 'Demande annulee');

INSERT INTO departement (id, nom) VALUES
    (1, 'Informatique'),
    (2, 'Ressources humaines'),
    (3, 'Finance'),
    (4, 'Direction'),
    (5, 'Marketing');

INSERT INTO personne (id, nom, prenom, email, telephone, ville) VALUES
    (1, 'Manager', 'Andry', 'andry.manager@sirh.test', NULL, NULL),
    (2, 'Manager', 'Miora', 'miora.manager@sirh.test', NULL, NULL),
    (3, 'RH', 'Hery', 'hery.rh@sirh.test', NULL, NULL),
    (4, 'Finance', 'Fara', 'fara.finance@sirh.test', NULL, NULL),
    (5, 'Direction', 'Solo', 'solo.direction@sirh.test', NULL, NULL);

-- Budget annuel disponible par departement.
-- Ce montant est compare individuellement au cout de chaque demande :
-- budget unitaire x nombre de personnes.
-- Il n est pas diminue et n est pas cumule avec les autres demandes.
INSERT INTO budget_entreprise (id, departement_id, annee, montant_disponible) VALUES
    (1, 1, 2026, 3000000),
    (2, 2, 2026, 2200000),
    (3, 3, 2026, 2600000),
    (4, 4, 2026, 2000000),
    (5, 5, 2026, 2500000);

INSERT INTO poste (id, intitule, departement_id, description) VALUES
    (1, 'Developpeur', 1, 'Developpement des applications et services internes'),
    (2, 'Comptable', 3, 'Suivi comptable et financier'),
    (3, 'Responsable marketing', 5, 'Pilotage des actions marketing'),
    (4, 'Technicien reseau', 1, 'Administration et support reseau'),
    (5, 'Charge de recrutement', 2, 'Gestion des recrutements');

-- Comptes de test conserves apres chaque reinitialisation.
-- Les mots de passe correspondent aux valeurs deja utilisees par le projet.
INSERT INTO utilisateur (id, personne_id, mot_de_passe, role_id, departement_id) VALUES
    (1, 1, 'hash_test_1', 1, 1),
    (2, 2, 'hash_test_2', 1, 5),
    (3, 3, 'hash_test_3', 2, 2),
    (4, 4, 'hash_test_4', 3, 3),
    (5, 5, 'hash_test_5', 4, 4);

INSERT INTO regle_budgetaire (id, poste_id, budget_min, budget_max) VALUES
    (1, 1, 200000, 400000),
    (2, 2, 250000, 500000),
    (3, 3, 300000, 600000),
    (4, 4, 180000, 350000),
    (5, 5, 220000, 420000);

INSERT INTO critere_candidature
    (id, poste_id, libelle, type, options, obligatoire, ordre)
VALUES
    (1, 1, 'Competences techniques', 'CASES', 'Java;Spring Boot;React;SQL;Git', TRUE, 1),
    (2, 1, 'Annees d experience', 'LISTE', 'Moins de 1 an;1 a 2 ans;3 a 5 ans;Plus de 5 ans', TRUE, 2),
    (3, 1, 'Niveau d etudes', 'LISTE', 'Bac+2;Bac+3;Bac+5', TRUE, 3),
    (4, 1, 'Disponibilite', 'LISTE', 'Immediate;Sous 1 mois;Sous 3 mois', TRUE, 4),
    (5, 1, 'Lien vers votre portfolio', 'TEXTE', NULL, FALSE, 5),
    (6, 2, 'Logiciels maitrises', 'CASES', 'Excel;Sage;SAP;Odoo', TRUE, 1),
    (7, 2, 'Annees d experience en comptabilite', 'LISTE', 'Moins de 1 an;1 a 3 ans;3 a 5 ans;Plus de 5 ans', TRUE, 2),
    (8, 2, 'Diplome', 'LISTE', 'BTS;Bac+3 (Licence);Bac+5 (Master);Expertise comptable', TRUE, 3),
    (9, 2, 'Acceptez-vous un poste a temps plein ?', 'OUI_NON', NULL, TRUE, 4),
    (10, 3, 'Domaines d expertise', 'CASES', 'Marketing digital;Reseaux sociaux;Evenementiel;Etudes de marche', TRUE, 1),
    (11, 3, 'Annees d experience', 'NOMBRE', NULL, TRUE, 2),
    (12, 3, 'Logiciels maitrises', 'CASES', 'Canva;Google Analytics;Meta Ads;Adobe Suite', FALSE, 3),
    (13, 3, 'Avez-vous deja encadre une equipe ?', 'OUI_NON', NULL, TRUE, 4),
    (14, 4, 'Technologies maitrisees', 'CASES', 'TCP/IP;VLAN;VPN;Firewall', TRUE, 1),
    (15, 4, 'Certifications', 'CASES', 'CCNA;CompTIA Network+;Aucune', FALSE, 2),
    (16, 4, 'Annees d experience', 'NOMBRE', NULL, TRUE, 3),
    (17, 4, 'Disponible pour des interventions hors heures ouvrables ?', 'OUI_NON', NULL, TRUE, 4),
    (18, 5, 'Etapes du recrutement maitrisees', 'CASES', 'Redaction d annonces;Tri de CV;Entretiens;Onboarding', TRUE, 1),
    (19, 5, 'Annees d experience en RH', 'LISTE', 'Moins de 1 an;1 a 3 ans;3 a 5 ans;Plus de 5 ans', TRUE, 2),
    (20, 5, 'Langues parlees', 'CASES', 'Francais;Anglais;Malagasy', TRUE, 3),
    (21, 5, 'Avez-vous un diplome en ressources humaines ?', 'OUI_NON', NULL, TRUE, 4);

SELECT setval(pg_get_serial_sequence('role', 'id'), 6, true);
SELECT setval(pg_get_serial_sequence('statut', 'id'), 8, true);
SELECT setval(pg_get_serial_sequence('departement', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('personne', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('poste', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('utilisateur', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('type_document', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('regle_budgetaire', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('budget_entreprise', 'id'), 5, true);
SELECT setval(pg_get_serial_sequence('critere_candidature', 'id'), 21, true);

-- BLOC 5 - referentiels necessaires au journal d audit.
INSERT INTO type_action (code, libelle) VALUES
    ('INSERT', 'Creation'),
    ('UPDATE', 'Modification'),
    ('DELETE', 'Suppression'),
    ('CONSULTATION', 'Consultation')
ON CONFLICT (code) DO NOTHING;

INSERT INTO type_entite (code, libelle) VALUES
    ('EMPLOYE', 'Employe'),
    ('CONTRAT', 'Contrat'),
    ('FICHE_PAIE', 'Fiche de paie'),
    ('ABSENCE', 'Absence'),
    ('DEMANDE_RECRUTEMENT', 'Demande de recrutement'),
    ('JOUR_TRAVAIL', 'Jour de travail'),
    ('DETAIL_JOUR_TRAVAIL', 'Détail de jour de travail'),
    ('HEURE_SUPPLEMENTAIRE', 'Heure supplémentaire'),
    ('PROPOSITION_EMBAUCHE', 'Proposition d’embauche'),
    ('DOCUMENT', 'Document'),
    ('CONGE', 'Conge'),
    ('MISSION', 'Mission')
ON CONFLICT (code) DO NOTHING;

COMMIT;
