package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regime_travail")
public class RegimeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String description;

    @Column(name = "heures_hebdomadaires", nullable = false)
    private java.math.BigDecimal heuresHebdomadaires;

    @Column(name = "jours_travailles_par_semaine", nullable = false)
    private Integer joursTravaillesParSemaine;

    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.math.BigDecimal getHeuresHebdomadaires() { return heuresHebdomadaires; }
    public void setHeuresHebdomadaires(java.math.BigDecimal heuresHebdomadaires) { this.heuresHebdomadaires = heuresHebdomadaires; }
    public Integer getJoursTravaillesParSemaine() { return joursTravaillesParSemaine; }
    public void setJoursTravaillesParSemaine(Integer joursTravaillesParSemaine) { this.joursTravaillesParSemaine = joursTravaillesParSemaine; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
