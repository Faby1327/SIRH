package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "historique_situation_personnelle")
public class HistoriqueSituationPersonnelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "email_personnel")
    private String emailPersonnel;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "commune")
    private String commune;

    @Column(name = "region")
    private String region;

    @Column(name = "situation_matrimoniale")
    private String situationMatrimoniale;

    @Column(name = "nombre_enfants")
    private Integer nombreEnfants;

    @Column(name = "contact_urgence_nom")
    private String contactUrgenceNom;

    @Column(name = "contact_urgence_relation")
    private String contactUrgenceRelation;

    @Column(name = "contact_urgence_telephone")
    private String contactUrgenceTelephone;

    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;

    @Column(name = "date_enregistrement", nullable = false)
    private LocalDateTime dateEnregistrement;

    @PrePersist
    void avantInsertion() {
        if (dateEnregistrement == null) dateEnregistrement = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getEmailPersonnel() { return emailPersonnel; }
    public void setEmailPersonnel(String emailPersonnel) { this.emailPersonnel = emailPersonnel; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getSituationMatrimoniale() { return situationMatrimoniale; }
    public void setSituationMatrimoniale(String s) { this.situationMatrimoniale = s; }
    public Integer getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(Integer nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public String getContactUrgenceNom() { return contactUrgenceNom; }
    public void setContactUrgenceNom(String nom) { this.contactUrgenceNom = nom; }
    public String getContactUrgenceRelation() { return contactUrgenceRelation; }
    public void setContactUrgenceRelation(String r) { this.contactUrgenceRelation = r; }
    public String getContactUrgenceTelephone() { return contactUrgenceTelephone; }
    public void setContactUrgenceTelephone(String t) { this.contactUrgenceTelephone = t; }
    public LocalDate getDateEffet() { return dateEffet; }
    public void setDateEffet(LocalDate dateEffet) { this.dateEffet = dateEffet; }
    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; }
    public void setDateEnregistrement(LocalDateTime d) { this.dateEnregistrement = d; }
}