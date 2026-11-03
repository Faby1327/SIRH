package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
@Table(name = "evaluation")
public class Evaluation {

    public static final String AVIS_FAVORABLE = "FAVORABLE";
    public static final String AVIS_RESERVE = "RESERVE";
    public static final String AVIS_DEFAVORABLE = "DEFAVORABLE";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entretien_id", nullable = false)
    private Entretien entretien;

    @ManyToOne
    @JoinColumn(name = "evaluateur_id", nullable = false)
    private Utilisateur evaluateur;

    @Column(name = "recommandation", nullable = false, length = 12)
    private String recommandation;

    @Column(name = "commentaire", nullable = false, columnDefinition = "TEXT")
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false)
    private LocalDateTime dateEvaluation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL)
    private List<EvaluationNote> notes = new ArrayList<>();

    public Evaluation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entretien getEntretien() {
        return entretien;
    }

    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }

    public Utilisateur getEvaluateur() {
        return evaluateur;
    }

    public void setEvaluateur(Utilisateur evaluateur) {
        this.evaluateur = evaluateur;
    }

    public String getRecommandation() {
        return recommandation;
    }

    public void setRecommandation(String recommandation) {
        this.recommandation = recommandation;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public LocalDateTime getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(LocalDateTime dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }

    public List<EvaluationNote> getNotes() {
        return notes;
    }

    public void setNotes(List<EvaluationNote> notes) {
        this.notes = notes;
    }

    public String getRecommandationAffichee() {
        if (AVIS_FAVORABLE.equals(recommandation)) {
            return "Favorable";
        }
        if (AVIS_RESERVE.equals(recommandation)) {
            return "Réservé";
        }
        if (AVIS_DEFAVORABLE.equals(recommandation)) {
            return "Défavorable";
        }
        return recommandation == null ? "" : recommandation;
    }

    public String getDateEvaluationFormatee() {
        return dateEvaluation == null ? "" : dateEvaluation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Map<Long, Integer> getNotesParCritere() {
        Map<Long, Integer> resultat = new LinkedHashMap<>();
        for (EvaluationNote note : notes) {
            if (note.getCritere() != null) {
                resultat.put(note.getCritere().getId(), note.getNote());
            }
        }
        return resultat;
    }

    public Double getMoyenne() {
        if (notes.isEmpty()) {
            return null;
        }
        double total = 0;
        for (EvaluationNote note : notes) {
            total += note.getNote();
        }
        return Math.round((total / notes.size()) * 10.0) / 10.0;
    }
}
