package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.ManyToOne;
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


    // ---- Bloc 5 : dossier complet (nom, prénom, téléphone, email et date de
    // naissance restent portés par Personne) ----
    @Column(name = "lieu_naissance")
    private String lieuNaissance;

    @Column(name = "sexe")
    private String sexe;

    @Column(name = "nationalite")
    private String nationalite;

    @Column(name = "cin_numero")
    private String cinNumero;

    @Column(name = "cin_date_delivrance")
    private LocalDate cinDateDelivrance;

    @Column(name = "cin_lieu_delivrance")
    private String cinLieuDelivrance;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "commune")
    private String commune;

    @Column(name = "region")
    private String region;

    @Column(name = "situation_matrimoniale")
    private String situationMatrimoniale;

    @Column(name = "nombre_enfants", nullable = false)
    private int nombreEnfants;

    @Column(name = "contact_urgence_nom")
    private String contactUrgenceNom;

    @Column(name = "contact_urgence_relation")
    private String contactUrgenceRelation;

    @Column(name = "contact_urgence_telephone")
    private String contactUrgenceTelephone;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @ManyToOne
    @JoinColumn(name = "superieur_id")
    private Employe superieur;

    @Column(name = "salaire_actuel")
    private BigDecimal salaireActuel;

    @Column(name = "photo")
    private String photo;

    /** Aligne poste, département et salaire du dossier sur le contrat en cours. */
    public void appliquerContrat(Contrat contrat) {
        if (contrat == null) return;
        this.poste = contrat.getPoste();
        this.departement = contrat.getPoste() == null ? null : contrat.getPoste().getDepartement();
        this.salaireActuel = contrat.getSalaireMensuel();
    }

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
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getCommune() { return commune; }
    public void setCommune(String commune) { this.commune = commune; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getSituationMatrimoniale() { return situationMatrimoniale; }
    public void setSituationMatrimoniale(String situationMatrimoniale) { this.situationMatrimoniale = situationMatrimoniale; }
    public int getNombreEnfants() { return nombreEnfants; }
    public void setNombreEnfants(int nombreEnfants) { this.nombreEnfants = nombreEnfants; }
    public String getContactUrgenceNom() { return contactUrgenceNom; }
    public void setContactUrgenceNom(String contactUrgenceNom) { this.contactUrgenceNom = contactUrgenceNom; }
    public String getContactUrgenceRelation() { return contactUrgenceRelation; }
    public void setContactUrgenceRelation(String contactUrgenceRelation) { this.contactUrgenceRelation = contactUrgenceRelation; }
    public String getContactUrgenceTelephone() { return contactUrgenceTelephone; }
    public void setContactUrgenceTelephone(String contactUrgenceTelephone) { this.contactUrgenceTelephone = contactUrgenceTelephone; }
    public Departement getDepartement() { return departement; }
    public void setDepartement(Departement departement) { this.departement = departement; }
    public Poste getPoste() { return poste; }
    public void setPoste(Poste poste) { this.poste = poste; }
    public Employe getSuperieur() { return superieur; }
    public void setSuperieur(Employe superieur) { this.superieur = superieur; }
    public BigDecimal getSalaireActuel() { return salaireActuel; }
    public void setSalaireActuel(BigDecimal salaireActuel) { this.salaireActuel = salaireActuel; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
