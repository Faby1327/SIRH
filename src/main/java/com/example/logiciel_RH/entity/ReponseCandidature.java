package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Réponse d'un candidat à un critère du formulaire (bloc 2 — Recrutement).
 */
@Entity
@Table(name = "reponse_candidature")
public class ReponseCandidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idReponse;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;

    @ManyToOne
    @JoinColumn(name = "critere_id", nullable = false)
    private CritereCandidature critere;

    @Column(name = "valeur")
    private String valeur;

    public ReponseCandidature() {
    }

    public Long getIdReponse() {
        return idReponse;
    }

    public void setIdReponse(Long idReponse) {
        this.idReponse = idReponse;
    }

    public Candidature getCandidature() {
        return candidature;
    }

    public void setCandidature(Candidature candidature) {
        this.candidature = candidature;
    }

    public CritereCandidature getCritere() {
        return critere;
    }

    public void setCritere(CritereCandidature critere) {
        this.critere = critere;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
}
