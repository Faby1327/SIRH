package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "evaluation_note",
        uniqueConstraints = @UniqueConstraint(columnNames = {"evaluation_id", "critere_id"}))
public class EvaluationNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "critere_id", nullable = false)
    private CritereEvaluation critere;

    @Column(name = "note", nullable = false)
    private Integer note;

    public EvaluationNote() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public CritereEvaluation getCritere() {
        return critere;
    }

    public void setCritere(CritereEvaluation critere) {
        this.critere = critere;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }
}
