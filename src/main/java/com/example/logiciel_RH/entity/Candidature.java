package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Candidature déposée depuis la page publique d'une offre (bloc 2 — Recrutement).
 */
@Entity
@Table(name = "candidature")
public class Candidature {

    public static final String STATUT_NOUVELLE = "NOUVELLE";
    public static final String STATUT_ETUDIEE = "ETUDIEE";
    public static final String STATUT_PRESELECTIONNEE = "PRESELECTIONNEE";
    public static final String STATUT_EVALUEE = "EVALUEE";
    public static final String STATUT_RETENUE = "RETENUE";
    public static final String STATUT_REFUSEE = "REFUSEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idCandidature;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreEmploi offre;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "telephone", nullable = false)
    private String telephone;

    @Column(name = "message")
    private String message;

    @Column(name = "date_candidature", nullable = false)
    private LocalDateTime dateCandidature;

    @Column(name = "statut", nullable = false)
    private String statut;

    @Column(name = "date_preselection")
    private LocalDateTime datePreselection;

    @ManyToOne
    @JoinColumn(name = "preselectionne_par")
    private Utilisateur preselectionnePar;

    @Column(name = "commentaire_preselection")
    private String commentairePreselection;

    public Candidature() {
    }

    public Long getIdCandidature() {
        return idCandidature;
    }

    public void setIdCandidature(Long idCandidature) {
        this.idCandidature = idCandidature;
    }

    public OffreEmploi getOffre() {
        return offre;
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateCandidature() {
        return dateCandidature;
    }

    public void setDateCandidature(LocalDateTime dateCandidature) {
        this.dateCandidature = dateCandidature;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePreselection() {
        return datePreselection;
    }

    public void setDatePreselection(LocalDateTime datePreselection) {
        this.datePreselection = datePreselection;
    }

    public Utilisateur getPreselectionnePar() {
        return preselectionnePar;
    }

    public void setPreselectionnePar(Utilisateur preselectionnePar) {
        this.preselectionnePar = preselectionnePar;
    }

    public String getCommentairePreselection() {
        return commentairePreselection;
    }

    public void setCommentairePreselection(String commentairePreselection) {
        this.commentairePreselection = commentairePreselection;
    }

    // ------------------------------------------------------------------
    // Valeurs dérivées pour l'affichage
    // ------------------------------------------------------------------

    public String getNomComplet() {
        return ((prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom)).trim();
    }

    public String getDateCandidatureFormatee() {
        return dateCandidature == null ? ""
                : dateCandidature.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public boolean isPreselectionnee() {
        return STATUT_PRESELECTIONNEE.equals(statut);
    }

    public String getDatePreselectionFormatee() {
        return datePreselection == null ? ""
                : datePreselection.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getStatutAffiche() {
        if (isPreselectionnee()) {
            return "Présélectionné";
        }
        if (STATUT_ETUDIEE.equals(statut)) {
            return "Étudiée";
        }
        if (STATUT_EVALUEE.equals(statut)) {
            return "Évaluée";
        }
        if (STATUT_RETENUE.equals(statut)) {
            return "Retenue";
        }
        if (STATUT_REFUSEE.equals(statut)) {
            return "Refusée";
        }
        return "Nouvelle";
    }
}
