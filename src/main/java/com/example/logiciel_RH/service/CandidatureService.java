package com.example.logiciel_RH.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Candidat;
import com.example.logiciel_RH.entity.Candidature;
import com.example.logiciel_RH.entity.CritereCandidature;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.ReponseCandidature;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.CandidatureRepository;
import com.example.logiciel_RH.repository.CritereCandidatureRepository;
import com.example.logiciel_RH.repository.ReponseCandidatureRepository;

/**
 * Formulaire de candidature interne des offres d'emploi (bloc 2 — Recrutement).
 *
 * Les critères du formulaire dépendent du poste de l'offre. Chaque candidature
 * est enregistrée avec une réponse par critère renseigné.
 */
@Service
public class CandidatureService {

    private final CandidatureRepository candidatureRepository;
    private final ReponseCandidatureRepository reponseRepository;
    private final CritereCandidatureRepository critereRepository;

    public CandidatureService(CandidatureRepository candidatureRepository,
                              ReponseCandidatureRepository reponseRepository,
                              CritereCandidatureRepository critereRepository) {
        this.candidatureRepository = candidatureRepository;
        this.reponseRepository = reponseRepository;
        this.critereRepository = critereRepository;
    }

    /** Champs du formulaire pour le poste de l'offre. */
    public List<CritereCandidature> criteresPour(OffreEmploi offre) {
        if (offre == null || offre.getPoste() == null) {
            return List.of();
        }
        return critereRepository.findByPoste_IdPosteOrderByOrdreAscIdCritereAsc(
                offre.getPoste().getIdPoste());
    }

    /**
     * Enregistre une candidature et ses réponses.
     *
     * @param valeurs réponses saisies, indexées par identifiant de critère
     */
    /**
     * Enregistre une candidature liée à un compte candidat.
     *
     * Les informations d'identité sont reprises du profil : le formulaire ne
     * peut pas les modifier, et les colonnes de la candidature en gardent une
     * copie pour l'affichage RH.
     */
    @Transactional
    public Candidature enregistrer(OffreEmploi offre,
                                   Candidat candidat,
                                   String message,
                                   Map<Long, String> valeurs) {
        Utilisateur utilisateur = candidat.getUtilisateur();
        return enregistrer(offre,
                candidat.getNom(),
                candidat.getPrenom(),
                utilisateur == null ? null : utilisateur.getEmail(),
                candidat.getTelephone(),
                message,
                valeurs,
                candidat);
    }

    @Transactional
    public Candidature enregistrer(OffreEmploi offre,
                                   String nom,
                                   String prenom,
                                   String email,
                                   String telephone,
                                   String message,
                                   Map<Long, String> valeurs) {
        return enregistrer(offre, nom, prenom, email, telephone, message, valeurs, null);
    }

    private Candidature enregistrer(OffreEmploi offre,
                                    String nom,
                                    String prenom,
                                    String email,
                                    String telephone,
                                    String message,
                                    Map<Long, String> valeurs,
                                    Candidat candidat) {
        Candidature candidature = new Candidature();
        candidature.setOffre(offre);
        candidature.setCandidat(candidat);
        candidature.setNom(nom.trim());
        candidature.setPrenom(prenom.trim());
        candidature.setEmail(email.trim());
        candidature.setTelephone(telephone.trim());
        candidature.setMessage(estVide(message) ? null : message.trim());
        candidature.setDateCandidature(LocalDateTime.now());
        candidature.setStatut(Candidature.STATUT_NOUVELLE);
        candidature = candidatureRepository.save(candidature);

        for (CritereCandidature critere : criteresPour(offre)) {
            String valeur = valeurs.get(critere.getIdCritere());
            if (estVide(valeur)) {
                continue;
            }
            ReponseCandidature reponse = new ReponseCandidature();
            reponse.setCandidature(candidature);
            reponse.setCritere(critere);
            reponse.setValeur(valeur.trim());
            reponseRepository.save(reponse);
        }

        return candidature;
    }

    public List<Candidature> parOffre(Long idOffre) {
        return candidatureRepository.findByOffre_IdOffreOrderByDateCandidatureDesc(idOffre);
    }

