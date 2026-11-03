package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Candidature;
import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.Entretien;
import com.example.logiciel_RH.entity.Evaluation;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.CandidatureService;
import com.example.logiciel_RH.service.DemandeRecrutementService;
import com.example.logiciel_RH.service.EntretienService;
import com.example.logiciel_RH.service.PosteService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final DemandeRecrutementService demandeService;
    private final PosteService posteService;
    private final CandidatureService candidatureService;
    private final EntretienService entretienService;

    public ManagerController(DemandeRecrutementService demandeService,
                             PosteService posteService,
                             CandidatureService candidatureService,
                             EntretienService entretienService) {
        this.demandeService = demandeService;
        this.posteService = posteService;
        this.candidatureService = candidatureService;
        this.entretienService = entretienService;
    }

    @GetMapping("/accueil")
    public String accueil(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        List<DemandeRecrutement> demandes = demandeService.findByManager(utilisateur);
        Map<Long, Integer> etapesRefus = new HashMap<>();
        demandes.stream()
                .filter(d -> d.getStatutActuel() != null && List.of("REFUSEE", "BUDGET_INSUFFISANT")
                        .contains(d.getStatutActuel().getLibelle()))
                .forEach(d -> etapesRefus.put(d.getIdDemande(), demandeService.getEtapeRefus(d)));
        model.addAttribute("utilisateurConnecte", utilisateur);
        model.addAttribute("demandes", demandes);
        model.addAttribute("etapesRefus", etapesRefus);
        model.addAttribute("nbDemandes", demandes.size());
        long nbBrouillons = compterStatut(demandes, "BROUILLON");
        model.addAttribute("nbBrouillons", nbBrouillons);
        model.addAttribute("nbValidees", demandes.size() - nbBrouillons);

        return "manager/accueil";
    }

    @GetMapping("/nouvelle")
    public String nouvelle(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        model.addAttribute("utilisateurConnecte", manager);
        model.addAttribute("postes", posteService.findByDepartement(manager.getDepartement().getIdDepartement()));
        model.addAttribute("dateMin", LocalDate.now());
        return "manager/nouvelle";
    }

    @PostMapping("/nouvelle")
    public String creer(@RequestParam Long idPoste,
                        @RequestParam Integer nombrePersonnes,
                        @RequestParam BigDecimal budgetDemande,
                        @RequestParam String motif,
                        @RequestParam(required = false) String dateSouhaitee,
                        @RequestParam(defaultValue = "CREATION") String typeRecrutement,
                        @RequestParam(defaultValue = "SOUMISE") String action,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        LocalDate date = null;
        try {
            if (dateSouhaitee != null && !dateSouhaitee.isBlank()) {
                date = LocalDate.parse(dateSouhaitee);
            }
        } catch (DateTimeParseException exception) {
            redirectAttributes.addFlashAttribute("error", "La date souhaitée n’est pas valide.");
            return "redirect:/manager/nouvelle";
        }
        if (date != null && date.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "La date souhaitée doit être égale ou postérieure à aujourd’hui.");
            return "redirect:/manager/nouvelle";
        }

        if (!"BROUILLON".equals(action) && !"SOUMISE".equals(action)) {
            redirectAttributes.addFlashAttribute("error", "Action de demande invalide.");
            return "redirect:/manager/nouvelle";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        boolean posteAutorise = posteService.findByDepartement(manager.getDepartement().getIdDepartement()).stream()
                .anyMatch(poste -> poste.getIdPoste().equals(idPoste));
        if (!posteAutorise) {
            redirectAttributes.addFlashAttribute("error", "Le poste sélectionné ne fait pas partie de votre département.");
            return "redirect:/manager/nouvelle";
        }

        demandeService.creerDemande(
                manager,
                idPoste,
                nombrePersonnes,
                budgetDemande,
                motif,
                date,
                typeRecrutement,
                action);

        redirectAttributes.addFlashAttribute("message", "BROUILLON".equals(action)
                ? "Demande enregistrée comme brouillon."
                : "Demande soumise avec succès.");
        return "redirect:/manager/accueil";
    }

    // ------------------------------------------------------------------
    // Bloc 6 — Candidats à évaluer (entretiens assignés au manager)
    // ------------------------------------------------------------------

    @GetMapping("/candidats")
    public String candidats(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        List<Entretien> entretiens = entretienService.pourEvaluateur(manager.getIdUtilisateur());
        Map<Long, List<Entretien>> entretiensParCandidature = new LinkedHashMap<>();
        for (Entretien entretien : entretiens) {
            if (entretien.getCandidature() != null) {
                entretiensParCandidature
                        .computeIfAbsent(entretien.getCandidature().getIdCandidature(), id -> new java.util.ArrayList<>())
                        .add(entretien);
            }
        }

        Long idDepartement = manager.getDepartement() == null
                ? null : manager.getDepartement().getIdDepartement();
        List<Candidature> candidats = candidatureService.pourManager(idDepartement).stream()
                .filter(c -> entretiensParCandidature.containsKey(c.getIdCandidature()))
                .toList();

        long aEvaluer = 0;
        long evaluations = 0;
        Map<Long, Boolean> dejaEvalueParCandidature = new LinkedHashMap<>();
        for (Candidature candidat : candidats) {
            List<Entretien> assignes = entretiensParCandidature.get(candidat.getIdCandidature());
            if (assignes == null) {
                dejaEvalueParCandidature.put(candidat.getIdCandidature(), false);
                continue;
            }
            long aEvaluerPourCandidat = 0;
            boolean toutEvalue = true;
            for (Entretien entretien : assignes) {
                if (entretienService.evaluationPour(entretien.getId(), manager.getIdUtilisateur()).isPresent()) {
                    evaluations++;
                } else if (!Entretien.STATUT_ANNULE.equals(entretien.getStatut())) {
                    aEvaluerPourCandidat++;
                    toutEvalue = false;
                }
                if (Entretien.STATUT_ANNULE.equals(entretien.getStatut())
                        && entretienService.evaluationPour(entretien.getId(), manager.getIdUtilisateur()).isEmpty()) {
                    // annulé sans évaluation ne compte pas comme à faire
                }
            }
            if (aEvaluerPourCandidat > 0) {
                aEvaluer++;
            }
            dejaEvalueParCandidature.put(candidat.getIdCandidature(), toutEvalue && aEvaluerPourCandidat == 0 && evaluations >= assignes.size());
        }

        model.addAttribute("utilisateurConnecte", manager);
        model.addAttribute("candidats", candidats);
        model.addAttribute("entretiensParCandidature", entretiensParCandidature);
        model.addAttribute("dejaEvalueParCandidature", dejaEvalueParCandidature);
        model.addAttribute("nbCandidats", candidats.size());
        model.addAttribute("nbAEvaluer", aEvaluer);
        model.addAttribute("nbEvaluations", evaluations);
        return "manager/candidats";
    }

    @GetMapping("/candidats/{id}")
    public String evaluerCandidat(@PathVariable("id") Long idCandidature,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        Candidature candidature = candidatureService.findById(idCandidature).orElse(null);
        if (candidature == null || !candidatureDuDepartement(candidature, manager)) {
            redirectAttributes.addFlashAttribute("error", "Ce candidat n'appartient pas à votre département.");
            return "redirect:/manager/candidats";
        }

        List<Entretien> entretiens = entretienService.pourCandidature(idCandidature).stream()
                .filter(e -> entretienService.estEvaluateur(e.getId(), manager.getIdUtilisateur()))
                .toList();
        if (entretiens.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Aucun entretien ne vous est assigné pour ce candidat.");
            return "redirect:/manager/candidats";
        }

        Map<Long, Evaluation> mesEvaluations = new LinkedHashMap<>();
        for (Entretien entretien : entretiens) {
            entretienService.evaluationPour(entretien.getId(), manager.getIdUtilisateur())
                    .ifPresent(evaluation -> mesEvaluations.put(entretien.getId(), evaluation));
        }

        model.addAttribute("utilisateurConnecte", manager);
        model.addAttribute("candidature", candidature);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("criteres", entretienService.criteresPourOffre(candidature.getOffre()));
        model.addAttribute("evaluations", entretienService.evaluationsPourCandidature(idCandidature));
        model.addAttribute("mesEvaluations", mesEvaluations);
        return "manager/evaluation-candidat";
    }

    @PostMapping("/candidats/{id}/evaluation")
    public String enregistrerEvaluationCandidat(@PathVariable("id") Long idCandidature,
                                                @RequestParam Long entretienId,
                                                @RequestParam String recommandation,
                                                @RequestParam(required = false) String commentaire,
                                                @RequestParam Map<String, String> params,
                                                HttpSession session,
                                                RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        Candidature candidature = candidatureService.findById(idCandidature).orElse(null);
        Entretien entretien = entretienService.findById(entretienId).orElse(null);
        if (candidature == null
                || !candidatureDuDepartement(candidature, manager)
                || entretien == null
                || entretien.getCandidature() == null
                || !idCandidature.equals(entretien.getCandidature().getIdCandidature())
                || !entretienService.estEvaluateur(entretienId, manager.getIdUtilisateur())) {
            redirectAttributes.addFlashAttribute("error", "Cet entretien ne vous est pas assigné.");
            return "redirect:/manager/candidats";
        }

        Map<Long, Integer> notes = new LinkedHashMap<>();
        for (Map.Entry<String, String> entree : params.entrySet()) {
            if (!entree.getKey().startsWith("note_")) {
                continue;
            }
            try {
                notes.put(Long.valueOf(entree.getKey().substring(5)), Integer.valueOf(entree.getValue()));
            } catch (NumberFormatException ignore) {
                // Note invalide : le service ne gardera que les valeurs de 1 à 5.
            }
        }

        try {
            entretienService.enregistrerEvaluation(entretienId,
                    manager.getIdUtilisateur(), recommandation, commentaire, notes);
            redirectAttributes.addFlashAttribute("message", "Évaluation enregistrée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/manager/candidats/" + idCandidature;
    }

    private boolean candidatureDuDepartement(Candidature candidature, Utilisateur manager) {
        if (candidature == null || candidature.getOffre() == null || manager == null) {
            return false;
        }
        Long idDepartementOffre = candidature.getOffre().getDepartement() == null
                ? null : candidature.getOffre().getDepartement().getIdDepartement();
        Long idDepartementManager = manager.getDepartement() == null
                ? null : manager.getDepartement().getIdDepartement();
        return idDepartementOffre != null && idDepartementOffre.equals(idDepartementManager);
    }

    private long compterStatut(List<DemandeRecrutement> demandes, String statut) {
        return demandes.stream()
                .filter(d -> d.getStatutActuel() != null && statut.equals(d.getStatutActuel().getLibelle()))
                .count();
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long idDemande,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        Utilisateur manager = SessionUtils.getUtilisateur(session);
        Optional<DemandeRecrutement> demande = demandeService.findByIdForManager(idDemande, manager);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/manager/accueil";
        }

        model.addAttribute("utilisateurConnecte", manager);
        model.addAttribute("demande", demande.get());
        model.addAttribute("historique", demandeService.findHistorique(idDemande));
        model.addAttribute("etapeRefus", demandeService.getEtapeRefus(demande.get()));

        return "manager/detail";
    }

    @PostMapping("/delete/{id}")
    public String supprimer(@PathVariable("id") Long idDemande,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "MANAGER")) {
            return "redirect:/login";
        }

        boolean supprimee = demandeService.supprimerParManager(idDemande, SessionUtils.getUtilisateur(session));
        if (supprimee) {
            redirectAttributes.addFlashAttribute("message", "Demande supprimee.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Suppression impossible pour ce statut.");
        }

        return "redirect:/manager/accueil";
    }
}
