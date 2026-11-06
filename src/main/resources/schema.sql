-- SIRH - schema.sql
-- Reconstruction complète de la base. Les données sont dans data.sql.
-- Exécuter avec psql en ciblant une base permettant l'administration,
-- par exemple : psql -h localhost -U postgres -d postgres -f schema.sql


DROP DATABASE IF EXISTS sirh2 WITH (FORCE);
CREATE DATABASE sirh2;

\c sirh2

CREATE TABLE IF NOT EXISTS departement (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS personne (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    telephone VARCHAR(30),
    ville VARCHAR(100),
    date_naissance DATE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS poste (
    id SERIAL PRIMARY KEY,
    intitule VARCHAR(100) NOT NULL,
    departement_id INT NOT NULL,
    description TEXT,

    CONSTRAINT fk_poste_departement
        FOREIGN KEY (departement_id)
        REFERENCES departement(id)
);

CREATE TABLE IF NOT EXISTS role (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS utilisateur (
    id SERIAL PRIMARY KEY,
    personne_id INT NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    departement_id INT,
    actif BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_utilisateur_role
        FOREIGN KEY (role_id)
        REFERENCES role(id),

    CONSTRAINT fk_utilisateur_personne
        FOREIGN KEY (personne_id)
        REFERENCES personne(id),

    CONSTRAINT fk_utilisateur_departement
        FOREIGN KEY (departement_id)
        REFERENCES departement(id)
);

CREATE TABLE IF NOT EXISTS candidat (
    id SERIAL PRIMARY KEY,
    personne_id INT NOT NULL UNIQUE,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_candidat_personne
        FOREIGN KEY (personne_id)
        REFERENCES personne(id)
        ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS employe_matricule_seq START WITH 1;

CREATE TABLE IF NOT EXISTS employe (
    id SERIAL PRIMARY KEY,
    personne_id INT NOT NULL UNIQUE,
    matricule VARCHAR(50) NOT NULL UNIQUE,
    date_embauche DATE NOT NULL,
    date_fin DATE,
    statut VARCHAR(20) NOT NULL DEFAULT 'ACTIF',
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_employe_personne
        FOREIGN KEY (personne_id)
        REFERENCES personne(id),

    CONSTRAINT ck_employe_statut
        CHECK (statut IN ('ACTIF', 'SUSPENDU', 'SORTI')),

    CONSTRAINT ck_employe_dates
        CHECK (date_fin IS NULL OR date_fin >= date_embauche)
);

CREATE TABLE IF NOT EXISTS statut (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS regle_budgetaire (
    id SERIAL PRIMARY KEY,
    poste_id INT NOT NULL UNIQUE,

    budget_min NUMERIC(15,2) NOT NULL
        CHECK (budget_min >= 0),

    budget_max NUMERIC(15,2) NOT NULL
        CHECK (budget_max >= budget_min),

    CONSTRAINT fk_regle_budgetaire_poste
        FOREIGN KEY (poste_id)
        REFERENCES poste(id)
);

CREATE TABLE IF NOT EXISTS budget_entreprise (
    id SERIAL PRIMARY KEY,
    departement_id INT NOT NULL,
    annee INT NOT NULL
        CHECK (annee >= 2000),
    montant_disponible NUMERIC(14,2) NOT NULL
        CHECK (montant_disponible >= 0),

    CONSTRAINT uq_budget_entreprise_departement_annee
        UNIQUE (departement_id, annee),

    CONSTRAINT fk_budget_entreprise_departement
        FOREIGN KEY (departement_id)
        REFERENCES departement(id)
);

CREATE TABLE IF NOT EXISTS demande_recrutement (
    id SERIAL PRIMARY KEY,
    motif TEXT NOT NULL,

    nombre_personnes INT NOT NULL DEFAULT 1
        CHECK (nombre_personnes > 0),

    type_recrutement VARCHAR(30) NOT NULL
        CHECK (
            type_recrutement IN (
                'CREATION',
                'REMPLACEMENT'
            )
        ),

    date_demande DATE NOT NULL DEFAULT CURRENT_DATE,
    date_souhaitee DATE,

    -- Le budget saisi par le manager est le salaire mensuel estimé
    -- d'une seule personne.
    salaire_mensuel_estime NUMERIC(12,2) NOT NULL
        CHECK (salaire_mensuel_estime >= 0),

    -- Montant unitaire saisi par le manager (par personne).
    -- L'application mappe actuellement cette colonne sur budgetDemande.
    cout_total_estime NUMERIC(14,2) NOT NULL
        CHECK (cout_total_estime >= 0),

    -- Le salaire estimé et le budget unitaire saisi doivent rester identiques.
    -- Le coût total de la demande est calculé par l'application :
    -- salaire_mensuel_estime * nombre_personnes.
    CONSTRAINT ck_demande_salaire_budget_unitaire
        CHECK (salaire_mensuel_estime = cout_total_estime),

    poste_id INT NOT NULL,
    demandeur_id INT NOT NULL,
    statut_id INT NOT NULL,

    date_modification TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_demande_poste
        FOREIGN KEY (poste_id)
        REFERENCES poste(id),

    CONSTRAINT fk_demande_demandeur
        FOREIGN KEY (demandeur_id)
        REFERENCES utilisateur(id),

    CONSTRAINT fk_demande_statut
        FOREIGN KEY (statut_id)
        REFERENCES statut(id),

    CONSTRAINT ck_date_souhaitee
        CHECK (
            date_souhaitee IS NULL
            OR date_souhaitee >= date_demande
        )
);

CREATE TABLE IF NOT EXISTS historique_validation_demande (
    id SERIAL PRIMARY KEY,
    demande_id INT NOT NULL,
    validateur_id INT NOT NULL,

    type_validation VARCHAR(30) NOT NULL
        CHECK (
            type_validation IN (
                'MANAGER',
                'RH',
                'FINANCIERE',
                'FINALE'
            )
        ),

    statut_id INT NOT NULL,
    commentaire TEXT,
    date_validation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_validation_demande
        FOREIGN KEY (demande_id)
        REFERENCES demande_recrutement(id),

    CONSTRAINT fk_validation_validateur
        FOREIGN KEY (validateur_id)
        REFERENCES utilisateur(id),

    CONSTRAINT fk_validation_statut
        FOREIGN KEY (statut_id)
        REFERENCES statut(id)
);

CREATE TABLE IF NOT EXISTS offre_emploi (
    id SERIAL PRIMARY KEY,

    -- Une demande approuvée ne donne qu'une seule offre.
    demande_id INT NOT NULL UNIQUE,

    titre VARCHAR(200) NOT NULL,
    description TEXT,
    date_publication TIMESTAMP,

    statut VARCHAR(30) NOT NULL DEFAULT 'PUBLIEE'
        CHECK (
            statut IN (
                'PUBLIEE',
                'EN_ATTENTE_CONFIRMATION',
                'CLOTUREE'
            )
        ),

    CONSTRAINT fk_offre_demande
        FOREIGN KEY (demande_id)
        REFERENCES demande_recrutement(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS critere_candidature (
    id SERIAL PRIMARY KEY,

    poste_id INT NOT NULL,

    libelle VARCHAR(150) NOT NULL,

    type VARCHAR(20) NOT NULL
        CHECK (
            type IN (
                'LISTE',
                'CASES',
                'OUI_NON',
                'NOMBRE',
                'TEXTE'
            )
        ),

    -- Valeurs séparées par « ; » pour les types LISTE et CASES.
    options TEXT,

    obligatoire BOOLEAN NOT NULL DEFAULT TRUE,
    ordre INT NOT NULL DEFAULT 0,

    CONSTRAINT fk_critere_poste
        FOREIGN KEY (poste_id)
        REFERENCES poste(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS candidature (
    id SERIAL PRIMARY KEY,

    offre_id INT NOT NULL,
    candidat_id INT,

    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    telephone VARCHAR(30) NOT NULL,
    message TEXT,

    date_candidature TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    statut VARCHAR(20) NOT NULL DEFAULT 'NOUVELLE'
        CHECK (
            statut IN (
                'NOUVELLE',
                'ETUDIEE',
                'PRESELECTIONNEE',
                'RETENUE',
                'REFUSEE',
                'EVALUEE'
            )
        ),

    -- Présélection par les RH (bloc 2 — Recrutement).
    date_preselection TIMESTAMP,
    preselectionne_par INT,
    commentaire_preselection TEXT,

    CONSTRAINT fk_candidature_offre
        FOREIGN KEY (offre_id)
        REFERENCES offre_emploi(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_candidature_preselectionne_par
        FOREIGN KEY (preselectionne_par)
        REFERENCES utilisateur(id),

    CONSTRAINT fk_candidature_candidat
        FOREIGN KEY (candidat_id)
        REFERENCES candidat(id),

    -- Un même candidat ne postule qu'une fois par offre.
    CONSTRAINT uk_candidature_offre_email
        UNIQUE (offre_id, email),

    CONSTRAINT uk_candidature_offre_candidat
        UNIQUE (offre_id, candidat_id)
);

CREATE TABLE IF NOT EXISTS reponse_candidature (
    id SERIAL PRIMARY KEY,

    candidature_id INT NOT NULL,
    critere_id INT NOT NULL,
    valeur TEXT,

    CONSTRAINT fk_reponse_candidature
        FOREIGN KEY (candidature_id)
        REFERENCES candidature(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_reponse_critere
        FOREIGN KEY (critere_id)
        REFERENCES critere_candidature(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS critere_evaluation (
    id SERIAL PRIMARY KEY,
    offre_id INT NOT NULL,
    libelle VARCHAR(150) NOT NULL,
    ordre INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_critere_evaluation_offre FOREIGN KEY (offre_id) REFERENCES offre_emploi(id) ON DELETE CASCADE,
    CONSTRAINT uq_critere_evaluation_offre_libelle UNIQUE (offre_id, libelle)
);

CREATE TABLE IF NOT EXISTS entretien (
    id SERIAL PRIMARY KEY,
    candidature_id INT NOT NULL,
    type_evaluation VARCHAR(20) NOT NULL,
    date_heure TIMESTAMP NOT NULL,
    lieu VARCHAR(250) NOT NULL,
    consigne TEXT,
    statut VARCHAR(10) NOT NULL DEFAULT 'PLANIFIE',
    planifie_par INT NOT NULL,
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_entretien_candidature FOREIGN KEY (candidature_id) REFERENCES candidature(id) ON DELETE CASCADE,
    CONSTRAINT fk_entretien_planifie_par FOREIGN KEY (planifie_par) REFERENCES utilisateur(id),
    CONSTRAINT ck_entretien_type CHECK (type_evaluation IN ('ENTRETIEN_RH', 'ENTRETIEN_TECHNIQUE', 'TEST_PRATIQUE')),
    CONSTRAINT ck_entretien_statut CHECK (statut IN ('PLANIFIE', 'TERMINE', 'ANNULE'))
);

CREATE TABLE IF NOT EXISTS entretien_evaluateur (
    entretien_id INT NOT NULL,
    utilisateur_id INT NOT NULL,
    PRIMARY KEY (entretien_id, utilisateur_id),
    CONSTRAINT fk_entretien_eval_entretien FOREIGN KEY (entretien_id) REFERENCES entretien(id) ON DELETE CASCADE,
    CONSTRAINT fk_entretien_eval_utilisateur FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id)
);

CREATE TABLE IF NOT EXISTS evaluation (
    id SERIAL PRIMARY KEY,
    entretien_id INT NOT NULL,
    evaluateur_id INT NOT NULL,
    recommandation VARCHAR(12) NOT NULL,
    commentaire TEXT NOT NULL,
    date_evaluation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_modification TIMESTAMP,
    CONSTRAINT fk_evaluation_entretien FOREIGN KEY (entretien_id) REFERENCES entretien(id) ON DELETE CASCADE,
    CONSTRAINT fk_evaluation_evaluateur FOREIGN KEY (entretien_id, evaluateur_id) REFERENCES entretien_evaluateur(entretien_id, utilisateur_id),
    CONSTRAINT uq_evaluation_entretien_evaluateur UNIQUE (entretien_id, evaluateur_id),
    CONSTRAINT ck_evaluation_recommandation CHECK (recommandation IN ('FAVORABLE', 'RESERVE', 'DEFAVORABLE'))
);

CREATE TABLE IF NOT EXISTS evaluation_note (
    id SERIAL PRIMARY KEY,
    evaluation_id INT NOT NULL,
    critere_id INT NOT NULL,
    note INT NOT NULL,
    CONSTRAINT uq_evaluation_note_evaluation_critere UNIQUE (evaluation_id, critere_id),
    CONSTRAINT fk_evaluation_note_evaluation FOREIGN KEY (evaluation_id) REFERENCES evaluation(id) ON DELETE CASCADE,
    CONSTRAINT fk_evaluation_note_critere FOREIGN KEY (critere_id) REFERENCES critere_evaluation(id),
    CONSTRAINT ck_evaluation_note_valeur CHECK (note BETWEEN 1 AND 5)
);

CREATE TABLE IF NOT EXISTS proposition_embauche (
    id SERIAL PRIMARY KEY,
    candidature_id INT NOT NULL,
    offre_id INT NOT NULL,
    propose_par INT NOT NULL,
    statut VARCHAR(30) NOT NULL DEFAULT 'PROPOSEE',
    date_proposition TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_reponse TIMESTAMP,
    commentaire_rh TEXT,
    commentaire_candidat TEXT,
    salaire_propose NUMERIC(12,2),
    salaire_demande_candidat NUMERIC(12,2),
    type_contrat_propose VARCHAR(20) NOT NULL DEFAULT 'CE',
    type_contrat_demande_candidat VARCHAR(20),
    salaire_final NUMERIC(12,2),
    date_acceptation_candidat TIMESTAMP,
    date_decision_rh TIMESTAMP,
    commentaire_decision_rh TEXT,
    CONSTRAINT fk_proposition_candidature FOREIGN KEY (candidature_id) REFERENCES candidature(id) ON DELETE CASCADE,
    CONSTRAINT fk_proposition_offre FOREIGN KEY (offre_id) REFERENCES offre_emploi(id) ON DELETE CASCADE,
    CONSTRAINT fk_proposition_propose_par FOREIGN KEY (propose_par) REFERENCES utilisateur(id),
    CONSTRAINT ck_proposition_statut CHECK (statut IN ('PROPOSEE','ACCEPTEE','REFUSEE','ANNULEE','DEMANDE_SALAIRE','DEMANDE_CONTRAT','DEMANDE_CONDITIONS','EN_ATTENTE_CONFIRMATION','CONFIRMEE')),
    CONSTRAINT ck_proposition_type_contrat CHECK (type_contrat_propose IN ('CE', 'CDD', 'CDI')),
    CONSTRAINT ck_proposition_type_contrat_demande CHECK (type_contrat_demande_candidat IS NULL OR type_contrat_demande_candidat IN ('CDD', 'CDI')),
    CONSTRAINT uq_proposition_candidature UNIQUE (candidature_id)
);

CREATE TABLE IF NOT EXISTS historique_proposition (
    id SERIAL PRIMARY KEY,
    proposition_id INT NOT NULL,
    acteur_id INT NOT NULL,
    ancien_statut VARCHAR(30),
    nouveau_statut VARCHAR(30) NOT NULL,
    salaire_demande NUMERIC(12,2),
    salaire_decide NUMERIC(12,2),
    type_contrat_demande VARCHAR(20),
    type_contrat_decide VARCHAR(20),
    commentaire TEXT,
    date_changement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_historique_proposition
        FOREIGN KEY (proposition_id)
        REFERENCES proposition_embauche(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_historique_proposition_acteur
        FOREIGN KEY (acteur_id)
        REFERENCES utilisateur(id),

    CONSTRAINT ck_historique_proposition_statut
        CHECK (nouveau_statut IN (
            'PROPOSEE', 'ACCEPTEE', 'REFUSEE', 'ANNULEE',
            'DEMANDE_SALAIRE', 'DEMANDE_CONTRAT', 'DEMANDE_CONDITIONS',
            'EN_ATTENTE_CONFIRMATION', 'CONFIRMEE'
        )),

    CONSTRAINT ck_historique_proposition_type_contrat_demande
        CHECK (type_contrat_demande IS NULL OR type_contrat_demande IN ('CDD', 'CDI')),

    CONSTRAINT ck_historique_proposition_type_contrat_decide
        CHECK (type_contrat_decide IS NULL OR type_contrat_decide IN ('CE', 'CDD', 'CDI')),

    CONSTRAINT ck_historique_proposition_montants
        CHECK (salaire_demande IS NULL OR salaire_demande >= 0)
);

CREATE TABLE IF NOT EXISTS contrat (
    id SERIAL PRIMARY KEY,
    employe_id INT NOT NULL,
    proposition_id INT,
    poste_id INT NOT NULL,

    type_contrat VARCHAR(20) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE,
    salaire_mensuel NUMERIC(12,2) NOT NULL,
    devise VARCHAR(10) NOT NULL DEFAULT 'Ar',
    statut VARCHAR(20) NOT NULL DEFAULT 'EN_COURS',

    FOREIGN KEY (employe_id)
        REFERENCES employe(id),

    FOREIGN KEY (proposition_id)
        REFERENCES proposition_embauche(id),

    FOREIGN KEY (poste_id)
        REFERENCES poste(id),

    CHECK (type_contrat IN ('CE', 'CDD', 'CDI')),
    CHECK (statut IN ('EN_COURS', 'TERMINE', 'ANNULE')),
    CHECK (salaire_mensuel >= 0),
    CHECK (date_fin IS NULL OR date_fin >= date_debut)
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_contrat_employe_en_cours
    ON contrat(employe_id)
    WHERE statut = 'EN_COURS';

CREATE TABLE IF NOT EXISTS fiche_paie (
    id SERIAL PRIMARY KEY,
    employe_id INT NOT NULL,
    contrat_id INT NOT NULL,
    mois INT NOT NULL,
    annee INT NOT NULL,
    salaire_base NUMERIC(12,2) NOT NULL,
    total_primes NUMERIC(12,2) NOT NULL DEFAULT 0,
    total_retenues NUMERIC(12,2) NOT NULL DEFAULT 0,
    net_a_payer NUMERIC(12,2) NOT NULL,
    statut VARCHAR(20) NOT NULL DEFAULT 'BROUILLON',
    date_creation TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_validation TIMESTAMP,
    CONSTRAINT fk_fiche_paie_employe FOREIGN KEY (employe_id) REFERENCES employe(id),
    CONSTRAINT fk_fiche_paie_contrat FOREIGN KEY (contrat_id) REFERENCES contrat(id),
    CONSTRAINT uq_fiche_paie_periode UNIQUE (employe_id, contrat_id, mois, annee),
    CONSTRAINT ck_fiche_paie_mois CHECK (mois BETWEEN 1 AND 12),
    CONSTRAINT ck_fiche_paie_annee CHECK (annee BETWEEN 2000 AND 2200),
    CONSTRAINT ck_fiche_paie_montants CHECK (
        salaire_base >= 0 AND total_primes >= 0 AND total_retenues >= 0 AND net_a_payer >= 0
    ),
    CONSTRAINT ck_fiche_paie_statut CHECK (statut IN ('BROUILLON', 'VALIDEE', 'PAYEE', 'ANNULEE'))
);

CREATE TABLE IF NOT EXISTS paiement_salaire (
    id SERIAL PRIMARY KEY,
    fiche_paie_id INT NOT NULL UNIQUE,
    montant NUMERIC(12,2) NOT NULL,
    date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    mode_paiement VARCHAR(20) NOT NULL DEFAULT 'MANUEL',
    reference VARCHAR(100) NOT NULL UNIQUE,
    statut VARCHAR(20) NOT NULL DEFAULT 'EFFECTUE',
    CONSTRAINT fk_paiement_fiche FOREIGN KEY (fiche_paie_id) REFERENCES fiche_paie(id),
    CONSTRAINT ck_paiement_montant CHECK (montant >= 0),
    CONSTRAINT ck_paiement_mode CHECK (mode_paiement IN ('MANUEL', 'VIREMENT', 'ESPECES')),
    CONSTRAINT ck_paiement_statut CHECK (statut IN ('EFFECTUE', 'ECHEC', 'ANNULE'))
);

CREATE INDEX IF NOT EXISTS idx_poste_departement ON poste(departement_id);
CREATE INDEX IF NOT EXISTS idx_utilisateur_role ON utilisateur(role_id);
CREATE INDEX IF NOT EXISTS idx_utilisateur_departement ON utilisateur(departement_id);
CREATE INDEX IF NOT EXISTS idx_utilisateur_personne ON utilisateur(personne_id);
CREATE INDEX IF NOT EXISTS idx_employe_statut ON employe(statut);
CREATE INDEX IF NOT EXISTS idx_demande_poste ON demande_recrutement(poste_id);
CREATE INDEX IF NOT EXISTS idx_demande_demandeur ON demande_recrutement(demandeur_id);
CREATE INDEX IF NOT EXISTS idx_demande_statut ON demande_recrutement(statut_id);
CREATE INDEX IF NOT EXISTS idx_validation_demande ON historique_validation_demande(demande_id);
CREATE INDEX IF NOT EXISTS idx_offre_statut ON offre_emploi(statut);
CREATE INDEX IF NOT EXISTS idx_budget_entreprise_departement_annee ON budget_entreprise(departement_id, annee);
CREATE INDEX IF NOT EXISTS idx_critere_poste ON critere_candidature(poste_id);
CREATE INDEX IF NOT EXISTS idx_candidature_offre ON candidature(offre_id);
CREATE INDEX IF NOT EXISTS idx_candidature_candidat ON candidature(candidat_id);
CREATE INDEX IF NOT EXISTS idx_candidature_statut ON candidature(statut);
CREATE INDEX IF NOT EXISTS idx_critere_evaluation_offre ON critere_evaluation(offre_id);
CREATE INDEX IF NOT EXISTS idx_entretien_candidature ON entretien(candidature_id);
CREATE INDEX IF NOT EXISTS idx_entretien_date ON entretien(date_heure);
CREATE INDEX IF NOT EXISTS idx_entretien_statut ON entretien(statut);
CREATE INDEX IF NOT EXISTS idx_entretien_eval_utilisateur ON entretien_evaluateur(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_entretien ON evaluation(entretien_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_evaluateur ON evaluation(evaluateur_id);
CREATE INDEX IF NOT EXISTS idx_evaluation_note_critere ON evaluation_note(critere_id);
CREATE INDEX IF NOT EXISTS idx_proposition_offre ON proposition_embauche(offre_id);
CREATE INDEX IF NOT EXISTS idx_proposition_statut ON proposition_embauche(statut);
CREATE INDEX IF NOT EXISTS idx_proposition_candidature ON proposition_embauche(candidature_id);
CREATE INDEX IF NOT EXISTS idx_historique_proposition ON historique_proposition(proposition_id);
CREATE INDEX IF NOT EXISTS idx_contrat_employe ON contrat(employe_id);
CREATE INDEX IF NOT EXISTS idx_contrat_poste ON contrat(poste_id);
CREATE INDEX IF NOT EXISTS idx_fiche_paie_periode ON fiche_paie(annee, mois);
CREATE INDEX IF NOT EXISTS idx_fiche_paie_statut ON fiche_paie(statut);
CREATE INDEX IF NOT EXISTS idx_fiche_paie_employe ON fiche_paie(employe_id);
CREATE INDEX IF NOT EXISTS idx_paiement_salaire_date ON paiement_salaire(date_paiement);

-- ============================================================
-- BLOC 5 - Historique et journal d'audit
-- ============================================================

CREATE TABLE IF NOT EXISTS type_action (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS type_entite (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS historique_employe (
    id SERIAL PRIMARY KEY,
    employe_id INT NOT NULL REFERENCES employe(id),
    poste_id INT NOT NULL REFERENCES poste(id),
    departement_id INT NOT NULL REFERENCES departement(id),
    superieur_id INT REFERENCES employe(id),
    salaire NUMERIC(12,2) NOT NULL CHECK (salaire >= 0),
    date_effet DATE NOT NULL,
    date_enregistrement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_hist_employe_superieur
        CHECK (superieur_id IS NULL OR superieur_id <> employe_id)
);

CREATE TABLE IF NOT EXISTS historique_situation_personnelle (
    id SERIAL PRIMARY KEY,
    employe_id INT NOT NULL REFERENCES employe(id),
    telephone VARCHAR(30),
    email_personnel VARCHAR(150),
    adresse VARCHAR(255),
    commune VARCHAR(100),
    region VARCHAR(100),
    situation_matrimoniale VARCHAR(30),
    nombre_enfants INT CHECK (nombre_enfants IS NULL OR nombre_enfants >= 0),
    contact_urgence_nom VARCHAR(150),
    contact_urgence_relation VARCHAR(50),
    contact_urgence_telephone VARCHAR(30),
    date_effet DATE NOT NULL,
    date_enregistrement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- entite_id est polymorphe : type_entite_id dit de quelle table il s'agit.
-- Pas de cle etrangere possible, la coherence est controlee par AuditService.
CREATE TABLE IF NOT EXISTS journal_audit (
    id BIGSERIAL PRIMARY KEY,
    utilisateur_id INT NOT NULL REFERENCES utilisateur(id),
    action_id INT NOT NULL REFERENCES type_action(id),
    entite_id INT NOT NULL,
    type_entite_id INT NOT NULL REFERENCES type_entite(id),
    date_action TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    anciennes_valeurs TEXT,
    nouvelles_valeurs TEXT
);

CREATE INDEX IF NOT EXISTS idx_hist_employe ON historique_employe(employe_id, date_effet);
CREATE INDEX IF NOT EXISTS idx_hist_situation ON historique_situation_personnelle(employe_id, date_effet);
CREATE INDEX IF NOT EXISTS idx_audit_date ON journal_audit(date_action);
CREATE INDEX IF NOT EXISTS idx_audit_utilisateur ON journal_audit(utilisateur_id);
CREATE INDEX IF NOT EXISTS idx_audit_entite ON journal_audit(type_entite_id, entite_id);