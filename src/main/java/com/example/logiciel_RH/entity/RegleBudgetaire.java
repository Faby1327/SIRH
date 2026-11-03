package com.example.logiciel_RH.entity;

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
@Table(name = "regle_budgetaire")
public class RegleBudgetaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idRegle;

    @ManyToOne
    @JoinColumn(name = "poste_id", referencedColumnName = "id")
    private Poste poste;

    @Column(name = "budget_min")
    private BigDecimal budgetMin;

    @Column(name = "budget_max")
    private BigDecimal budgetMax;

    public Long getIdRegle() {
        return idRegle;
    }

    public void setIdRegle(Long idRegle) {
        this.idRegle = idRegle;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public BigDecimal getBudgetMin() {
        return budgetMin;
    }

    public void setBudgetMin(BigDecimal budgetMin) {
        this.budgetMin = budgetMin;
    }

    public BigDecimal getBudgetMax() {
        return budgetMax;
    }

    public void setBudgetMax(BigDecimal budgetMax) {
        this.budgetMax = budgetMax;
    }
}
