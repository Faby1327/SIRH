package com.example.logiciel_RH.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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
import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.CritereEvaluation;
import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.Entretien;
import com.example.logiciel_RH.entity.Evaluation;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.PropositionEmbauche;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.DepartementRepository;
import com.example.logiciel_RH.service.CandidatureService;
import com.example.logiciel_RH.service.DemandeRecrutementService;
import com.example.logiciel_RH.service.EntretienService;
import com.example.logiciel_RH.service.OffreEmploiService;
import com.example.logiciel_RH.service.PropositionEmbaucheService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh")
public class RhController {

    private final DemandeRecrutementService demandeService;
    private final DepartementRepository departementRepository;
    private final OffreEmploiService offreService;
    private final CandidatureService candidatureService;
    private final EntretienService entretienService;
    private final PropositionEmbaucheService propositionEmbaucheService;

    public RhController(DemandeRecrutementService demandeService,
                        DepartementRepository departementRepository,
                        OffreEmploiService offreService,
                        CandidatureService candidatureService,
                        EntretienService entretienService,
                        PropositionEmbaucheService propositionEmbaucheService) {
        this.demandeService = demandeService;
        this.departementRepository = departementRepository;
        this.offreService = offreService;
        this.candidatureService = candidatureService;
        this.entretienService = entretienService;
        this.propositionEmbaucheService = propositionEmbaucheService;
    }

    @GetMapping("/accueil")
    public String accueil(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("departements", departementRepository.findAll());
        var demandes = demandeService.findByStatuts(Arrays.asList(
                "SOUMISE",
                "EN_VERIFICATION_FINANCIERE",
                "BUDGET_INSUFFISANT",
                "EN_ATTENTE_VALIDATION",
                "REFUSEE",
                "VALIDEE",
                "ANNULEE"
        ));
        model.addAttribute("demandes", demandes);
        Map<Long, String> statutsRole = new HashMap<>();
        demandes.forEach(d -> statutsRole.put(d.getIdDemande(), demandeService.getStatutParRole(d, "RH")));
        model.addAttribute("statutsRole", statutsRole);
        model.addAttribute("aVerifier", statutsRole.values().stream().filter("EN_ATTENTE"::equals).count());
        model.addAttribute("validees", statutsRole.values().stream().filter("VALIDEE"::equals).count());
        model.addAttribute("refusees", statutsRole.values().stream().filter("REFUSEE"::equals).count());

        // Bloc 2 — Recrutement : demandes approuvées à publier et offres existantes.
        model.addAttribute("demandesAPublier", demandes.stream()
                .filter(d -> "VALIDEE".equals(d.getStatutActuel().getLibelle()))
                .filter(d -> offreService.findByDemande(d.getIdDemande()).isEmpty())
                .toList());
        // Bloc 2 — Recrutement : nombre de candidatures par offre.
        List<OffreEmploi> offres = offreService.findAll();
        model.addAttribute("offres", offres);
        model.addAttribute("candidaturesParOffre", candidatureService.compterParOffre(offres));

        return "rh/accueil";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long idDemande,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/rh/accueil";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("demande", demande.get());
        model.addAttribute("historique", demandeService.findHistorique(idDemande));
        model.addAttribute("etapeRefus", demandeService.getEtapeRefus(demande.get()));

        return "rh/detail";
    }

    @PostMapping("/valider/{id}")
    public String valider(@PathVariable("id") Long idDemande,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/rh/accueil";
        }

        Utilisateur rh = SessionUtils.getUtilisateur(session);
        demandeService.changerStatut(demande.get(), "EN_VERIFICATION_FINANCIERE", rh, "Validation RH");
        redirectAttributes.addFlashAttribute("message", "Demande validee et transmise a la Finance.");

