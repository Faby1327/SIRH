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
 * Offre d'emploi publiée à partir d'une demande de recrutement approuvée
 * par la Direction (bloc 2 — Recrutement).
 */
@Entity
@Table(name = "offre_emploi")
public class OffreEmploi {

    public static final String STATUT_PUBLIEE = "PUBLIEE";
    public static final String STATUT_EN_ATTENTE_CONFIRMATION = "EN_ATTENTE_CONFIRMATION";
    public static final String STATUT_CLOTUREE = "CLOTUREE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idOffre;

    @ManyToOne
    @JoinColumn(name = "demande_id", nullable = false)
    private DemandeRecrutement demande;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description")
    private String description;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(name = "statut", nullable = false)
    private String statut;

    public OffreEmploi() {
    }

    public Long getIdOffre() {
        return idOffre;
    }

    public void setIdOffre(Long idOffre) {
        this.idOffre = idOffre;
    }

    public DemandeRecrutement getDemande() {
        return demande;
    }

    public void setDemande(DemandeRecrutement demande) {
        this.demande = demande;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDatePublication() {
        return datePublication;
    }

    public void setDatePublication(LocalDateTime datePublication) {
        this.datePublication = datePublication;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    // ------------------------------------------------------------------
    // Valeurs dérivées pour l'affichage
    // ------------------------------------------------------------------

    public boolean isPubliee() {
        return STATUT_PUBLIEE.equals(statut);
    }

    public boolean isCloturee() {
        return STATUT_CLOTUREE.equals(statut);
    }

    public String getStatutAffiche() {
        if (isCloturee()) {
            return "Clôturée";
        }
        if (STATUT_EN_ATTENTE_CONFIRMATION.equals(statut)) {
            return "En attente de confirmation RH";
        }
        return isPubliee() ? "Publiée" : statut;
    }

    public String getDatePublicationFormatee() {
        return datePublication == null ? "" : datePublication.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** Poste de l'offre, repris de la demande d'origine. */
    public Poste getPoste() {
        return demande == null ? null : demande.getPoste();
    }

    /** Service qui recrute, repris de la demande d'origine. */
    public Departement getDepartement() {
        return demande == null ? null : demande.getDepartement();
    }

    public Integer getNombrePersonnes() {
        return demande == null ? null : demande.getNombrePersonnes();
    }

    public String getTypeRecrutementAffiche() {
        return demande == null ? "" : demande.getTypeRecrutementAffiche();
    }

    public String getReference() {
        return demande == null ? "" : "DP-" + demande.getIdDemande();
    }
}
