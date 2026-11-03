package com.example.logiciel_RH.entity;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Champ du formulaire de candidature, propre à un poste (bloc 2 — Recrutement).
 *
 * Les types disponibles sont : LISTE, CASES, OUI_NON, NOMBRE et TEXTE.
 * Les valeurs possibles d'une LISTE ou d'un ensemble de CASES sont stockées
 * dans {@code options}, séparées par « ; ».
 */
@Entity
@Table(name = "critere_candidature")
public class CritereCandidature {

    public static final String TYPE_LISTE = "LISTE";
    public static final String TYPE_CASES = "CASES";
    public static final String TYPE_OUI_NON = "OUI_NON";
    public static final String TYPE_NOMBRE = "NOMBRE";
    public static final String TYPE_TEXTE = "TEXTE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idCritere;

    @ManyToOne
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "options")
    private String options;

    @Column(name = "obligatoire", nullable = false)
    private boolean obligatoire;

    @Column(name = "ordre", nullable = false)
    private int ordre;

    public CritereCandidature() {
    }

    public Long getIdCritere() {
        return idCritere;
    }

    public void setIdCritere(Long idCritere) {
        this.idCritere = idCritere;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public boolean isObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(boolean obligatoire) {
        this.obligatoire = obligatoire;
    }

    public int getOrdre() {
        return ordre;
    }

    public void setOrdre(int ordre) {
        this.ordre = ordre;
    }

    // ------------------------------------------------------------------
    // Aides pour l'affichage du formulaire
    // ------------------------------------------------------------------

    /** Valeurs proposées, quel que soit le type du critère. */
    public List<String> getOptionsListe() {
        if (options == null || options.isBlank()) {
            return List.of();
        }
        return Arrays.stream(options.split(";"))
                .map(String::trim)
                .filter(valeur -> !valeur.isEmpty())
                .toList();
    }

    public boolean isListe() {
        return TYPE_LISTE.equals(type);
    }

    public boolean isCases() {
        return TYPE_CASES.equals(type);
    }

    public boolean isOuiNon() {
        return TYPE_OUI_NON.equals(type);
    }

    public boolean isNombre() {
        return TYPE_NOMBRE.equals(type);
    }

    public boolean isTexte() {
        return TYPE_TEXTE.equals(type);
    }

    /**
     * Vrai si l'option fait partie de la valeur déjà soumise.
     * Sert à pré-cocher le formulaire lorsqu'un champ est en erreur.
     */
    public boolean estSelectionnee(String valeurSoumise, String option) {
        if (valeurSoumise == null || option == null) {
            return false;
        }
        for (String valeur : valeurSoumise.split(",")) {
            if (valeur.trim().equals(option)) {
                return true;
            }
        }
        return false;
    }
}
