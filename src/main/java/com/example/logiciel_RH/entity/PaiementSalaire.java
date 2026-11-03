package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "paiement_salaire")
public class PaiementSalaire {

    public enum Statut { EFFECTUE, ECHEC, ANNULE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "fiche_paie_id", nullable = false, unique = true)
    private FichePaie fichePaie;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_paiement", nullable = false)
    private LocalDateTime datePaiement = LocalDateTime.now();

    @Column(name = "mode_paiement", nullable = false)
    private String modePaiement = "MANUEL";

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Statut statut = Statut.EFFECTUE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public FichePaie getFichePaie() { return fichePaie; }
    public void setFichePaie(FichePaie fichePaie) { this.fichePaie = fichePaie; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
