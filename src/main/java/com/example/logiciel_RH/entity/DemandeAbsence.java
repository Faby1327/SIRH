package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import jakarta.persistence.*;

@Entity @Table(name="demande_absence")
public class DemandeAbsence {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="employe_id") private Employe employe;
    @ManyToOne @JoinColumn(name="remplacant_id") private Employe remplacant;
    @ManyToOne(optional=false) @JoinColumn(name="declaree_par_id") private Utilisateur declareePar;
    @Column(name="date_debut", nullable=false) private LocalDate dateDebut;
    @Column(name="date_fin", nullable=false) private LocalDate dateFin;
    @Column(nullable=false) private String motif;
    @Column(name="justificatif_url") private String justificatifUrl;
    @Column(nullable=false) private String statut="A_TRANSMETTRE_RH";
    @Column(nullable=false) private boolean justifiee;
    @Column(name="commentaire_manager",columnDefinition="text") private String commentaireManager;
    @Column(name="commentaire_rh",columnDefinition="text") private String commentaireRh;
    @Column(nullable=false,name="date_creation") private LocalDateTime dateCreation=LocalDateTime.now();
    @Column(name="date_decision_rh") private LocalDateTime dateDecisionRh;
    public Long getId(){return id;} public Employe getEmploye(){return employe;} public void setEmploye(Employe v){employe=v;}
    public Employe getRemplacant(){return remplacant;} public void setRemplacant(Employe v){remplacant=v;}
    public Utilisateur getDeclareePar(){return declareePar;} public void setDeclareePar(Utilisateur v){declareePar=v;}
    public LocalDate getDateDebut(){return dateDebut;} public void setDateDebut(LocalDate v){dateDebut=v;}
    public LocalDate getDateFin(){return dateFin;} public void setDateFin(LocalDate v){dateFin=v;}
    public long getJours(){return dateDebut==null||dateFin==null||dateFin.isBefore(dateDebut)?0:ChronoUnit.DAYS.between(dateDebut,dateFin)+1;}
    public String getMotif(){return motif;} public void setMotif(String v){motif=v;}
    public String getJustificatifUrl(){return justificatifUrl;} public void setJustificatifUrl(String v){justificatifUrl=v;}
    public String getStatut(){return statut;} public void setStatut(String v){statut=v;}
    public boolean isJustifiee(){return justifiee;} public void setJustifiee(boolean v){justifiee=v;}
    public String getCommentaireManager(){return commentaireManager;} public void setCommentaireManager(String v){commentaireManager=v;}
    public String getCommentaireRh(){return commentaireRh;} public void setCommentaireRh(String v){commentaireRh=v;}
    public LocalDateTime getDateCreation(){return dateCreation;} public LocalDateTime getDateDecisionRh(){return dateDecisionRh;} public void setDateDecisionRh(LocalDateTime v){dateDecisionRh=v;}
}