        return "redirect:/rh/accueil";
    }

    @PostMapping("/refuser/{id}")
    public String refuser(@PathVariable("id") Long idDemande,
                          @RequestParam String remarque,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/rh/accueil";
        }

        Utilisateur rh = SessionUtils.getUtilisateur(session);
        demandeService.changerStatut(demande.get(), "REFUSEE", rh, remarque);
        redirectAttributes.addFlashAttribute("message", "Demande refusee.");

        return "redirect:/rh/accueil";
    }

    // ------------------------------------------------------------------
    // Bloc 2 — Recrutement : publication des offres d'emploi
    // ------------------------------------------------------------------

    @PostMapping("/publier/{id}")
    public String publier(@PathVariable("id") Long idDemande,
                          @RequestParam(value = "titre", required = false) String titre,
                          @RequestParam(value = "description", required = false) String description,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreurOffres", "Demande introuvable.");
            return "redirect:/rh/accueil#offres";
        }

        try {
            offreService.publier(demande.get(), SessionUtils.getUtilisateur(session), titre, description);
            redirectAttributes.addFlashAttribute("messageOffres",
                    "Offre publiée : elle est maintenant visible sur la page d'accueil publique.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erreurOffres", e.getMessage());
        }

        return "redirect:/rh/accueil#offres";
    }

    @PostMapping("/cloturer/{id}")
    public String cloturer(@PathVariable("id") Long idOffre,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        if (offreService.cloturer(idOffre)) {
            redirectAttributes.addFlashAttribute("messageOffres",
                    "Offre clôturée : elle n'est plus visible par les candidats.");
        } else {
            redirectAttributes.addFlashAttribute("erreurOffres", "Offre introuvable.");
        }

        return "redirect:/rh/accueil#offres";
    }

    // ------------------------------------------------------------------
    // Bloc 2 — Recrutement : consultation des candidatures
    // ------------------------------------------------------------------

    @GetMapping("/offre/{id}/candidatures")
    public String candidatures(@PathVariable("id") Long idOffre,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<OffreEmploi> offre = offreService.findById(idOffre);
        if (offre.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreurOffres", "Offre introuvable.");
            return "redirect:/rh/accueil#offres";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("offre", offre.get());
        List<Candidature> candidatures = candidatureService.parOffre(idOffre);
        model.addAttribute("candidatures", candidatures);
        model.addAttribute("nombrePreselectionnes",
                candidatures.stream().filter(Candidature::isPreselectionnee).count());
        return "rh/candidatures";
    }

    /** Enregistre la liste de présélection d'une offre (un ou plusieurs candidats). */
    @PostMapping("/offre/{id}/preselection")
    public String preselection(@PathVariable("id") Long idOffre,
                               @RequestParam(value = "candidatureIds", required = false) List<Long> candidatureIds,
                               @RequestParam(value = "note", required = false) String note,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<OffreEmploi> offre = offreService.findById(idOffre);
        if (offre.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreurPreselection", "Offre introuvable.");
            return "redirect:/rh/accueil#offres";
        }

        int nombre = candidatureService.enregistrerPreselection(
                offre.get(), candidatureIds, SessionUtils.getUtilisateur(session), note);

        if (nombre == 0) {
            redirectAttributes.addFlashAttribute("messagePreselection",
                    "Aucun candidat n'est présélectionné pour cette offre.");
        } else {
            redirectAttributes.addFlashAttribute("messagePreselection",
                    nombre + " candidat" + (nombre > 1 ? "s" : "") + " présélectionné" + (nombre > 1 ? "s" : "")
                            + " dans la liste de présélection.");
        }

        return "redirect:/rh/offre/" + idOffre + "/candidatures";
    }

    @GetMapping("/candidature/{id}")
    public String candidature(@PathVariable("id") Long idCandidature,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<Candidature> candidature = candidatureService.findById(idCandidature);
        if (candidature.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreurOffres", "Candidature introuvable.");
            return "redirect:/rh/accueil#offres";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("candidature", candidature.get());
        model.addAttribute("reponses", candidatureService.reponses(candidature.get()));
        model.addAttribute("entretiens", entretienService.pourCandidature(idCandidature));
        return "rh/candidature";
    }

    // ------------------------------------------------------------------
    // Bloc 6 — Entretiens, tests et évaluations
    // ------------------------------------------------------------------

    @GetMapping("/entretiens")
    public String entretiens(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        List<Entretien> entretiens = entretienService.tous();
        Utilisateur rh = SessionUtils.getUtilisateur(session);
        java.util.Map<Long, Boolean> dejaEvalueParCandidature = new java.util.LinkedHashMap<>();
        for (Entretien entretien : entretiens) {
            if (entretien.getCandidature() != null) {
                Long cid = entretien.getCandidature().getIdCandidature();
                boolean estDesigne = entretienService.estEvaluateur(entretien.getId(), rh.getIdUtilisateur());
                boolean aEvalue = estDesigne && entretienService.evaluationPour(entretien.getId(), rh.getIdUtilisateur()).isPresent();
                if (aEvalue) {
                    dejaEvalueParCandidature.put(cid, Boolean.TRUE);
                } else if (!dejaEvalueParCandidature.containsKey(cid)) {
                    dejaEvalueParCandidature.put(cid, Boolean.FALSE);
                }
            }
        }
        model.addAttribute("utilisateurConnecte", rh);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("dejaEvalueParCandidature", dejaEvalueParCandidature);
        model.addAttribute("nbEntretiens", entretiens.stream()
                .filter(e -> Entretien.STATUT_PLANIFIE.equals(e.getStatut())).count());
        model.addAttribute("nbTermines", entretiens.stream()
                .filter(e -> Entretien.STATUT_TERMINE.equals(e.getStatut())).count());
        model.addAttribute("nbTests", entretiens.stream()
                .filter(e -> Entretien.TYPE_TEST_PRACTIQUE.equals(e.getTypeEvaluation())).count());
        model.addAttribute("nbAnnules", entretiens.stream()
                .filter(e -> Entretien.STATUT_ANNULE.equals(e.getStatut())).count());
        return "rh/entretiens";
    }

    @GetMapping("/entretiens/planifier")
    public String planifierEntretienPage(@RequestParam(value = "candidatureId", required = false) Long candidatureId,
                                         HttpSession session,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Candidature candidature = null;
        if (candidatureId != null) {
            candidature = candidatureService.findById(candidatureId).orElse(null);
            if (candidature == null) {
                redirectAttributes.addFlashAttribute("error", "Candidature introuvable.");
                return "redirect:/rh/entretiens";
            }
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("candidatures", candidatureService.pourEntretien());
        model.addAttribute("candidature", candidature);
        model.addAttribute("evaluateurs", entretienService.utilisateursPourDesignation());
        model.addAttribute("maintenant", LocalDateTime.now().withSecond(0).withNano(0)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        return "rh/entretien-planifier";
    }

    @PostMapping("/entretiens/planifier")
    public String planifierEntretien(@RequestParam Long candidatureId,
                                     @RequestParam String typeEvaluation,
                                     @RequestParam String dateHeure,
                                     @RequestParam String lieu,
                                     @RequestParam(required = false) String consigne,
                                     @RequestParam(required = false) List<Long> evaluateurs,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        try {
            LocalDateTime date = LocalDateTime.parse(dateHeure);
            System.out.println("DEBUG typeEvaluation=[" + typeEvaluation + "] len=" + typeEvaluation.length());
            entretienService.planifier(candidatureId, typeEvaluation, date, lieu, consigne,
                    SessionUtils.getUtilisateur(session).getIdUtilisateur(), evaluateurs);
            redirectAttributes.addFlashAttribute("message", "Entretien ou test planifié.");
            return "redirect:/rh/entretiens";
        } catch (DateTimeParseException ex) {
            redirectAttributes.addFlashAttribute("error", "La date et l'heure sont invalides.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/entretiens/planifier?candidatureId=" + candidatureId;
    }

    @PostMapping("/entretiens/{id}/annuler")
    public String annulerEntretien(@PathVariable("id") Long idEntretien,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        boolean annule = entretienService.annuler(idEntretien,
                SessionUtils.getUtilisateur(session).getIdUtilisateur());
        redirectAttributes.addFlashAttribute(annule ? "message" : "error",
                annule ? "Entretien annulé." : "Cet entretien ne peut plus être annulé.");
        return "redirect:/rh/entretiens";
    }

    @GetMapping("/evaluations/{candidatureId}")
    public String evaluationCandidat(@PathVariable("candidatureId") Long idCandidature,
                                     HttpSession session,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Candidature candidature = candidatureService.findById(idCandidature).orElse(null);
        if (candidature == null) {
            redirectAttributes.addFlashAttribute("error", "Candidature introuvable.");
            return "redirect:/rh/entretiens";
        }

        Utilisateur rh = SessionUtils.getUtilisateur(session);
        List<Entretien> entretiens = entretienService.pourCandidature(idCandidature);
        Map<Long, Boolean> affectations = new LinkedHashMap<>();
        Map<Long, Evaluation> mesEvaluations = new LinkedHashMap<>();
        for (Entretien entretien : entretiens) {
            boolean affecte = entretienService.estEvaluateur(entretien.getId(), rh.getIdUtilisateur());
            affectations.put(entretien.getId(), affecte);
            if (affecte) {
                entretienService.evaluationPour(entretien.getId(), rh.getIdUtilisateur())
                        .ifPresent(evaluation -> mesEvaluations.put(entretien.getId(), evaluation));
            }
        }

        model.addAttribute("utilisateurConnecte", rh);
        model.addAttribute("candidature", candidature);
        model.addAttribute("entretiens", entretiens);
        model.addAttribute("criteres", entretienService.criteresPourOffre(candidature.getOffre()));
        model.addAttribute("evaluations", entretienService.evaluationsPourCandidature(idCandidature));
        model.addAttribute("affectations", affectations);
        model.addAttribute("mesEvaluations", mesEvaluations);
        return "rh/evaluation-candidat";
    }

    @PostMapping("/evaluations/enregistrer")
    public String enregistrerEvaluation(@RequestParam Long candidatureId,
                                        @RequestParam Long entretienId,
                                        @RequestParam String recommandation,
                                        @RequestParam(required = false) String commentaire,
                                        @RequestParam Map<String, String> params,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Entretien entretien = entretienService.findById(entretienId).orElse(null);
        if (entretien == null || !entretien.getCandidature().getIdCandidature().equals(candidatureId)) {
            redirectAttributes.addFlashAttribute("error", "Entretien introuvable pour cette candidature.");
            return "redirect:/rh/evaluations/" + candidatureId;
        }

        Map<Long, Integer> notes = new LinkedHashMap<>();
        for (Map.Entry<String, String> entree : params.entrySet()) {
            if (!entree.getKey().startsWith("note_")) {
                continue;
            }
            try {
                notes.put(Long.valueOf(entree.getKey().substring(5)), Integer.valueOf(entree.getValue()));
            } catch (NumberFormatException ignore) {
                // Note invalide : ignorée, le service ne gardera que 1..5.
            }
        }

        try {
            entretienService.enregistrerEvaluation(entretienId,
                    SessionUtils.getUtilisateur(session).getIdUtilisateur(),
                    recommandation, commentaire, notes);
            redirectAttributes.addFlashAttribute("message", "Évaluation enregistrée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/evaluations/" + candidatureId;
    }

    // ------------------------------------------------------------------
    // Bloc 7 — Proposition d'embauche
    // ------------------------------------------------------------------

    @GetMapping("/offre/{id}/embauches")
    public String embauches(@PathVariable("id") Long idOffre,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        Optional<OffreEmploi> offre = offreService.findById(idOffre);
        if (offre.isEmpty()) {
            redirectAttributes.addFlashAttribute("erreurOffres", "Offre introuvable.");
            return "redirect:/rh/accueil#offres";
        }

        List<PropositionEmbauche> propositions = propositionEmbaucheService.propositionsPourOffre(idOffre);
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("offre", offre.get());
        model.addAttribute("eligibles", propositionEmbaucheService.candidatsEligibles(idOffre));
        model.addAttribute("propositions", propositions);
        model.addAttribute("confirmationsEnAttente", propositions.stream()
                .filter(p -> PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION.equals(p.getStatut()))
                .toList());
        model.addAttribute("placesDemandees", propositionEmbaucheService.placesDemandees(idOffre));
        model.addAttribute("placesPrises", propositionEmbaucheService.placesPrises(idOffre));
        model.addAttribute("peutProposer", propositionEmbaucheService.peutProposer(idOffre));
        return "rh/offre-embauches";
    }

    @PostMapping("/offre/{id}/embauches/proposer")
    public String proposerEmbauche(@PathVariable("id") Long idOffre,
                                   @RequestParam Long candidatureId,
                                   @RequestParam(required = false) String commentaireRh,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }

        try {
            propositionEmbaucheService.proposer(idOffre, candidatureId,
                    SessionUtils.getUtilisateur(session).getIdUtilisateur(), commentaireRh);
            redirectAttributes.addFlashAttribute("message", "Proposition d'embauche envoyée au candidat.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/offre/" + idOffre + "/embauches";
    }

    @PostMapping("/offre/{id}/embauches/{propositionId}/conditions")
    public String traiterDemandeConditions(@PathVariable("id") Long idOffre,
                                        @PathVariable Long propositionId,
                                        @RequestParam boolean accepter,
                                        @RequestParam(required = false) String commentaireRh,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        try {
            propositionEmbaucheService.traiterDemandeConditions(
                    propositionId, SessionUtils.getUtilisateur(session).getIdUtilisateur(),
                    accepter, commentaireRh);
            redirectAttributes.addFlashAttribute("message",
                    accepter ? "La demande du candidat a été acceptée."
                            : "La demande du candidat a été refusée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/offre/" + idOffre + "/embauches";
    }

    @GetMapping("/offre/{id}/embauches/{propositionId}/confirmation")
    public String confirmationEmbauche(@PathVariable("id") Long idOffre,
                                       @PathVariable Long propositionId,
                                       HttpSession session,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        Optional<OffreEmploi> offre = offreService.findById(idOffre);
        Optional<PropositionEmbauche> proposition = propositionEmbaucheService.propositionPourOffre(idOffre, propositionId);
        if (offre.isEmpty() || proposition.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Proposition introuvable.");
            return "redirect:/rh/offre/" + idOffre + "/embauches";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("offre", offre.get());
        model.addAttribute("proposition", proposition.get());
        return "rh/confirmation-embauche";
    }

    @PostMapping("/offre/{id}/embauches/{propositionId}/confirmation")
    public String confirmerEmbauche(@PathVariable("id") Long idOffre,
                                    @PathVariable Long propositionId,
                                    @RequestParam(required = false) String commentaireRh,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        try {
            propositionEmbaucheService.confirmerEmbauche(
                    propositionId, SessionUtils.getUtilisateur(session).getIdUtilisateur(), commentaireRh);
            redirectAttributes.addFlashAttribute("message", "Embauche confirmée. Complétez maintenant la création de l'employé et du contrat.");
            return "redirect:/rh/offre/" + idOffre + "/embauches/" + propositionId + "/creation";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/offre/" + idOffre + "/embauches";
    }

    @GetMapping("/offre/{id}/embauches/{propositionId}/creation")
    public String creationEmploye(@PathVariable("id") Long idOffre,
                                  @PathVariable Long propositionId,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        Optional<OffreEmploi> offre = offreService.findById(idOffre);
        Optional<PropositionEmbauche> proposition = propositionEmbaucheService
                .propositionPourOffre(idOffre, propositionId);
        if (offre.isEmpty() || proposition.isEmpty()
                || !PropositionEmbauche.STATUT_CONFIRMEE.equals(proposition.get().getStatut())) {
            redirectAttributes.addFlashAttribute("error", "Cette proposition n'est pas disponible pour la création du contrat.");
            return "redirect:/rh/offre/" + idOffre + "/embauches";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("offre", offre.get());
        model.addAttribute("proposition", proposition.get());
        model.addAttribute("dateMin", LocalDate.now());
        return "rh/creation-employe";
    }

    @PostMapping("/offre/{id}/embauches/{propositionId}/creation")
    public String creerEmployeEtContrat(@PathVariable("id") Long idOffre,
                                        @PathVariable Long propositionId,
                                        @RequestParam boolean confirmation,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        try {
            if (!confirmation) {
                throw new IllegalArgumentException("Vous devez confirmer la création de l'employé et du contrat.");
            }
            Contrat contrat = propositionEmbaucheService.creerEmployeEtContrat(propositionId);
            redirectAttributes.addFlashAttribute("message",
                    "Employé créé et contrat " + contrat.getTypeContrat() + " activé avec succès.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", "Les informations de l'employé ou du contrat sont invalides : "
                    + ex.getMessage());
            return "redirect:/rh/offre/" + idOffre + "/embauches/" + propositionId + "/creation";
        }
        return "redirect:/rh/offre/" + idOffre + "/embauches";
    }
}
