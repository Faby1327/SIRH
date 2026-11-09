package com.example.logiciel_RH.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historique_situation_personnelle")
public class HistoriqueSituationPersonnelle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "employe_id", nullable = false) private Employe employe;
    private String telephone;
    @Column(name = "email_personnel") private String emailPersonnel;
    private String adresse;
    private String commune;
    private String region;
    @Column(name = "situation_matrimoniale") private String situationMatrimoniale;
    @Column(name = "nombre_enfants") private Integer nombreEnfants;
    @Column(name = "contact_urgence_nom") private String contactUrgenceNom;
    @Column(name = "contact_urgence_relation") private String contactUrgenceRelation;
    @Column(name = "contact_urgence_telephone") private String contactUrgenceTelephone;
    @Column(name = "date_effet", nullable = false) private LocalDate dateEffet;
    @Column(name = "date_enregistrement", nullable = false) private LocalDateTime dateEnregistrement;
    @PrePersist void avantInsertion() { if (dateEnregistrement == null) dateEnregistrement = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; } public void setEmploye(Employe v) { employe = v; }
    public String getTelephone() { return telephone; } public void setTelephone(String v) { telephone = v; }
    public String getEmailPersonnel() { return emailPersonnel; } public void setEmailPersonnel(String v) { emailPersonnel = v; }
    public String getAdresse() { return adresse; } public void setAdresse(String v) { adresse = v; }
    public String getCommune() { return commune; } public void setCommune(String v) { commune = v; }
    public String getRegion() { return region; } public void setRegion(String v) { region = v; }
    public String getSituationMatrimoniale() { return situationMatrimoniale; } public void setSituationMatrimoniale(String v) { situationMatrimoniale = v; }
    public Integer getNombreEnfants() { return nombreEnfants; } public void setNombreEnfants(Integer v) { nombreEnfants = v; }
    public String getContactUrgenceNom() { return contactUrgenceNom; } public void setContactUrgenceNom(String v) { contactUrgenceNom = v; }
    public String getContactUrgenceRelation() { return contactUrgenceRelation; } public void setContactUrgenceRelation(String v) { contactUrgenceRelation = v; }
    public String getContactUrgenceTelephone() { return contactUrgenceTelephone; } public void setContactUrgenceTelephone(String v) { contactUrgenceTelephone = v; }
    public LocalDate getDateEffet() { return dateEffet; } public void setDateEffet(LocalDate v) { dateEffet = v; }
    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; } public void setDateEnregistrement(LocalDateTime v) { dateEnregistrement = v; }
}
