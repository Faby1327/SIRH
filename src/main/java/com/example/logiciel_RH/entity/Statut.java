package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "statut")
public class Statut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idStatut;

    @Column(name = "libelle")
    private String libelle;

    public Statut() {
    }

    public Long getIdStatut() {
        return idStatut;
    }

    public void setIdStatut(Long idStatut) {
        this.idStatut = idStatut;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelleAffiche() {
        if (libelle == null) return "";
        return switch (libelle) {
            case "BROUILLON" -> "Brouillon";
            case "SOUMISE" -> "En attente RH";
            case "EN_VERIFICATION_FINANCIERE" -> "En attente Finance";
            case "BUDGET_INSUFFISANT" -> "Budget insuffisant";
            case "EN_ATTENTE_VALIDATION" -> "À approuver";
            case "VALIDEE" -> "Approuvée";
            case "REFUSEE" -> "Refusée";
            case "ANNULEE" -> "Annulée";
            default -> libelle;
        };
    }
}
