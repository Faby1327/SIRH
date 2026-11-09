package com.example.logiciel_RH.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historique_employe")
public class HistoriqueEmploye {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "employe_id", nullable = false) private Employe employe;
    @ManyToOne(optional = false) @JoinColumn(name = "poste_id", nullable = false) private Poste poste;
    @ManyToOne(optional = false) @JoinColumn(name = "departement_id", nullable = false) private Departement departement;
    @ManyToOne @JoinColumn(name = "superieur_id") private Employe superieur;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal salaire;
    @Column(name = "date_effet", nullable = false) private LocalDate dateEffet;
    @Column(name = "date_enregistrement", nullable = false) private LocalDateTime dateEnregistrement;
    @PrePersist void avantInsertion() { if (dateEnregistrement == null) dateEnregistrement = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Employe getEmploye() { return employe; } public void setEmploye(Employe v) { employe = v; }
    public Poste getPoste() { return poste; } public void setPoste(Poste v) { poste = v; }
    public Departement getDepartement() { return departement; } public void setDepartement(Departement v) { departement = v; }
    public Employe getSuperieur() { return superieur; } public void setSuperieur(Employe v) { superieur = v; }
    public BigDecimal getSalaire() { return salaire; } public void setSalaire(BigDecimal v) { salaire = v; }
    public LocalDate getDateEffet() { return dateEffet; } public void setDateEffet(LocalDate v) { dateEffet = v; }
    public LocalDateTime getDateEnregistrement() { return dateEnregistrement; } public void setDateEnregistrement(LocalDateTime v) { dateEnregistrement = v; }
}
