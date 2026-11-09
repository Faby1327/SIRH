-- SIRH - data.sql
-- Données de référence et comptes de test.
-- À exécuter après schema.sql dans la base sirh2.
--
-- Les seuls utilisateurs conservés sont les comptes de test de l'application.

BEGIN;

INSERT INTO role (id, nom, description) VALUES
    (1, 'MANAGER', 'Exprime et évalue les besoins de son service'),
    (2, 'RH', 'Gère les demandes de recrutement et les candidatures'),
    (3, 'FINANCIER', 'Vérifie la disponibilité et la conformité du budget'),
    (4, 'DIRECTEUR', 'Prend les décisions finales de validation'),
    (5, 'CANDIDAT', 'Consulte les offres et postule'),
    (6, 'EMPLOYE', 'Accède aux fonctionnalités internes');

INSERT INTO type_document (id, code, libelle) VALUES
    (1, 'CIN', 'Carte d''identité nationale'),
    (2, 'DIPLOME', 'Diplôme'),
    (3, 'CV', 'Curriculum vitae'),
    (4, 'CERTIFICAT', 'Certificat'),
    (5, 'AUTRE', 'Autre document');

INSERT INTO statut (id, libelle, description) VALUES
    (1, 'BROUILLON', 'Demande en cours de préparation'),
    (2, 'SOUMISE', 'Demande transmise pour vérification'),
    (3, 'EN_VERIFICATION_FINANCIERE', 'Vérification budgétaire en cours'),
    (4, 'BUDGET_INSUFFISANT', 'Le budget disponible ne suffit pas'),
    (5, 'EN_ATTENTE_VALIDATION', 'En attente de la décision finale'),
    (6, 'VALIDEE', 'Demande validée'),
    (7, 'REFUSEE', 'Demande refusée'),
    (8, 'ANNULEE', 'Demande annulée');

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

-- Budget annuel disponible par département.
-- Ce montant est comparé individuellement au coût de chaque demande :
-- budget unitaire × nombre de personnes.
-- Il n'est pas diminué et n'est pas cumulé avec les autres demandes.
INSERT INTO budget_entreprise (id, departement_id, annee, montant_disponible) VALUES
    (1, 1, 2026, 3000000),
    (2, 2, 2026, 2200000),
    (3, 3, 2026, 2600000),
    (4, 4, 2026, 2000000),
    (5, 5, 2026, 2500000);

INSERT INTO poste (id, intitule, departement_id, description) VALUES
    (1, 'Développeur', 1, 'Développement des applications et services internes'),
    (2, 'Comptable', 3, 'Suivi comptable et financier'),
    (3, 'Responsable marketing', 5, 'Pilotage des actions marketing'),
    (4, 'Technicien réseau', 1, 'Administration et support réseau'),
    (5, 'Chargé de recrutement', 2, 'Gestion des recrutements');

-- Comptes de test conservés après chaque réinitialisation.
-- Les mots de passe correspondent aux valeurs déjà utilisées par le projet.
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
    (1, 1, 'Compétences techniques', 'CASES', 'Java;Spring Boot;React;SQL;Git', TRUE, 1),
    (2, 1, 'Années d''expérience', 'LISTE', 'Moins de 1 an;1 à 2 ans;3 à 5 ans;Plus de 5 ans', TRUE, 2),
    (3, 1, 'Niveau d''études', 'LISTE', 'Bac+2;Bac+3;Bac+5', TRUE, 3),
    (4, 1, 'Disponibilité', 'LISTE', 'Immédiate;Sous 1 mois;Sous 3 mois', TRUE, 4),
    (5, 1, 'Lien vers votre portfolio', 'TEXTE', NULL, FALSE, 5),
    (6, 2, 'Logiciels maîtrisés', 'CASES', 'Excel;Sage;SAP;Odoo', TRUE, 1),
    (7, 2, 'Années d''expérience en comptabilité', 'LISTE', 'Moins de 1 an;1 à 3 ans;3 à 5 ans;Plus de 5 ans', TRUE, 2),
    (8, 2, 'Diplôme', 'LISTE', 'BTS;Bac+3 (Licence);Bac+5 (Master);Expertise comptable', TRUE, 3),
    (9, 2, 'Acceptez-vous un poste à temps plein ?', 'OUI_NON', NULL, TRUE, 4),
    (10, 3, 'Domaines d''expertise', 'CASES', 'Marketing digital;Réseaux sociaux;Événementiel;Études de marché', TRUE, 1),
    (11, 3, 'Années d''expérience', 'NOMBRE', NULL, TRUE, 2),
    (12, 3, 'Logiciels maîtrisés', 'CASES', 'Canva;Google Analytics;Meta Ads;Adobe Suite', FALSE, 3),
    (13, 3, 'Avez-vous déjà encadré une équipe ?', 'OUI_NON', NULL, TRUE, 4),
    (14, 4, 'Technologies maîtrisées', 'CASES', 'TCP/IP;VLAN;VPN;Firewall', TRUE, 1),
    (15, 4, 'Certifications', 'CASES', 'CCNA;CompTIA Network+;Aucune', FALSE, 2),
    (16, 4, 'Années d''expérience', 'NOMBRE', NULL, TRUE, 3),
    (17, 4, 'Disponible pour des interventions hors heures ouvrables ?', 'OUI_NON', NULL, TRUE, 4),
    (18, 5, 'Étapes du recrutement maîtrisées', 'CASES', 'Rédaction d''annonces;Tri de CV;Entretiens;Onboarding', TRUE, 1),
    (19, 5, 'Années d''expérience en RH', 'LISTE', 'Moins de 1 an;1 à 3 ans;3 à 5 ans;Plus de 5 ans', TRUE, 2),
    (20, 5, 'Langues parlées', 'CASES', 'Français;Anglais;Malagasy', TRUE, 3),
    (21, 5, 'Avez-vous un diplôme en ressources humaines ?', 'OUI_NON', NULL, TRUE, 4);

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

-- BLOC 5 - référentiels nécessaires au journal d'audit.
INSERT INTO type_action (code, libelle) VALUES
    ('INSERT', 'Création'),
    ('UPDATE', 'Modification'),
    ('DELETE', 'Suppression'),
    ('CONSULTATION', 'Consultation')
ON CONFLICT (code) DO NOTHING;

INSERT INTO type_entite (code, libelle) VALUES
    ('EMPLOYE', 'Employé'),
    ('CONTRAT', 'Contrat'),
    ('FICHE_PAIE', 'Fiche de paie'),
    ('DOCUMENT', 'Document'),
    ('CONGE', 'Congé'),
    ('MISSION', 'Mission')
ON CONFLICT (code) DO NOTHING;

COMMIT;
