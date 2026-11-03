package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposition_embauche")
public class PropositionEmbauche {

    public static final String STATUT_PROPOSEE = "PROPOSEE";
    public static final String STATUT_ACCEPTEE = "ACCEPTEE";
    public static final String STATUT_REFUSEE = "REFUSEE";
    public static final String STATUT_ANNULEE = "ANNULEE";
    public static final String STATUT_DEMANDE_SALAIRE = "DEMANDE_SALAIRE";
    public static final String STATUT_DEMANDE_CONTRAT = "DEMANDE_CONTRAT";
    public static final String STATUT_DEMANDE_CONDITIONS = "DEMANDE_CONDITIONS";
    public static final String STATUT_EN_ATTENTE_CONFIRMATION = "EN_ATTENTE_CONFIRMATION";
    public static final String STATUT_CONFIRMEE = "CONFIRMEE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreEmploi offre;

    @ManyToOne
    @JoinColumn(name = "propose_par", nullable = false)
    private Utilisateur proposePar;

    @Column(name = "statut", nullable = false, length = 30)
    private String statut = STATUT_PROPOSEE;

    @Column(name = "date_proposition", nullable = false)
    private LocalDateTime dateProposition = LocalDateTime.now();

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "commentaire_rh", columnDefinition = "TEXT")
    private String commentaireRh;

    @Column(name = "commentaire_candidat", columnDefinition = "TEXT")
    private String commentaireCandidat;

    @Column(name = "salaire_propose", precision = 12, scale = 2)
    private BigDecimal salairePropose;

    @Column(name = "salaire_demande_candidat", precision = 12, scale = 2)
    private BigDecimal salaireDemandeCandidat;

    @Column(name = "type_contrat_propose", nullable = false, length = 20)
    private String typeContratPropose = "CE";

    @Column(name = "type_contrat_demande_candidat", length = 20)
    private String typeContratDemandeCandidat;

    @Column(name = "salaire_final", precision = 12, scale = 2)
    private BigDecimal salaireFinal;

    @Column(name = "date_acceptation_candidat")
    private LocalDateTime dateAcceptationCandidat;

    @Column(name = "date_decision_rh")
    private LocalDateTime dateDecisionRh;

    @Column(name = "commentaire_decision_rh", columnDefinition = "TEXT")
    private String commentaireDecisionRh;

    public PropositionEmbauche() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidature getCandidature() {
        return candidature;
    }

    public void setCandidature(Candidature candidature) {
        this.candidature = candidature;
    }

    public OffreEmploi getOffre() {
        return offre;
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
    }

    public Utilisateur getProposePar() {
        return proposePar;
    }

    public void setProposePar(Utilisateur proposePar) {
        this.proposePar = proposePar;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateProposition() {
        return dateProposition;
    }

    public void setDateProposition(LocalDateTime dateProposition) {
        this.dateProposition = dateProposition;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    public String getCommentaireRh() {
        return commentaireRh;
    }

    public void setCommentaireRh(String commentaireRh) {
        this.commentaireRh = commentaireRh;
    }

    public String getCommentaireCandidat() {
        return commentaireCandidat;
    }

    public void setCommentaireCandidat(String commentaireCandidat) {
        this.commentaireCandidat = commentaireCandidat;
    }

    public BigDecimal getSalairePropose() { return salairePropose; }
    public void setSalairePropose(BigDecimal salairePropose) { this.salairePropose = salairePropose; }
    public BigDecimal getSalaireDemandeCandidat() { return salaireDemandeCandidat; }
    public void setSalaireDemandeCandidat(BigDecimal salaireDemandeCandidat) { this.salaireDemandeCandidat = salaireDemandeCandidat; }
    public String getTypeContratPropose() { return typeContratPropose; }
    public void setTypeContratPropose(String typeContratPropose) { this.typeContratPropose = typeContratPropose; }
    public String getTypeContratDemandeCandidat() { return typeContratDemandeCandidat; }
    public void setTypeContratDemandeCandidat(String typeContratDemandeCandidat) { this.typeContratDemandeCandidat = typeContratDemandeCandidat; }
    public BigDecimal getSalaireFinal() { return salaireFinal; }
    public void setSalaireFinal(BigDecimal salaireFinal) { this.salaireFinal = salaireFinal; }
    public LocalDateTime getDateAcceptationCandidat() { return dateAcceptationCandidat; }
    public void setDateAcceptationCandidat(LocalDateTime dateAcceptationCandidat) { this.dateAcceptationCandidat = dateAcceptationCandidat; }
    public LocalDateTime getDateDecisionRh() { return dateDecisionRh; }
    public void setDateDecisionRh(LocalDateTime dateDecisionRh) { this.dateDecisionRh = dateDecisionRh; }
    public String getCommentaireDecisionRh() { return commentaireDecisionRh; }
    public void setCommentaireDecisionRh(String commentaireDecisionRh) { this.commentaireDecisionRh = commentaireDecisionRh; }

    public String getStatutAffiche() {
        if (STATUT_PROPOSEE.equals(statut)) {
            return "Proposée";
        }
        if (STATUT_ACCEPTEE.equals(statut)) {
            return "Acceptée";
        }
        if (STATUT_REFUSEE.equals(statut)) {
            return "Refusée";
        }
        if (STATUT_ANNULEE.equals(statut)) {
            return "Annulée";
        }
        if (STATUT_DEMANDE_SALAIRE.equals(statut)) {
            return "Salaire à valider";
        }
        if (STATUT_DEMANDE_CONTRAT.equals(statut)) {
            return "Contrat à valider";
        }
        if (STATUT_DEMANDE_CONDITIONS.equals(statut)) {
            return "Conditions à valider";
        }
        if (STATUT_EN_ATTENTE_CONFIRMATION.equals(statut)) {
            return "En attente de confirmation RH";
        }
        if (STATUT_CONFIRMEE.equals(statut)) {
            return "Embauche confirmée";
        }
        return statut == null ? "" : statut;
    }

    public String getDatePropositionFormatee() {
        return dateProposition == null ? "" : dateProposition.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}
