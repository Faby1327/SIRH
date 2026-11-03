package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "entretien")
public class Entretien {

    public static final String TYPE_ENTRETIEN_RH = "ENTRETIEN_RH";
    public static final String TYPE_ENTRETIEN_TECHNIQUE = "ENTRETIEN_TECHNIQUE";
    public static final String TYPE_TEST_PRACTIQUE = "TEST_PRACTIQUE";

    public static final String STATUT_PLANIFIE = "PLANIFIE";
    public static final String STATUT_TERMINE = "TERMINE";
    public static final String STATUT_ANNULE = "ANNULE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;

    @Column(name = "type_evaluation", nullable = false, length = 20)
    private String typeEvaluation;

    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Column(name = "lieu", nullable = false, length = 250)
    private String lieu;

    @Column(name = "consigne", columnDefinition = "TEXT")
    private String consigne;

    @Column(name = "statut", nullable = false, length = 10)
    private String statut = STATUT_PLANIFIE;

    @ManyToOne
    @JoinColumn(name = "planifie_par", nullable = false)
    private Utilisateur planifiePar;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntretienEvaluateur> evaluationsDesignees = new ArrayList<>();

    @OneToMany(mappedBy = "entretien", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations = new ArrayList<>();

    public Entretien() {
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

    public String getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(String typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getConsigne() {
        return consigne;
    }

    public void setConsigne(String consigne) {
        this.consigne = consigne;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Utilisateur getPlanifiePar() {
        return planifiePar;
    }

    public void setPlanifiePar(Utilisateur planifiePar) {
        this.planifiePar = planifiePar;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public List<EntretienEvaluateur> getEvaluationsDesignees() {
        return evaluationsDesignees;
    }

    public void setEvaluationsDesignees(List<EntretienEvaluateur> evaluationsDesignees) {
        this.evaluationsDesignees = evaluationsDesignees;
    }

    public List<Evaluation> getEvaluations() {
        return evaluations;
    }

    public void setEvaluations(List<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public String getDateHeureFormatee() {
        return dateHeure == null ? "" : dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getTypeEvaluationAffiche() {
        if (TYPE_ENTRETIEN_RH.equals(typeEvaluation)) {
            return "Entretien RH";
        }
        if (TYPE_ENTRETIEN_TECHNIQUE.equals(typeEvaluation)) {
            return "Entretien technique";
        }
        if (TYPE_TEST_PRACTIQUE.equals(typeEvaluation)) {
            return "Test pratique";
        }
        return typeEvaluation == null ? "" : typeEvaluation;
    }

    public String getStatutAffiche() {
        if (STATUT_TERMINE.equals(statut)) {
            return "Terminé";
        }
        if (STATUT_ANNULE.equals(statut)) {
            return "Annulé";
        }
        if (STATUT_PLANIFIE.equals(statut)) {
            return "Planifié";
        }
        return statut == null ? "" : statut;
    }
}
