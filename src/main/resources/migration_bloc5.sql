-- Migration bloc 5 (dossier employé) pour une base sirh2 déjà créée.
-- Idempotent : peut être rejouée sans risque.

-- Bloc 5 (personne 1) : dossier complet de l'employe.
-- nom, prenom, telephone, email (personnel) et date_naissance restent dans
-- personne (employe.personne_id) : on ne les duplique pas.
ALTER TABLE employe ADD COLUMN IF NOT EXISTS lieu_naissance VARCHAR(100);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS sexe VARCHAR(10);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS nationalite VARCHAR(60);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS cin_numero VARCHAR(30);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS cin_date_delivrance DATE;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS cin_lieu_delivrance VARCHAR(100);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS adresse VARCHAR(255);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS commune VARCHAR(100);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS region VARCHAR(100);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS situation_matrimoniale VARCHAR(30);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS nombre_enfants INT NOT NULL DEFAULT 0;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS contact_urgence_nom VARCHAR(150);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS contact_urgence_relation VARCHAR(60);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS contact_urgence_telephone VARCHAR(30);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS departement_id INT;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS poste_id INT;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS superieur_id INT;
ALTER TABLE employe ADD COLUMN IF NOT EXISTS salaire_actuel NUMERIC(14,2);
ALTER TABLE employe ADD COLUMN IF NOT EXISTS photo VARCHAR(255);

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_employe_departement') THEN
        ALTER TABLE employe ADD CONSTRAINT fk_employe_departement
            FOREIGN KEY (departement_id) REFERENCES departement(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_employe_poste') THEN
        ALTER TABLE employe ADD CONSTRAINT fk_employe_poste
            FOREIGN KEY (poste_id) REFERENCES poste(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_employe_superieur') THEN
        ALTER TABLE employe ADD CONSTRAINT fk_employe_superieur
            FOREIGN KEY (superieur_id) REFERENCES employe(id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'ck_employe_superieur') THEN
        ALTER TABLE employe ADD CONSTRAINT ck_employe_superieur
            CHECK (superieur_id IS NULL OR superieur_id <> id);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS type_document (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS document_employe (
    id SERIAL PRIMARY KEY,
    employe_id INT NOT NULL,
    type_document_id INT NOT NULL,
    nom_fichier VARCHAR(255) NOT NULL,
    chemin_fichier VARCHAR(500) NOT NULL,
    date_ajout TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_expiration DATE,
    utilisateur_id INT,

    CONSTRAINT fk_document_employe_employe
        FOREIGN KEY (employe_id) REFERENCES employe(id) ON DELETE CASCADE,
    CONSTRAINT fk_document_employe_type
        FOREIGN KEY (type_document_id) REFERENCES type_document(id),
    CONSTRAINT fk_document_employe_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

CREATE INDEX IF NOT EXISTS idx_document_employe_employe ON document_employe(employe_id);

INSERT INTO type_document (code, libelle) VALUES
    ('CV', 'Curriculum vitae'),
    ('CIN', 'Carte d''identité nationale'),
    ('DIPLOME', 'Diplôme'),
    ('CONTRAT', 'Contrat'),
    ('AUTRE', 'Autre pièce')
ON CONFLICT (code) DO NOTHING;

-- Aligne les dossiers existants sur leur contrat en cours.
UPDATE employe e SET
    poste_id = c.poste_id,
    departement_id = p.departement_id,
    salaire_actuel = c.salaire_mensuel
FROM contrat c JOIN poste p ON p.id = c.poste_id
WHERE c.employe_id = e.id AND c.statut = 'EN_COURS' AND e.poste_id IS NULL;