    public Optional<Candidature> findById(Long idCandidature) {
        return candidatureRepository.findById(idCandidature);
    }

    /** Candidatures pouvant recevoir un entretien ou un test. */
    public List<Candidature> pourEntretien() {
        return candidatureRepository.findByStatutInOrderByDateCandidatureAsc(
                List.of(Candidature.STATUT_PRESELECTIONNEE, Candidature.STATUT_EVALUEE));
    }

    /** Candidatures visibles par un manager, limitées à son département. */
    public List<Candidature> pourManager(Long idDepartement) {
        if (idDepartement == null) {
            return List.of();
        }
        return candidatureRepository.findByOffre_Demande_Poste_Departement_IdDepartementAndStatutInOrderByDateCandidatureAsc(
                idDepartement,
                List.of(Candidature.STATUT_PRESELECTIONNEE, Candidature.STATUT_EVALUEE));
    }

    public List<ReponseCandidature> reponses(Candidature candidature) {
        return reponseRepository.findByCandidature_IdCandidatureOrderByCritere_OrdreAscIdReponseAsc(
                candidature.getIdCandidature());
    }

    /** Nombre de candidatures par offre, pour le tableau RH. */
    public Map<Long, Long> compterParOffre(List<OffreEmploi> offres) {
        Map<Long, Long> comptes = new HashMap<>();
        for (OffreEmploi offre : offres) {
            comptes.put(offre.getIdOffre(), candidatureRepository.countByOffre_IdOffre(offre.getIdOffre()));
        }
        return comptes;
    }

    /** Un candidat ne postule qu'une fois à une même offre. */
    public boolean dejaPostule(Long idOffre, String email) {
        return email != null
                && candidatureRepository.existsByOffre_IdOffreAndEmailIgnoreCase(idOffre, email.trim());
    }

    /**
     * Un candidat connecté ne postule qu'une fois à une offre. On vérifie aussi
     * l'email pour couvrir les candidatures déposées avant la création des
     * comptes candidats.
     */
    public boolean dejaPostule(Long idOffre, Candidat candidat) {
        if (candidat == null) {
            return false;
        }
        if (candidat.getIdCandidat() != null
                && candidatureRepository.existsByOffre_IdOffreAndCandidat_IdCandidat(
                        idOffre, candidat.getIdCandidat())) {
            return true;
        }
        return candidat.getEmail() != null
                && candidatureRepository.existsByOffre_IdOffreAndEmailIgnoreCase(
                        idOffre, candidat.getEmail());
    }

    /**
     * Enregistre la liste de présélection d'une offre.
     *
     * Les candidats cochés passent au statut PRESELECTIONNEE ; ceux qui ne le
     * sont plus reviennent au statut NOUVELLE. Seules les candidatures de
     * l'offre sont concernées.
     *
     * @param idsSelectionnes identifiants des candidats cochés
     * @param note           note facultative, appliquée aux candidats cochés
     * @return nombre de candidats présélectionnés après l'enregistrement
     */
    @Transactional
    public int enregistrerPreselection(OffreEmploi offre,
                                       List<Long> idsSelectionnes,
                                       Utilisateur rh,
                                       String note) {
        if (offre == null || offre.getIdOffre() == null) {
            return 0;
        }

        Set<Long> selection = idsSelectionnes == null
                ? Set.of()
                : new HashSet<>(idsSelectionnes);
        int total = 0;

        for (Candidature candidature : parOffre(offre.getIdOffre())) {
            if (selection.contains(candidature.getIdCandidature())) {
                if (!candidature.isPreselectionnee()) {
                    candidature.setStatut(Candidature.STATUT_PRESELECTIONNEE);
                    candidature.setDatePreselection(LocalDateTime.now());
                    candidature.setPreselectionnePar(rh);
                }
                if (!estVide(note)) {
                    candidature.setCommentairePreselection(note.trim());
                }
                total++;
                candidatureRepository.save(candidature);
            } else if (candidature.isPreselectionnee()) {
                candidature.setStatut(Candidature.STATUT_NOUVELLE);
                candidature.setDatePreselection(null);
                candidature.setPreselectionnePar(null);
                candidature.setCommentairePreselection(null);
                candidatureRepository.save(candidature);
            }
        }

        return total;
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }
}
