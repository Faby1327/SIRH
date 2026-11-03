package com.example.logiciel_RH.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Candidature;
import com.example.logiciel_RH.entity.CritereEvaluation;
import com.example.logiciel_RH.entity.Entretien;
import com.example.logiciel_RH.entity.EntretienEvaluateur;
import com.example.logiciel_RH.entity.Evaluation;
import com.example.logiciel_RH.entity.EvaluationNote;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.CandidatureRepository;
import com.example.logiciel_RH.repository.CritereEvaluationRepository;
import com.example.logiciel_RH.repository.EntretienEvaluateurRepository;
import com.example.logiciel_RH.repository.EntretienRepository;
import com.example.logiciel_RH.repository.EvaluationNoteRepository;
import com.example.logiciel_RH.repository.EvaluationRepository;
import com.example.logiciel_RH.repository.UtilisateurRepository;

@Service
public class EntretienService {

    private final EntretienRepository entretienRepository;
    private final EntretienEvaluateurRepository entretienEvaluateurRepository;
    private final CritereEvaluationRepository critereEvaluationRepository;
    private final EvaluationRepository evaluationRepository;
    private final EvaluationNoteRepository evaluationNoteRepository;
    private final CandidatureRepository candidatureRepository;
    private final UtilisateurRepository utilisateurRepository;

