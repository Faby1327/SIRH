package com.example.logiciel_RH.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long idUtilisateur;

    @OneToOne
    @JoinColumn(name = "personne_id", nullable = false, unique = true)
    private Personne personne;

    @Column(name = "mot_de_passe")
    private String motDePasseHash;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Formula("(select r.nom from role r where r.id = {alias}.role_id)")
    private String role;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    public Utilisateur() {
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getNom() {
        return personne == null ? null : personne.getNomComplet();
    }

    public void setNom(String nom) {
        assurerPersonne().setNom(nom);
    }

    public String getNomComplet() {
        return personne == null ? "" : personne.getNomComplet();
    }

    public String getEmail() {
        return personne == null ? null : personne.getEmail();
    }

    public void setEmail(String email) {
        assurerPersonne().setEmail(email);
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
        if (personne != null && personne.getUtilisateur() != this) {
            personne.setUtilisateur(this);
        }
    }

    private Personne assurerPersonne() {
        if (personne == null) {
            personne = new Personne();
            personne.setUtilisateur(this);
        }
        return personne;
    }
}
