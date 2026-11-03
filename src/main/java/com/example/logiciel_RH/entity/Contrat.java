package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "contrat")
public class Contrat {

    public enum Type { CE, CDD, CDI }
    public enum Statut { EN_COURS, TERMINE, ANNULE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "proposition_id")
    private PropositionEmbauche proposition;

    @ManyToOne
    @JoinColumn(name = "poste_id", nullable = false)
    private Poste poste;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contrat", nullable = false)
    private Type typeContrat;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "salaire_mensuel", nullable = false, precision = 12, scale = 2)
    private BigDecimal salaireMensuel;

    @Column(name = "devise", nullable = false)
    private String devise = "Ar";

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private Statut statut = Statut.EN_COURS;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; }
    public void setEmploye(Employe employe) { this.employe = employe; }
    public PropositionEmbauche getProposition() { return proposition; }
    public void setProposition(PropositionEmbauche proposition) { this.proposition = proposition; }
    public Poste getPoste() { return poste; }
    public void setPoste(Poste poste) { this.poste = poste; }
    public Type getTypeContrat() { return typeContrat; }
    public void setTypeContrat(Type typeContrat) { this.typeContrat = typeContrat; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public BigDecimal getSalaireMensuel() { return salaireMensuel; }
    public void setSalaireMensuel(BigDecimal salaireMensuel) { this.salaireMensuel = salaireMensuel; }
    public String getDevise() { return devise; }
    public void setDevise(String devise) { this.devise = devise; }
    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }
}
