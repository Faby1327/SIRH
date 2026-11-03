package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historique_validation_demande")
public class HistoriqueStatutDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idHistorique;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private DemandeRecrutement demande;

    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;

    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private Utilisateur utilisateur;

    @Column(name = "type_validation")
    private String typeValidation;

    @Column(name = "date_validation")
    private LocalDateTime dateChangement;

    @Column(name = "commentaire")
    private String remarque;

    public Long getIdHistorique() {
        return idHistorique;
    }

    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }

    public DemandeRecrutement getDemande() {
        return demande;
    }

    public void setDemande(DemandeRecrutement demande) {
        this.demande = demande;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getTypeValidation() {
        return typeValidation;
    }

    public void setTypeValidation(String typeValidation) {
        this.typeValidation = typeValidation;
    }

    public LocalDateTime getDateChangement() {
        return dateChangement;
    }

    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }
}
