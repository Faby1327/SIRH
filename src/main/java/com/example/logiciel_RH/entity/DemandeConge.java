package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "demande_conge")
public class DemandeConge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "employe_id")
    private Employe employe;
    @ManyToOne(optional = false) @JoinColumn(name = "demandeur_id")
    private Utilisateur demandeur;
    @Column(name = "date_debut", nullable = false) private LocalDate dateDebut;
    @Column(name = "date_fin", nullable = false) private LocalDate dateFin;
    @Column(name = "jours", nullable = false) private int jours;
    @Column(nullable = false) private String statut = "EN_ATTENTE_MANAGER";
    @Column(columnDefinition = "text") private String motif;
    @Column(name = "commentaire_manager", columnDefinition = "text") private String commentaireManager;
    @Column(name = "commentaire_rh", columnDefinition = "text") private String commentaireRh;
    @ManyToOne @JoinColumn(name = "remplacant_id") private Employe remplacant;
    @Column(name = "date_creation", nullable = false) private LocalDateTime dateCreation = LocalDateTime.now();
    @Column(name = "date_decision_manager") private LocalDateTime dateDecisionManager;
    @Column(name = "date_decision_rh") private LocalDateTime dateDecisionRh;
    public Long getId(){return id;} public Employe getEmploye(){return employe;} public void setEmploye(Employe v){employe=v;}
    public Utilisateur getDemandeur(){return demandeur;} public void setDemandeur(Utilisateur v){demandeur=v;}
    public LocalDate getDateDebut(){return dateDebut;} public void setDateDebut(LocalDate v){dateDebut=v;}
    public LocalDate getDateFin(){return dateFin;} public void setDateFin(LocalDate v){dateFin=v;}
    public int getJours(){return jours;} public void setJours(int v){jours=v;}
    public String getStatut(){return statut;} public void setStatut(String v){statut=v;}
    public String getMotif(){return motif;} public void setMotif(String v){motif=v;}
    public String getCommentaireManager(){return commentaireManager;} public void setCommentaireManager(String v){commentaireManager=v;}
    public String getCommentaireRh(){return commentaireRh;} public void setCommentaireRh(String v){commentaireRh=v;}
    public LocalDateTime getDateCreation(){return dateCreation;} public LocalDateTime getDateDecisionManager(){return dateDecisionManager;} public void setDateDecisionManager(LocalDateTime v){dateDecisionManager=v;}
    public LocalDateTime getDateDecisionRh(){return dateDecisionRh;} public void setDateDecisionRh(LocalDateTime v){dateDecisionRh=v;}
    public Employe getRemplacant(){return remplacant;} public void setRemplacant(Employe v){remplacant=v;}
}
