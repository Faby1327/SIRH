-- Bloc 7 : appliquer sur une base existante, après sauvegarde.
CREATE TABLE IF NOT EXISTS solde_conge (
    id BIGSERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employe(id),
    date_acquisition DATE NOT NULL,
    jours_initialement_acquis INTEGER NOT NULL CHECK (jours_initialement_acquis > 0),
    jours_restants INTEGER NOT NULL CHECK (jours_restants >= 0),
    CONSTRAINT ck_solde_conge_restant CHECK (jours_restants <= jours_initialement_acquis)
);
CREATE INDEX IF NOT EXISTS ix_solde_conge_fifo ON solde_conge(employe_id,date_acquisition,id);

-- Soldes de démonstration variés pour les employés de test du département Informatique.
INSERT INTO solde_conge (employe_id, date_acquisition, jours_initialement_acquis, jours_restants)
SELECT e.id, CURRENT_DATE, 30, v.jours_restants
FROM (VALUES (1, 18), (2, 25), (3, 7), (4, 0), (5, 12)) AS v(employe_id, jours_restants)
JOIN employe e ON e.id = v.employe_id
WHERE NOT EXISTS (
    SELECT 1 FROM solde_conge s
    WHERE s.employe_id = e.id AND s.date_acquisition = CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS demande_conge (
    id BIGSERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employe(id),
    demandeur_id BIGINT NOT NULL REFERENCES utilisateur(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    jours INTEGER NOT NULL CHECK (jours > 0),
    motif TEXT,
    statut VARCHAR(30) NOT NULL CHECK (statut IN ('EN_ATTENTE_MANAGER','EN_ATTENTE_RH','REFUSE_MANAGER','REFUSE_RH','VALIDEE')),
    commentaire_manager TEXT,
    commentaire_rh TEXT,
    remplacant_id BIGINT REFERENCES employe(id),
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_decision_manager TIMESTAMP,
    date_decision_rh TIMESTAMP,
    CHECK (date_fin >= date_debut)
);
CREATE INDEX IF NOT EXISTS ix_demande_conge_employe ON demande_conge(employe_id,date_creation DESC);

CREATE TABLE IF NOT EXISTS historique_demande_conge (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL REFERENCES demande_conge(id),
    validateur_id BIGINT NOT NULL REFERENCES utilisateur(id),
    etape VARCHAR(20) NOT NULL CHECK (etape IN ('MANAGER','RH')),
    decision VARCHAR(20) NOT NULL CHECK (decision IN ('VALIDEE','REFUSEE')),
    commentaire TEXT,
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS demande_absence (
    id BIGSERIAL PRIMARY KEY,
    employe_id BIGINT NOT NULL REFERENCES employe(id),
    remplacant_id BIGINT REFERENCES employe(id),
    declaree_par_id BIGINT NOT NULL REFERENCES utilisateur(id),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    motif TEXT NOT NULL,
    justificatif_url VARCHAR(500),
    statut VARCHAR(30) NOT NULL CHECK (statut IN ('A_TRANSMETTRE_RH','TRANSMISE_RH','JUSTIFIEE','NON_JUSTIFIEE')),
    justifiee BOOLEAN NOT NULL DEFAULT FALSE,
    commentaire_manager TEXT,
    commentaire_rh TEXT,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_decision_rh TIMESTAMP,
    CHECK (date_fin >= date_debut)
);
ALTER TABLE demande_absence ADD COLUMN IF NOT EXISTS remplacant_id BIGINT REFERENCES employe(id);
CREATE INDEX IF NOT EXISTS ix_demande_absence_employe ON demande_absence(employe_id,date_creation DESC);

CREATE TABLE IF NOT EXISTS historique_demande_absence (
    id BIGSERIAL PRIMARY KEY,
    demande_id BIGINT NOT NULL REFERENCES demande_absence(id),
    acteur_id BIGINT NOT NULL REFERENCES utilisateur(id),
    action VARCHAR(30) NOT NULL CHECK (action IN ('TRANSMISE_RH','JUSTIFIEE','NON_JUSTIFIEE')),
    commentaire TEXT,
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
