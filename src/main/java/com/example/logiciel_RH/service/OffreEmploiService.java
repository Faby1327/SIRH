package com.example.logiciel_RH.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.OffreEmploiRepository;

/**
 * Publication des offres d'emploi (bloc 2 — Recrutement).
 *
 * Une demande approuvée par la Direction peut être publiée par les RH :
 * l'offre devient alors visible sur la page publique, sans connexion.
 */
@Service
public class OffreEmploiService {

    private final OffreEmploiRepository repository;

    public OffreEmploiService(OffreEmploiRepository repository) {
        this.repository = repository;
    }

    /** Offres visibles par les candidats (page publique). */
    public List<OffreEmploi> findPubliees() {
        return repository.findByStatutOrderByDatePublicationDesc(OffreEmploi.STATUT_PUBLIEE);
    }

    /** Toutes les offres, pour l'écran RH. */
    public List<OffreEmploi> findAll() {
        return repository.findAllByOrderByDatePublicationDesc();
    }

    /** Une offre, quel que soit son statut (écran RH). */
    public Optional<OffreEmploi> findById(Long idOffre) {
        return repository.findById(idOffre);
    }

    /** Une offre publiée, consultable sans connexion. */
    public Optional<OffreEmploi> findPublique(Long idOffre) {
        return repository.findById(idOffre).filter(OffreEmploi::isPubliee);
    }

    public Optional<OffreEmploi> findByDemande(Long idDemande) {
        return repository.findByDemande_IdDemande(idDemande);
    }

    /** Vrai si la demande possède une offre actuellement publiée. */
    public boolean estPubliee(DemandeRecrutement demande) {
        return demande != null
                && findByDemande(demande.getIdDemande()).map(OffreEmploi::isPubliee).orElse(false);
    }

    /**
     * Publie (ou republie) l'offre d'emploi d'une demande approuvée.
     *
     * @param titre       titre saisi par les RH ; à défaut, un titre est généré
     * @param description description saisie par les RH ; à défaut, générée
     */
    @Transactional
    public OffreEmploi publier(DemandeRecrutement demande,
                               Utilisateur rh,
                               String titre,
                               String description) {
        if (demande == null || demande.getIdDemande() == null) {
            throw new IllegalArgumentException("Demande introuvable.");
        }
        if (demande.getStatutActuel() == null || !"VALIDEE".equals(demande.getStatutActuel().getLibelle())) {
            throw new IllegalArgumentException("Seule une demande approuvée par la Direction peut être publiée.");
        }

        Optional<OffreEmploi> offreExistante = findByDemande(demande.getIdDemande());
        if (offreExistante.isPresent()) {
            if (offreExistante.get().isCloturee()) {
                throw new IllegalArgumentException("Cette offre est clôturée et ne peut plus être publiée.");
            }
            if (OffreEmploi.STATUT_EN_ATTENTE_CONFIRMATION.equals(offreExistante.get().getStatut())) {
                throw new IllegalArgumentException("Cette offre est en attente de confirmation RH.");
            }
        }

        OffreEmploi offre = offreExistante.orElseGet(OffreEmploi::new);
        offre.setDemande(demande);
        offre.setTitre(estVide(titre) ? demande.getOffreTitreDefaut() : titre.trim());
        offre.setDescription(estVide(description) ? demande.getOffreDescriptionDefaut() : description.trim());
        offre.setDatePublication(LocalDateTime.now());
        offre.setStatut(OffreEmploi.STATUT_PUBLIEE);
        return repository.save(offre);
    }

    /** Retire l'offre du site public sans supprimer son historique. */
    @Transactional
    public boolean cloturer(Long idOffre) {
        Optional<OffreEmploi> offre = repository.findById(idOffre);
        if (offre.isEmpty()) {
            return false;
        }
        OffreEmploi valeur = offre.get();
        valeur.setStatut(OffreEmploi.STATUT_CLOTUREE);
        repository.save(valeur);
        return true;
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
