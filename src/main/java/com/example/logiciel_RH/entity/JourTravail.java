package com.example.logiciel_RH.entity;

import java.time.LocalDate;
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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "jour_travail", uniqueConstraints = @UniqueConstraint(name = "uq_jour_travail_employe_date", columnNames = {"employe_id", "date_travail"}))
public class JourTravail {

    public enum Statut { PLANIFIEE, EN_COURS, A_VERIFIER, VALIDEE, ABSENCE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affectation_horaire_id")
    private AffectationHoraire affectationHoraire;

    @Column(name = "date_travail", nullable = false)
    private LocalDate dateTravail;

    @Column(name = "heure_debut_prevue")
    private LocalTime heureDebutPrevue;

    @Column(name = "heure_fin_prevue")
    private LocalTime heureFinPrevue;

    @Column(name = "pause_prevue_minutes", nullable = false)
    private Integer pausePrevueMinutes = 0;

    @Column(name = "heure_entree_reelle")
    private LocalTime heureEntreeReelle;

    @Column(name = "heure_sortie_reelle")
    private LocalTime heureSortieReelle;

    @Column(name = "minutes_travaillees")
    private Integer minutesTravaillees;

    @Column(name = "minutes_retard", nullable = false)
    private Integer minutesRetard = 0;

    @Column(name = "minutes_supplementaires", nullable = false)
    private Integer minutesSupplementaires = 0;

    @Column(nullable = false)
    private String statut = "PLANIFIEE";

    private String commentaire;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public Contrat getContrat() { return contrat; }
    public void setContrat(Contrat contrat) { this.contrat = contrat; }
    public AffectationHoraire getAffectationHoraire() { return affectationHoraire; }
    public void setAffectationHoraire(AffectationHoraire affectationHoraire) { this.affectationHoraire = affectationHoraire; }
    public LocalDate getDateTravail() { return dateTravail; }
    public void setDateTravail(LocalDate dateTravail) { this.dateTravail = dateTravail; }
    public LocalTime getHeureDebutPrevue() { return heureDebutPrevue; }
    public void setHeureDebutPrevue(LocalTime heureDebutPrevue) { this.heureDebutPrevue = heureDebutPrevue; }
    public LocalTime getHeureFinPrevue() { return heureFinPrevue; }
    public void setHeureFinPrevue(LocalTime heureFinPrevue) { this.heureFinPrevue = heureFinPrevue; }
    public Integer getPausePrevueMinutes() { return pausePrevueMinutes; }
    public void setPausePrevueMinutes(Integer pausePrevueMinutes) { this.pausePrevueMinutes = pausePrevueMinutes; }
    public LocalTime getHeureEntreeReelle() { return heureEntreeReelle; }
    public void setHeureEntreeReelle(LocalTime heureEntreeReelle) { this.heureEntreeReelle = heureEntreeReelle; }
    public LocalTime getHeureSortieReelle() { return heureSortieReelle; }
    public void setHeureSortieReelle(LocalTime heureSortieReelle) { this.heureSortieReelle = heureSortieReelle; }
    public Integer getMinutesTravaillees() { return minutesTravaillees; }
    public void setMinutesTravaillees(Integer minutesTravaillees) { this.minutesTravaillees = minutesTravaillees; }
    public Integer getMinutesRetard() { return minutesRetard; }
    public void setMinutesRetard(Integer minutesRetard) { this.minutesRetard = minutesRetard; }
    public Integer getMinutesSupplementaires() { return minutesSupplementaires; }
    public void setMinutesSupplementaires(Integer minutesSupplementaires) { this.minutesSupplementaires = minutesSupplementaires; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
