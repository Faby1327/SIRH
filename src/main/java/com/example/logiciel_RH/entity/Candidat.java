package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Profil de recrutement rattaché à une identité personne.
 */
@Entity
@Table(name = "candidat")
public class Candidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idCandidat;

    @OneToOne
    @JoinColumn(name = "personne_id", nullable = false, unique = true)
    private Personne personne;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    public Candidat() {
    }

    public Long getIdCandidat() {
        return idCandidat;
    }

    public void setIdCandidat(Long idCandidat) {
        this.idCandidat = idCandidat;
    }

    public Utilisateur getUtilisateur() {
        return personne == null ? null : personne.getUtilisateur();
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        setPersonne(utilisateur == null ? null : utilisateur.getPersonne());
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    public String getNom() {
        return personne == null ? null : personne.getNom();
    }

    public void setNom(String nom) {
        assurerPersonne().setNom(nom);
    }

    public String getPrenom() {
        return personne == null ? null : personne.getPrenom();
    }

    public void setPrenom(String prenom) {
        assurerPersonne().setPrenom(prenom);
    }

    public String getTelephone() {
        return personne == null ? null : personne.getTelephone();
    }

    public void setTelephone(String telephone) {
        assurerPersonne().setTelephone(telephone);
    }

    public String getVille() {
        return personne == null ? null : personne.getVille();
    }

    public void setVille(String ville) {
        assurerPersonne().setVille(ville);
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getEmail() {
        return personne == null ? null : personne.getEmail();
    }

    public String getNomComplet() {
        return personne == null ? "" : personne.getNomComplet();
    }

    private Personne assurerPersonne() {
        if (personne == null) {
            personne = new Personne();
        }
        return personne;
    }
}
