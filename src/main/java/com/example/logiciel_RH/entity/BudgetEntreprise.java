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
@Table(name = "budget_entreprise")
public class BudgetEntreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idBudget;

    @ManyToOne
    @JoinColumn(name = "departement_id", nullable = false)
    private Departement departement;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    @Column(name = "montant_disponible", nullable = false)
    private BigDecimal montantDisponible;

    public Long getIdBudget() {
        return idBudget;
    }

    public void setIdBudget(Long idBudget) {
        this.idBudget = idBudget;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public BigDecimal getMontantDisponible() {
        return montantDisponible;
    }

    public void setMontantDisponible(BigDecimal montantDisponible) {
        this.montantDisponible = montantDisponible;
    }
}
