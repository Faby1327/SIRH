package com.example.logiciel_RH.entity;

import java.time.LocalTime;

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

@Entity
@Table(name = "detail_jour_travail")
public class DetailJourTravail {

    public enum TypeDetail { MISSION, PAUSE, REUNION, FORMATION, TACHE_INTERNE, AUTRE }
    public enum Statut { A_VERIFIER, VALIDE, REFUSE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "jour_travail_id", nullable = false)
    private JourTravail jourTravail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_mission_id")
    private ParticipationMission participationMission;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_detail", nullable = false)
    private TypeDetail typeDetail;

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(name = "nombre_minutes", nullable = false)
    private Integer nombreMinutes;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.A_VERIFIER;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public JourTravail getJourTravail() { return jourTravail; }
    public void setJourTravail(JourTravail jourTravail) { this.jourTravail = jourTravail; }
    public ParticipationMission getParticipationMission() { return participationMission; }
    public void setParticipationMission(ParticipationMission participationMission) { this.participationMission = participationMission; }
    public TypeDetail getTypeDetail() { return typeDetail; }
    public void setTypeDetail(TypeDetail typeDetail) { this.typeDetail = typeDetail; }
    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }
    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }
    public Integer getNombreMinutes() { return nombreMinutes; }
    public void setNombreMinutes(Integer nombreMinutes) { this.nombreMinutes = nombreMinutes; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
