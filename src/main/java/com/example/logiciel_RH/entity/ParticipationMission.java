package com.example.logiciel_RH.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "participation_mission", uniqueConstraints = @UniqueConstraint(name = "uq_participation_mission_employe", columnNames = {"mission_id", "employe_id"}))
public class ParticipationMission {

    public enum Statut { AFFECTEE, EN_COURS, TERMINEE, ANNULEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(name = "role", nullable = false)
    private String roleMission;

    @Column(name = "date_affectation", nullable = false)
    private LocalDate dateAffectation;

    @Column(nullable = false)
    private Integer avancement = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.AFFECTEE;

    @Column(name = "date_fin_reelle")
    private LocalDate dateFinReelle;

    @Column(name = "compte_rendu")
    private String compteRendu;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public String getRoleMission() { return roleMission; }
    public void setRoleMission(String roleMission) { this.roleMission = roleMission; }
    public LocalDate getDateAffectation() { return dateAffectation; }
    public void setDateAffectation(LocalDate dateAffectation) { this.dateAffectation = dateAffectation; }
    public Integer getAvancement() { return avancement; }
    public void setAvancement(Integer avancement) { this.avancement = avancement; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public LocalDate getDateFinReelle() { return dateFinReelle; }
    public void setDateFinReelle(LocalDate dateFinReelle) { this.dateFinReelle = dateFinReelle; }
    public String getCompteRendu() { return compteRendu; }
    public void setCompteRendu(String compteRendu) { this.compteRendu = compteRendu; }
}
