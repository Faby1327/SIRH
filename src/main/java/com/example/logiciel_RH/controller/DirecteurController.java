package com.example.logiciel_RH.controller;

import java.util.Optional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/directeur")
public class DirecteurController {

    private final DemandeRecrutementService demandeService;
    private final DepartementRepository departementRepository;
    private final AuditService auditService;

    public DirecteurController(DemandeRecrutementService demandeService, DepartementRepository departementRepository,
                              AuditService auditService) {
        this.demandeService = demandeService;
        this.departementRepository = departementRepository;
        this.auditService = auditService;
    }

    @GetMapping("/accueil")
    public String accueil(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "DIRECTEUR")) {
            return "redirect:/login";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        List<DemandeRecrutement> demandes = demandeService.findByStatuts(List.of(
                "EN_ATTENTE_VALIDATION",
                "VALIDEE",
                "REFUSEE",
                "ANNULEE"
        ));
        model.addAttribute("demandes", demandes);
        model.addAttribute("departements", departementRepository.findAll());
        Map<Long, String> statutsRole = new HashMap<>();
        demandes.forEach(d -> statutsRole.put(d.getIdDemande(), demandeService.getStatutParRole(d, "FINALE")));
        demandes.removeIf(d -> "EN_ATTENTE".equals(statutsRole.get(d.getIdDemande()))
                && !"EN_ATTENTE_VALIDATION".equals(d.getStatutActuel().getLibelle()));
        model.addAttribute("demandes", demandes);
        model.addAttribute("statutsRole", statutsRole);
        model.addAttribute("aApprouver", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("EN_ATTENTE"::equals).count());
        model.addAttribute("approuvees", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("VALIDEE"::equals).count());
        model.addAttribute("refusees", demandes.stream().map(d -> statutsRole.get(d.getIdDemande())).filter("REFUSEE"::equals).count());
        return "directeur/accueil";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long idDemande,
                         HttpSession session,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "DIRECTEUR")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/directeur/accueil";
        }

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("demande", demande.get());
        model.addAttribute("historique", demandeService.findHistorique(idDemande));
        model.addAttribute("etapeRefus", demandeService.getEtapeRefus(demande.get()));
        model.addAttribute("budgetCompatible", demandeService.estBudgetCompatible(demande.get()));
        model.addAttribute("budgetMin", demandeService.getBudgetMin(demande.get().getPoste().getIdPoste()));
        model.addAttribute("budgetMax", demandeService.getBudgetMax(demande.get().getPoste().getIdPoste()));

        return "directeur/detail";
    }

    @PostMapping("/approuver/{id}")
    public String approuver(@PathVariable("id") Long idDemande,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "DIRECTEUR")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/directeur/accueil";
        }

        Utilisateur directeur = SessionUtils.getUtilisateur(session);
        String ancienStatut = demande.get().getStatutActuel() == null ? null : demande.get().getStatutActuel().getLibelle();
        demandeService.changerStatut(demande.get(), "VALIDEE", directeur, "Validation Direction");
        auditService.enregistrer(directeur, AuditService.UPDATE, AuditService.DEMANDE_RECRUTEMENT, idDemande,
                AuditService.valeurs("statut", ancienStatut), AuditService.valeurs("statut", "VALIDEE", "decision", "APPROBATION_DIRECTION"));
        redirectAttributes.addFlashAttribute("message", "Demande approuvee.");

        return "redirect:/directeur/accueil";
    }

    @PostMapping("/refuser/{id}")
    public String refuser(@PathVariable("id") Long idDemande,
                          @RequestParam String remarque,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "DIRECTEUR")) {
            return "redirect:/login";
        }

        Optional<DemandeRecrutement> demande = demandeService.findById(idDemande);
        if (demande.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Demande introuvable.");
            return "redirect:/directeur/accueil";
        }

        Utilisateur directeur = SessionUtils.getUtilisateur(session);
        String ancienStatut = demande.get().getStatutActuel() == null ? null : demande.get().getStatutActuel().getLibelle();
        demandeService.changerStatut(demande.get(), "REFUSEE", directeur, remarque);
        auditService.enregistrer(directeur, AuditService.UPDATE, AuditService.DEMANDE_RECRUTEMENT, idDemande,
                AuditService.valeurs("statut", ancienStatut), AuditService.valeurs("statut", "REFUSEE", "decision", "REFUS_DIRECTION", "motif", remarque));
        redirectAttributes.addFlashAttribute("message", "Demande refusee.");

        return "redirect:/directeur/accueil";
    }

    @GetMapping("/budgets")
    public String budgets(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "DIRECTEUR")) {
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
        return "directeur/budgets";
    }
}
