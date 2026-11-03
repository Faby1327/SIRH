package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "fiche_paie",
       uniqueConstraints = @UniqueConstraint(name = "uq_fiche_paie_periode",
               columnNames = {"employe_id", "contrat_id", "mois", "annee"}))
public class FichePaie {

    public enum Statut { BROUILLON, VALIDEE, PAYEE, ANNULEE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "salaire_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "total_primes", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrimes = BigDecimal.ZERO;

    @Column(name = "total_retenues", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalRetenues = BigDecimal.ZERO;

    @Column(name = "net_a_payer", nullable = false, precision = 12, scale = 2)
    private BigDecimal netAPayer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.BROUILLON;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_validation")
    private LocalDateTime dateValidation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public Contrat getContrat() { return contrat; }
    public void setContrat(Contrat contrat) { this.contrat = contrat; }
    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public BigDecimal getSalaireBase() { return salaireBase; }
    public void setSalaireBase(BigDecimal salaireBase) { this.salaireBase = salaireBase; }
    public BigDecimal getTotalPrimes() { return totalPrimes; }
    public void setTotalPrimes(BigDecimal totalPrimes) { this.totalPrimes = totalPrimes; }
    public BigDecimal getTotalRetenues() { return totalRetenues; }
    public void setTotalRetenues(BigDecimal totalRetenues) { this.totalRetenues = totalRetenues; }
    public BigDecimal getNetAPayer() { return netAPayer; }
    public void setNetAPayer(BigDecimal netAPayer) { this.netAPayer = netAPayer; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    public LocalDateTime getDateValidation() { return dateValidation; }
    public void setDateValidation(LocalDateTime dateValidation) { this.dateValidation = dateValidation; }
}
