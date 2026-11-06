package com.example.logiciel_RH.entity;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "modele_horaire")
public class ModeleHoraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "pause_prevue_minutes", nullable = false)
    private Integer pausePrevueMinutes;

    @Column(name = "tolerance_retard_minutes", nullable = false)
    private Integer toleranceRetardMinutes;

    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Integer getPausePrevueMinutes() { return pausePrevueMinutes; }
    public void setPausePrevueMinutes(Integer pausePrevueMinutes) { this.pausePrevueMinutes = pausePrevueMinutes; }
    public Integer getToleranceRetardMinutes() { return toleranceRetardMinutes; }
    public void setToleranceRetardMinutes(Integer toleranceRetardMinutes) { this.toleranceRetardMinutes = toleranceRetardMinutes; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
