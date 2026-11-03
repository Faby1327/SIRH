package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "poste")
public class Poste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idPoste;

    @Column(name = "intitule")
    private String intitule;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    public Poste() {
    }

    public Long getIdPoste() {
        return idPoste;
    }

    public void setIdPoste(Long idPoste) {
        this.idPoste = idPoste;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}
