package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "affectation_horaire")
public class AffectationHoraire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "regime_travail_id", nullable = false)
    private RegimeTravail regimeTravail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "modele_horaire_id", nullable = false)
    private ModeleHoraire modeleHoraire;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(nullable = false)
    private boolean actif = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "affectation_horaire_jour", joinColumns = @JoinColumn(name = "affectation_horaire_id"))
    @Column(name = "jour_semaine", nullable = false)
    private Set<Integer> joursSemaine = new LinkedHashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public RegimeTravail getRegimeTravail() { return regimeTravail; }
    public void setRegimeTravail(RegimeTravail regimeTravail) { this.regimeTravail = regimeTravail; }
    public ModeleHoraire getModeleHoraire() { return modeleHoraire; }
    public void setModeleHoraire(ModeleHoraire modeleHoraire) { this.modeleHoraire = modeleHoraire; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
    public Set<Integer> getJoursSemaine() { return joursSemaine; }
    public void setJoursSemaine(Set<Integer> joursSemaine) { this.joursSemaine = joursSemaine; }
}
