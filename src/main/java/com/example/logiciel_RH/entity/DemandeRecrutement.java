package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "demande_recrutement")
public class DemandeRecrutement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idDemande;

    @ManyToOne
    @JoinColumn(name = "poste_id")
    private Poste poste;

    @Transient
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "demandeur_id")
    private Utilisateur manager;

    @Column(name = "nombre_personnes")
    private Integer nombrePersonnes;

    @Column(name = "salaire_mensuel_estime")
    private BigDecimal salaireMensuelEstime;

    @Column(name = "cout_total_estime")
    private BigDecimal budgetDemande;

    @Column(name = "motif")
    private String motif;

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @Column(name = "type_recrutement")
    private String typeRecrutement;

    @Column(name = "date_souhaitee")
    private LocalDate dateSouhaitee;

    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statutActuel;

    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification;

    public DemandeRecrutement() {
    }

    public Long getIdDemande() {
        return idDemande;
    }

    public void setIdDemande(Long idDemande) {
        this.idDemande = idDemande;
    }

    public Poste getPoste() {
        return poste;
    }

    public void setPoste(Poste poste) {
        this.poste = poste;
    }

    public Departement getDepartement() {
        return departement != null ? departement : (poste == null ? null : poste.getDepartement());
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Utilisateur getManager() {
        return manager;
    }

    public void setManager(Utilisateur manager) {
        this.manager = manager;
    }

    public Integer getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(Integer nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public BigDecimal getSalaireMensuelEstime() {
        return salaireMensuelEstime;
    }

    public void setSalaireMensuelEstime(BigDecimal salaireMensuelEstime) {
        this.salaireMensuelEstime = salaireMensuelEstime;
    }

    public BigDecimal getBudgetDemande() {
        return budgetDemande;
    }

    public void setBudgetDemande(BigDecimal budgetDemande) {
        this.budgetDemande = budgetDemande;
    }

    public BigDecimal getBudgetTotalEstime() {
        if (budgetDemande == null || nombrePersonnes == null) {
            return null;
        }
        return budgetDemande.multiply(BigDecimal.valueOf(nombrePersonnes.longValue()));
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public LocalDate getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDate dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getDateDemandeFormatee() {
        return dateDemande == null ? "" : dateDemande.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getTypeRecrutement() {
        return typeRecrutement;
    }

    public void setTypeRecrutement(String typeRecrutement) {
        this.typeRecrutement = typeRecrutement;
    }

    public String getTypeRecrutementAffiche() {
        if ("CREATION".equals(typeRecrutement)) return "Création de poste";
        if ("REMPLACEMENT".equals(typeRecrutement)) return "Remplacement";
        return typeRecrutement == null ? "" : typeRecrutement;
    }

    public LocalDate getDateSouhaitee() {
        return dateSouhaitee;
    }

    public void setDateSouhaitee(LocalDate dateSouhaitee) {
        this.dateSouhaitee = dateSouhaitee;
    }

    public String getDateSouhaiteeFormatee() {
        return dateSouhaitee == null ? "" : dateSouhaitee.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    /** Titre proposé par défaut lors de la publication de l'offre d'emploi. */
    public String getOffreTitreDefaut() {
        String intitule = getPoste() == null || getPoste().getIntitule() == null
                ? "Poste à pourvoir"
                : getPoste().getIntitule();
        int personnes = nombrePersonnes == null ? 1 : nombrePersonnes;
        return personnes > 1
                ? intitule + " (" + personnes + " postes)"
                : intitule;
    }

    /** Description proposée par défaut lors de la publication de l'offre d'emploi. */
    public String getOffreDescriptionDefaut() {
        int personnes = nombrePersonnes == null ? 1 : nombrePersonnes;
        StringBuilder texte = new StringBuilder();
        if (getDepartement() != null && getDepartement().getNom() != null) {
            texte.append("Le service ").append(getDepartement().getNom()).append(" recrute ");
        } else {
            texte.append("Nous recrutons ");
        }
        texte.append(personnes).append(personnes > 1 ? " personnes" : " personne");
        if (getPoste() != null && getPoste().getIntitule() != null) {
            texte.append(" pour le poste de ").append(getPoste().getIntitule());
        }
        texte.append(" en ").append("REMPLACEMENT".equals(typeRecrutement) ? "remplacement" : "création de poste");
        texte.append(".");
        if (motif != null && !motif.isBlank()) {
            texte.append("\n\nMotif : ").append(motif.trim());
        }
        if (dateSouhaitee != null) {
            texte.append("\nPrise de poste souhaitée : ").append(getDateSouhaiteeFormatee()).append(".");
        }
        return texte.toString();
    }

    public Statut getStatutActuel() {
        return statutActuel;
    }

    public void setStatutActuel(Statut statutActuel) {
        this.statutActuel = statutActuel;
    }

    public LocalDateTime getDateModification() {
        return dateModification;
    }

    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
}
