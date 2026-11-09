package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "heure_supplementaire")
public class HeureSupplementaire {

    public enum Statut { DEMANDEE, VALIDEE, REFUSEE, ANNULEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jour_travail_id", nullable = false)
    private JourTravail jourTravail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(name = "date_travail", nullable = false)
    private LocalDate dateTravail;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "nombre_minutes", nullable = false)
    private Integer nombreMinutes;

    @Column(nullable = false)
    private String motif;

    @Column(nullable = false)
    private String statut = "DEMANDEE";

    @Column(name = "date_demande", nullable = false)
    private LocalDateTime dateDemande;

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public JourTravail getJourTravail() { return jourTravail; }
    public void setJourTravail(JourTravail jourTravail) { this.jourTravail = jourTravail; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public LocalDate getDateTravail() { return dateTravail; }
    public void setDateTravail(LocalDate dateTravail) { this.dateTravail = dateTravail; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Integer getNombreMinutes() { return nombreMinutes; }
    public void setNombreMinutes(Integer nombreMinutes) { this.nombreMinutes = nombreMinutes; }
    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDateTime getDateDemande() { return dateDemande; }
    public void setDateDemande(LocalDateTime dateDemande) { this.dateDemande = dateDemande; }
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
}
