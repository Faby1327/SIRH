package com.example.logiciel_RH.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

/** Données saisies dans les formulaires d'ajout et de modification d'un employé. */
public class EmployeForm {

    private String nom;
    private String prenom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String sexe;
    private String nationalite;
    private String cinNumero;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate cinDateDelivrance;
    private String cinLieuDelivrance;
    private String telephone;
    private String emailPersonnel;
    private String adresse;
    private String commune;
    private String region;
    private String situationMatrimoniale;
    private Integer nombreEnfants;
    private String contactUrgenceNom;
    private String contactUrgenceRelation;
    private String contactUrgenceTelephone;
    private Long departementId;
    private Long posteId;
    private Long superieurId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateEmbauche;
    private BigDecimal salaireActuel;
    private String statut;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDepart;
    private boolean creerCompte;
    private String motDePasse;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getLieuNaissance() { return lieuNaissance; }
    public void setLieuNaissance(String lieuNaissance) { this.lieuNaissance = lieuNaissance; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public String getCinNumero() { return cinNumero; }
    public void setCinNumero(String cinNumero) { this.cinNumero = cinNumero; }
    public LocalDate getCinDateDelivrance() { return cinDateDelivrance; }
    public void setCinDateDelivrance(LocalDate cinDateDelivrance) { this.cinDateDelivrance = cinDateDelivrance; }
    public String getCinLieuDelivrance() { return cinLieuDelivrance; }
    public void setCinLieuDelivrance(String cinLieuDelivrance) { this.cinLieuDelivrance = cinLieuDelivrance; }
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
    public void setSituationMatrimoniale(String situationMatrimoniale) { this.situationMatrimoniale = situationMatrimoniale; }
    public Integer getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(Integer nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public String getContactUrgenceNom() { return contactUrgenceNom; }
    public void setContactUrgenceNom(String contactUrgenceNom) { this.contactUrgenceNom = contactUrgenceNom; }
    public String getContactUrgenceRelation() { return contactUrgenceRelation; }
    public void setContactUrgenceRelation(String contactUrgenceRelation) { this.contactUrgenceRelation = contactUrgenceRelation; }
    public String getContactUrgenceTelephone() { return contactUrgenceTelephone; }
    public void setContactUrgenceTelephone(String contactUrgenceTelephone) { this.contactUrgenceTelephone = contactUrgenceTelephone; }
    public Long getDepartementId() { return departementId; }
    public void setDepartementId(Long departementId) { this.departementId = departementId; }
    public Long getPosteId() { return posteId; }
    public void setPosteId(Long posteId) { this.posteId = posteId; }
    public Long getSuperieurId() { return superieurId; }
    public void setSuperieurId(Long superieurId) { this.superieurId = superieurId; }
    public LocalDate getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDate dateEmbauche) { this.dateEmbauche = dateEmbauche; }
    public BigDecimal getSalaireActuel() { return salaireActuel; }
    public void setSalaireActuel(BigDecimal salaireActuel) { this.salaireActuel = salaireActuel; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public LocalDate getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDate dateDepart) { this.dateDepart = dateDepart; }
    public boolean isCreerCompte() { return creerCompte; }
    public void setCreerCompte(boolean creerCompte) { this.creerCompte = creerCompte; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
