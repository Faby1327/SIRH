package com.example.logiciel_RH.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "entretien_evaluateur")
public class EntretienEvaluateur {

    @EmbeddedId
    private EntretienEvaluateurId id = new EntretienEvaluateurId();

    @ManyToOne
    @MapsId("entretienId")
    @JoinColumn(name = "entretien_id")
    private Entretien entretien;

    @ManyToOne
    @MapsId("utilisateurId")
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur evaluateur;

    public EntretienEvaluateur() {
    }

    public EntretienEvaluateur(Entretien entretien, Utilisateur evaluateur) {
        this.entretien = entretien;
        this.evaluateur = evaluateur;
        this.id = new EntretienEvaluateurId(entretien.getId(), evaluateur.getIdUtilisateur());
    }

    public EntretienEvaluateurId getId() {
        return id;
    }

    public void setId(EntretienEvaluateurId id) {
        this.id = id;
    }

    public Entretien getEntretien() {
        return entretien;
    }

    public void setEntretien(Entretien entretien) {
        this.entretien = entretien;
    }

    public Utilisateur getEvaluateur() {
        return evaluateur;
    }

    public void setEvaluateur(Utilisateur evaluateur) {
        this.evaluateur = evaluateur;
    }

    @Embeddable
    public static class EntretienEvaluateurId implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long entretienId;
        private Long utilisateurId;

        public EntretienEvaluateurId() {
        }

        public EntretienEvaluateurId(Long entretienId, Long utilisateurId) {
            this.entretienId = entretienId;
            this.utilisateurId = utilisateurId;
        }

        public Long getEntretienId() {
            return entretienId;
        }

        public void setEntretienId(Long entretienId) {
            this.entretienId = entretienId;
        }

        public Long getUtilisateurId() {
            return utilisateurId;
        }

        public void setUtilisateurId(Long utilisateurId) {
            this.utilisateurId = utilisateurId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EntretienEvaluateurId)) return false;
            EntretienEvaluateurId that = (EntretienEvaluateurId) o;
            return Objects.equals(entretienId, that.entretienId) && Objects.equals(utilisateurId, that.utilisateurId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entretienId, utilisateurId);
        }
    }
}
