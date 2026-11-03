package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employe")
public class Employe {

    public enum Statut {
        ACTIF, SUSPENDU, SORTI
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "personne_id", nullable = false, unique = true)
    private Personne personne;

    @Column(name = "matricule", nullable = false, unique = true)
    private String matricule;

    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut = Statut.ACTIF;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
