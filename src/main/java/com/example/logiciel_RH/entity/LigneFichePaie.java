package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "ligne_fiche_paie", uniqueConstraints = @UniqueConstraint(
        name = "uq_ligne_fiche_heure_supp",
        columnNames = {"fiche_paie_id", "heure_supplementaire_id"}))
public class LigneFichePaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fiche_paie_id", nullable = false)
    private FichePaie fichePaie;

    @Column(name = "type_ligne", nullable = false)
    private String typeLigne = "PRIME";

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    private String source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heure_supplementaire_id")
    private HeureSupplementaire heureSupplementaire;

    @Column(name = "nombre_minutes")
    private Integer nombreMinutes;

    @Column(name = "taux_majoration", precision = 5, scale = 2)
    private BigDecimal tauxMajoration;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FichePaie getFichePaie() { return fichePaie; }
    public void setFichePaie(FichePaie fichePaie) { this.fichePaie = fichePaie; }
    public String getTypeLigne() { return typeLigne; }
    public void setTypeLigne(String typeLigne) { this.typeLigne = typeLigne; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public HeureSupplementaire getHeureSupplementaire() { return heureSupplementaire; }
    public void setHeureSupplementaire(HeureSupplementaire heureSupplementaire) { this.heureSupplementaire = heureSupplementaire; }
    public Integer getNombreMinutes() { return nombreMinutes; }
    public void setNombreMinutes(Integer nombreMinutes) { this.nombreMinutes = nombreMinutes; }
    public BigDecimal getTauxMajoration() { return tauxMajoration; }
    public void setTauxMajoration(BigDecimal tauxMajoration) { this.tauxMajoration = tauxMajoration; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
