package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
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

import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.DepartementRepository;
import com.example.logiciel_RH.service.DemandeRecrutementService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final DemandeRecrutementService demandeService;
    private final DepartementRepository departementRepository;

    public FinanceController(DemandeRecrutementService demandeService, DepartementRepository departementRepository) {
        this.demandeService = demandeService;
        this.departementRepository = departementRepository;
    }

    @GetMapping("/accueil")
    public String accueil(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "FINANCIER")) {
            return "redirect:/login";
        }

        List<DemandeRecrutement> demandes = demandeService.findByStatuts(Arrays.asList(
                "EN_VERIFICATION_FINANCIERE",
                "BUDGET_INSUFFISANT",
                "EN_ATTENTE_VALIDATION",
                "REFUSEE",
                "VALIDEE",
                "ANNULEE"
        ));

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("demandes", demandes);
        model.addAttribute("departements", departementRepository.findAll());
        Map<Long, String> statutsRole = new HashMap<>();
        demandes.forEach(d -> statutsRole.put(d.getIdDemande(), demandeService.getStatutParRole(d, "FINANCIERE")));
        demandes.removeIf(d -> "EN_ATTENTE".equals(statutsRole.get(d.getIdDemande()))
                && !"EN_VERIFICATION_FINANCIERE".equals(d.getStatutActuel().getLibelle()));
        model.addAttribute("demandes", demandes);
        model.addAttribute("statutsRole", statutsRole);
        Map<Long, Boolean> compatibilites = new HashMap<>();
        Map<Long, Boolean> compatibilitesFourchette = new HashMap<>();
        Map<Long, Boolean> compatibilitesEntreprise = new HashMap<>();
        demandes.forEach(demande -> compatibilites.put(demande.getIdDemande(), demandeService.estBudgetCompatible(demande)));
        demandes.forEach(demande -> compatibilitesFourchette.put(
                demande.getIdDemande(), demandeService.estBudgetDansFourchette(demande)));
        demandes.forEach(demande -> compatibilitesEntreprise.put(
                demande.getIdDemande(), demandeService.estBudgetEntrepriseCompatible(demande)));
        model.addAttribute("compatibilites", compatibilites);
        model.addAttribute("compatibilitesFourchette", compatibilitesFourchette);
        model.addAttribute("compatibilitesEntreprise", compatibilitesEntreprise);
        model.addAttribute("aVerifier", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("EN_ATTENTE"::equals).count());
        model.addAttribute("validees", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("VALIDEE"::equals).count());
        model.addAttribute("refusees", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("REFUSEE"::equals).count());
        model.addAttribute("incompatibles", compatibilites.values().stream().filter(compatible -> !compatible).count());

        return "finance/accueil";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long idDemande,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "FINANCIER")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/finance/accueil";
        }

        DemandeRecrutement valeur = demande.get();
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("demande", valeur);
        model.addAttribute("historique", demandeService.findHistorique(idDemande));
        model.addAttribute("etapeRefus", demandeService.getEtapeRefus(valeur));
        model.addAttribute("budgetCompatible", demandeService.estBudgetCompatible(valeur));
        model.addAttribute("budgetDansFourchette", demandeService.estBudgetDansFourchette(valeur));
        model.addAttribute("budgetEntrepriseCompatible", demandeService.estBudgetEntrepriseCompatible(valeur));
        model.addAttribute("budgetMin", demandeService.getBudgetMin(valeur.getPoste().getIdPoste()));
        model.addAttribute("budgetMax", demandeService.getBudgetMax(valeur.getPoste().getIdPoste()));
        model.addAttribute("budgetEntrepriseDisponible", demandeService.getBudgetEntrepriseDisponible(valeur));
        model.addAttribute("budgetTotalEstime", valeur.getBudgetTotalEstime());
        model.addAttribute("anneeBudget", valeur.getDateDemande() == null ? null : valeur.getDateDemande().getYear());

        return "finance/detail";
    }

    @PostMapping("/valider/{id}")
    public String valider(@PathVariable("id") Long idDemande,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "FINANCIER")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/finance/accueil";
        }

        DemandeRecrutement valeur = demande.get();
        if (!demandeService.estBudgetCompatible(valeur)) {
            redirectAttributes.addFlashAttribute("error", "Budget incompatible, validation impossible.");
            return "redirect:/finance/detail/" + idDemande;
        }

        Utilisateur finance = SessionUtils.getUtilisateur(session);
        demandeService.changerStatut(valeur, "EN_ATTENTE_VALIDATION", finance, "Validation Finance");
        redirectAttributes.addFlashAttribute("message", "Budget valide, demande transmise a la Direction.");

        return "redirect:/finance/accueil";
    }

    @PostMapping("/refuser/{id}")
    public String refuser(@PathVariable("id") Long idDemande,
                          @RequestParam(required = false) String remarque,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "FINANCIER")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/finance/accueil";
        }

        DemandeRecrutement valeur = demande.get();
        String commentaire = remarque;
        if (commentaire == null || commentaire.isBlank()) {
            BigDecimal min = demandeService.getBudgetMin(valeur.getPoste().getIdPoste());
            BigDecimal max = demandeService.getBudgetMax(valeur.getPoste().getIdPoste());
            BigDecimal budgetEntreprise = demandeService.getBudgetEntrepriseDisponible(valeur);
            commentaire = "Budget incompatible : montant unitaire attendu entre " + min + " et " + max
                    + ", coût total estimé " + valeur.getBudgetTotalEstime() + " pour un budget entreprise de "
                    + budgetEntreprise + ".";
        }

        Utilisateur finance = SessionUtils.getUtilisateur(session);
        demandeService.changerStatut(valeur, "BUDGET_INSUFFISANT", finance, commentaire);
        redirectAttributes.addFlashAttribute("message", "Demande refusee par la Finance.");

        return "redirect:/finance/accueil";
    }

    @GetMapping("/budgets")
    public String budgets(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "FINANCIER")) {
            return "redirect:/login";
        }

        List<DemandeRecrutement> demandes = demandeService.findAllOrdered();
        Map<Long, BigDecimal> minima = new HashMap<>();
        Map<Long, BigDecimal> maxima = new HashMap<>();
        demandes.forEach(d -> {
            minima.put(d.getIdDemande(), demandeService.getBudgetMin(d.getPoste().getIdPoste()));
            maxima.put(d.getIdDemande(), demandeService.getBudgetMax(d.getPoste().getIdPoste()));
        });
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("demandes", demandes);
        model.addAttribute("budgetMinima", minima);
        model.addAttribute("budgetMaxima", maxima);
        return "finance/budgets";
    }
}
