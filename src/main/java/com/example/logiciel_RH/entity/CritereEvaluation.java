package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "critere_evaluation")
public class CritereEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "offre_id", nullable = false)
    private OffreEmploi offre;

    @Column(name = "libelle", nullable = false, length = 150)
    private String libelle;

    @Column(name = "ordre", nullable = false)
    private Integer ordre = 1;

    public CritereEvaluation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OffreEmploi getOffre() {
        return offre;
    }

    public void setOffre(OffreEmploi offre) {
        this.offre = offre;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public Integer getOrdre() {
        return ordre;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }
}
