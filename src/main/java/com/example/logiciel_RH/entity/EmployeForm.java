package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class EmployeForm {
    private String nom, prenom, lieuNaissance, sexe, nationalite, cinNumero, cinLieuDelivrance;
    private String telephone, emailPersonnel, adresse, commune, region, situationMatrimoniale;
    private String contactUrgenceNom, contactUrgenceRelation, contactUrgenceTelephone, statut, motDePasse;
    private Integer nombreEnfants;
    private Long departementId, posteId, superieurId;
    private BigDecimal salaireActuel;
    private boolean creerCompte;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) private LocalDate dateNaissance, cinDateDelivrance, dateEmbauche, dateDepart;

    public String getNom(){return nom;} public void setNom(String v){nom=v;} public String getPrenom(){return prenom;} public void setPrenom(String v){prenom=v;}
    public LocalDate getDateNaissance(){return dateNaissance;} public void setDateNaissance(LocalDate v){dateNaissance=v;} public String getLieuNaissance(){return lieuNaissance;} public void setLieuNaissance(String v){lieuNaissance=v;}
    public String getSexe(){return sexe;} public void setSexe(String v){sexe=v;} public String getNationalite(){return nationalite;} public void setNationalite(String v){nationalite=v;}
    public String getCinNumero(){return cinNumero;} public void setCinNumero(String v){cinNumero=v;} public LocalDate getCinDateDelivrance(){return cinDateDelivrance;} public void setCinDateDelivrance(LocalDate v){cinDateDelivrance=v;}
    public String getCinLieuDelivrance(){return cinLieuDelivrance;} public void setCinLieuDelivrance(String v){cinLieuDelivrance=v;} public String getTelephone(){return telephone;} public void setTelephone(String v){telephone=v;}
    public String getEmailPersonnel(){return emailPersonnel;} public void setEmailPersonnel(String v){emailPersonnel=v;} public String getAdresse(){return adresse;} public void setAdresse(String v){adresse=v;}
    public String getCommune(){return commune;} public void setCommune(String v){commune=v;} public String getRegion(){return region;} public void setRegion(String v){region=v;}
    public String getSituationMatrimoniale(){return situationMatrimoniale;} public void setSituationMatrimoniale(String v){situationMatrimoniale=v;} public Integer getNombreEnfants(){return nombreEnfants;} public void setNombreEnfants(Integer v){nombreEnfants=v;}
    public String getContactUrgenceNom(){return contactUrgenceNom;} public void setContactUrgenceNom(String v){contactUrgenceNom=v;} public String getContactUrgenceRelation(){return contactUrgenceRelation;} public void setContactUrgenceRelation(String v){contactUrgenceRelation=v;}
    public String getContactUrgenceTelephone(){return contactUrgenceTelephone;} public void setContactUrgenceTelephone(String v){contactUrgenceTelephone=v;} public Long getDepartementId(){return departementId;} public void setDepartementId(Long v){departementId=v;}
    public Long getPosteId(){return posteId;} public void setPosteId(Long v){posteId=v;} public Long getSuperieurId(){return superieurId;} public void setSuperieurId(Long v){superieurId=v;}
    public LocalDate getDateEmbauche(){return dateEmbauche;} public void setDateEmbauche(LocalDate v){dateEmbauche=v;} public BigDecimal getSalaireActuel(){return salaireActuel;} public void setSalaireActuel(BigDecimal v){salaireActuel=v;}
    public String getStatut(){return statut;} public void setStatut(String v){statut=v;} public LocalDate getDateDepart(){return dateDepart;} public void setDateDepart(LocalDate v){dateDepart=v;}
    public boolean isCreerCompte(){return creerCompte;} public void setCreerCompte(boolean v){creerCompte=v;} public String getMotDePasse(){return motDePasse;} public void setMotDePasse(String v){motDePasse=v;}
}