    public EntretienService(EntretienRepository entretienRepository,
                            EntretienEvaluateurRepository entretienEvaluateurRepository,
                            CritereEvaluationRepository critereEvaluationRepository,
                            EvaluationRepository evaluationRepository,
                            EvaluationNoteRepository evaluationNoteRepository,
                            CandidatureRepository candidatureRepository,
                            UtilisateurRepository utilisateurRepository) {
        this.entretienRepository = entretienRepository;
        this.entretienEvaluateurRepository = entretienEvaluateurRepository;
        this.critereEvaluationRepository = critereEvaluationRepository;
        this.evaluationRepository = evaluationRepository;
        this.evaluationNoteRepository = evaluationNoteRepository;
        this.candidatureRepository = candidatureRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<CritereEvaluation> criteresPourOffre(OffreEmploi offre) {
        if (offre == null || offre.getIdOffre() == null) {
            return List.of();
        }
        assurerCriteresDefaut(offre.getIdOffre());
        return critereEvaluationRepository.findByOffre_IdOffreOrderByOrdreAscIdAsc(offre.getIdOffre());
    }

    @Transactional
    public void assurerCriteresDefaut(Long idOffre) {
        if (idOffre == null) {
            return;
        }
        if (critereEvaluationRepository.countByOffre_IdOffre(idOffre) == 0) {
            OffreEmploi offre = new OffreEmploi();
            offre.setIdOffre(idOffre);
            CritereEvaluation c1 = new CritereEvaluation();
            c1.setOffre(offre);
            c1.setLibelle("Compétences techniques");
            c1.setOrdre(1);
            CritereEvaluation c2 = new CritereEvaluation();
            c2.setOffre(offre);
            c2.setLibelle("Expérience pertinente");
            c2.setOrdre(2);
            CritereEvaluation c3 = new CritereEvaluation();
            c3.setOffre(offre);
            c3.setLibelle("Communication");
            c3.setOrdre(3);
            critereEvaluationRepository.saveAll(List.of(c1, c2, c3));
        }
    }

    @Transactional
    public Entretien planifier(Long candidatureId, String typeEvaluation, LocalDateTime dateHeure,
                               String lieu, String consigne, Long planifieParId, List<Long> evaluateurIds) {
        Candidature candidature = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        if (candidature.getOffre() == null || candidature.getOffre().getIdOffre() == null) {
            throw new IllegalArgumentException("Offre introuvable pour cette candidature");
        }
        if (!Candidature.STATUT_PRESELECTIONNEE.equals(candidature.getStatut())
                && !Candidature.STATUT_EVALUEE.equals(candidature.getStatut())) {
            throw new IllegalArgumentException("Seule une candidature présélectionnée peut recevoir un entretien.");
        }
        if (dateHeure == null) {
            throw new IllegalArgumentException("Date et heure obligatoires");
        }
        if (lieu == null || lieu.isBlank()) {
            throw new IllegalArgumentException("Lieu ou lien obligatoire");
        }
        if (typeEvaluation == null || typeEvaluation.isBlank()) {
            throw new IllegalArgumentException("Type d'entretien obligatoire");
        }
        if (typeEvaluation != null) {
            typeEvaluation = typeEvaluation.trim();
        }
        if (!java.util.Set.of("ENTRETIEN_RH", "ENTRETIEN_TECHNIQUE", "TEST_PRATIQUE")
                .contains(typeEvaluation)) {
            throw new IllegalArgumentException("Type d'entretien invalide");
        }

        Utilisateur planifiePar = utilisateurRepository.findById(planifieParId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        assurerCriteresDefaut(candidature.getOffre().getIdOffre());

        Entretien entretien = new Entretien();
        entretien.setCandidature(candidature);
        entretien.setTypeEvaluation(typeEvaluation);
        entretien.setDateHeure(dateHeure);
        entretien.setLieu(lieu.trim());
        entretien.setConsigne(consigne == null || consigne.isBlank() ? null : consigne.trim());
        entretien.setStatut(Entretien.STATUT_PLANIFIE);
        entretien.setPlanifiePar(planifiePar);
        entretien.setDateCreation(LocalDateTime.now());
        entretien = entretienRepository.save(entretien);

        Set<Long> aAjouter = new LinkedHashSet<>();
        if (evaluateurIds != null) {
            for (Long idEval : evaluateurIds) {
                if (idEval != null) {
                    aAjouter.add(idEval);
                }
            }
        }
        if (aAjouter.isEmpty()) {
            aAjouter.add(planifieParId);
        }
        if (!aAjouter.contains(planifieParId)) {
            aAjouter.add(planifieParId);
        }

        for (Long idEval : aAjouter) {
            Optional<Utilisateur> evalOpt = utilisateurRepository.findById(idEval);
            if (evalOpt.isEmpty()) {
                continue;
            }
            EntretienEvaluateur ee = new EntretienEvaluateur(entretien, evalOpt.get());
            entretienEvaluateurRepository.save(ee);
        }

        entretienRepository.flush();
        return entretienRepository.findByIdAvecRelations(entretien.getId()).orElse(entretien);
    }

    @Transactional
    public boolean annuler(Long entretienId, Long utilisateurId) {
        Entretien entretien = entretienRepository.findById(entretienId).orElse(null);
        if (entretien == null) {
            return false;
        }
        if (!Entretien.STATUT_PLANIFIE.equals(entretien.getStatut())) {
            return false;
        }
        entretien.setStatut(Entretien.STATUT_ANNULE);
        entretienRepository.save(entretien);
        return true;
    }

    @Transactional
    public Evaluation enregistrerEvaluation(Long entretienId, Long evaluateurId, String recommandation,
                                            String commentaire, Map<Long, Integer> notes) {
        if (recommandation == null || recommandation.isBlank()) {
            throw new IllegalArgumentException("Recommandation obligatoire");
        }
        if (commentaire == null || commentaire.isBlank()) {
            throw new IllegalArgumentException("Commentaire obligatoire");
        }
        if (!List.of(Evaluation.AVIS_FAVORABLE, Evaluation.AVIS_RESERVE, Evaluation.AVIS_DEFAVORABLE)
                .contains(recommandation)) {
            throw new IllegalArgumentException("Recommandation invalide");
        }
        if (!entretienEvaluateurRepository.existsByEntretien_IdAndEvaluateur_IdUtilisateur(entretienId, evaluateurId)) {
            throw new IllegalArgumentException("Cet entretien ne vous est pas assigné");
        }

        Entretien entretien = entretienRepository.findByIdAvecRelations(entretienId)
                .orElseThrow(() -> new IllegalArgumentException("Entretien introuvable"));
        if (Entretien.STATUT_ANNULE.equals(entretien.getStatut())) {
            throw new IllegalArgumentException("Un entretien annulé ne peut pas être évalué");
        }

        Utilisateur evaluateur = utilisateurRepository.findById(evaluateurId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluateur introuvable"));

        assurerCriteresDefaut(entretien.getCandidature().getOffre().getIdOffre());
        List<CritereEvaluation> criteres = criteresPourOffre(entretien.getCandidature().getOffre());

        Evaluation evaluation = evaluationRepository.findByEntretien_IdAndEvaluateur_IdUtilisateur(entretienId, evaluateurId)
                .orElse(null);
        boolean nouveau = evaluation == null;
        if (evaluation == null) {
            evaluation = new Evaluation();
            evaluation.setEntretien(entretien);
            evaluation.setEvaluateur(evaluateur);
            evaluation.setDateEvaluation(LocalDateTime.now());
        } else {
            evaluation.setDateModification(LocalDateTime.now());
        }
        evaluation.setRecommandation(recommandation);
        evaluation.setCommentaire(commentaire.trim());

        evaluation = evaluationRepository.save(evaluation);

        // Remplace les notes existantes : suppression explicite avant réinsertion,
        // pour respecter la contrainte unique (évaluation, critère).
        evaluationNoteRepository.supprimerPourEvaluation(evaluation.getId());
        evaluation.getNotes().clear();
        if (notes != null) {
            for (CritereEvaluation critere : criteres) {
                Integer note = notes.get(critere.getId());
                if (note == null) {
                    continue;
                }
                if (note < 1 || note > 5) {
                    continue;
                }
                EvaluationNote en = new EvaluationNote();
                en.setEvaluation(evaluation);
                en.setCritere(critere);
                en.setNote(note);
                evaluation.getNotes().add(en);
            }
        }
        evaluationNoteRepository.saveAll(evaluation.getNotes());
        evaluationRepository.save(evaluation);

        mettreAJourStatutEntretienEtCandidature(entretien);

        return evaluationRepository.findById(evaluation.getId()).orElse(evaluation);
    }

    @Transactional
    public void mettreAJourStatutEntretienEtCandidature(Entretien entretien) {
        if (entretien == null || entretien.getId() == null) {
            return;
        }
        Entretien e = entretienRepository.findByIdAvecRelations(entretien.getId()).orElse(entretien);
        if (e == null || Entretien.STATUT_ANNULE.equals(e.getStatut())) {
            return;
        }
        int nbDesignees = e.getEvaluationsDesignees() == null ? 0 : e.getEvaluationsDesignees().size();
        long nbEvaluations = evaluationRepository.countByEntretien_Id(e.getId());
        if (nbDesignees > 0 && nbEvaluations >= nbDesignees) {
            e.setStatut(Entretien.STATUT_TERMINE);
            entretienRepository.save(e);
        }

        Candidature candidature = e.getCandidature();
        if (candidature == null || candidature.getIdCandidature() == null) {
            return;
        }
        Candidature c = candidatureRepository.findById(candidature.getIdCandidature()).orElse(candidature);
        List<Entretien> entretiensC = entretienRepository.findByCandidature(c.getIdCandidature());
        boolean aPlanifie = entretiensC.stream().anyMatch(ent -> Entretien.STATUT_PLANIFIE.equals(ent.getStatut()));
        if (!aPlanifie) {
            boolean tousTerminesOuAnnules = entretiensC.stream()
                    .allMatch(ent -> Entretien.STATUT_TERMINE.equals(ent.getStatut()) || Entretien.STATUT_ANNULE.equals(ent.getStatut()));
            boolean aEvaluation = evaluationRepository.findByCandidature(c.getIdCandidature()).stream().findAny().isPresent();
            if (tousTerminesOuAnnules && aEvaluation) {
                if (c.getStatut() == null || !Candidature.STATUT_EVALUEE.equals(c.getStatut())
                        && !Candidature.STATUT_REFUSEE.equals(c.getStatut())
                        && !Candidature.STATUT_RETENUE.equals(c.getStatut())) {
                    c.setStatut(Candidature.STATUT_EVALUEE);
                    candidatureRepository.save(c);
                }
            }
        }
    }

    public List<Entretien> tous() {
        return entretienRepository.findAllAvecRelations();
    }

    public List<Entretien> pourCandidature(Long idCandidature) {
        if (idCandidature == null) {
            return List.of();
        }
        return entretienRepository.findByCandidature(idCandidature);
    }

    public List<Entretien> pourEvaluateur(Long idEvaluateur) {
        if (idEvaluateur == null) {
            return List.of();
        }
        return entretienRepository.findPourEvaluateur(idEvaluateur);
    }

    public List<Evaluation> evaluationsPourCandidature(Long idCandidature) {
        if (idCandidature == null) {
            return List.of();
        }
        return evaluationRepository.findByCandidature(idCandidature);
    }

    public Optional<Evaluation> evaluationPour(Long idEntretien, Long idEvaluateur) {
        if (idEntretien == null || idEvaluateur == null) {
            return Optional.empty();
        }
        return evaluationRepository.findByEntretien_IdAndEvaluateur_IdUtilisateur(idEntretien, idEvaluateur);
    }

    public Optional<Entretien> findById(Long idEntretien) {
        return entretienRepository.findByIdAvecRelations(idEntretien);
    }

    public boolean estEvaluateur(Long entretienId, Long utilisateurId) {
        if (entretienId == null || utilisateurId == null) {
            return false;
        }
        return entretienEvaluateurRepository.existsByEntretien_IdAndEvaluateur_IdUtilisateur(entretienId, utilisateurId);
    }

    public Map<Long, Evaluation> evaluationsParEntretien(Long entretienId) {
        Map<Long, Evaluation> map = new LinkedHashMap<>();
        for (Evaluation ev : evaluationRepository.findByEntretien_IdOrderByDateEvaluationAsc(entretienId)) {
            if (ev.getEvaluateur() != null) {
                map.put(ev.getEvaluateur().getIdUtilisateur(), ev);
            }
        }
        return map;
    }

    public List<Utilisateur> utilisateursPourDesignation() {
        List<Utilisateur> tous = utilisateurRepository.findAll();
        List<Utilisateur> resultat = new ArrayList<>();
        for (Utilisateur u : tous) {
            if (u.getRole() == null) {
                continue;
            }
            String role = u.getRole();
            if ("RH".equals(role) || "MANAGER".equals(role)) {
                resultat.add(u);
            }
        }
        return resultat;
    }
}
