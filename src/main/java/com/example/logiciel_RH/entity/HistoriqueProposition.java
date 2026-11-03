package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
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
@Table(name = "historique_proposition")
public class HistoriqueProposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposition_id", nullable = false)
    private PropositionEmbauche proposition;

    @ManyToOne
    @JoinColumn(name = "acteur_id", nullable = false)
    private Utilisateur acteur;

    @Column(name = "ancien_statut")
    private String ancienStatut;

    @Column(name = "nouveau_statut", nullable = false)
    private String nouveauStatut;

    @Column(name = "salaire_demande", precision = 12, scale = 2)
    private BigDecimal salaireDemande;

    @Column(name = "salaire_decide", precision = 12, scale = 2)
    private BigDecimal salaireDecide;

    @Column(name = "type_contrat_demande", length = 20)
    private String typeContratDemande;

    @Column(name = "type_contrat_decide", length = 20)
    private String typeContratDecide;

    @Column(name = "commentaire", columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PropositionEmbauche getProposition() { return proposition; }
    public void setProposition(PropositionEmbauche proposition) { this.proposition = proposition; }
    public Utilisateur getActeur() { return acteur; }
    public void setActeur(Utilisateur acteur) { this.acteur = acteur; }
    public String getAncienStatut() { return ancienStatut; }
    public void setAncienStatut(String ancienStatut) { this.ancienStatut = ancienStatut; }
    public String getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(String nouveauStatut) { this.nouveauStatut = nouveauStatut; }
    public BigDecimal getSalaireDemande() { return salaireDemande; }
    public void setSalaireDemande(BigDecimal salaireDemande) { this.salaireDemande = salaireDemande; }
    public BigDecimal getSalaireDecide() { return salaireDecide; }
    public void setSalaireDecide(BigDecimal salaireDecide) { this.salaireDecide = salaireDecide; }
    public String getTypeContratDemande() { return typeContratDemande; }
    public void setTypeContratDemande(String typeContratDemande) { this.typeContratDemande = typeContratDemande; }
    public String getTypeContratDecide() { return typeContratDecide; }
    public void setTypeContratDecide(String typeContratDecide) { this.typeContratDecide = typeContratDecide; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDateTime getDateChangement() { return dateChangement; }
    public void setDateChangement(LocalDateTime dateChangement) { this.dateChangement = dateChangement; }
}
