package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.service.PlanningJourService;
import com.example.logiciel_RH.service.AuditService;
import com.example.logiciel_RH.repository.JourTravailRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/planning")
public class PlanningJourController {

    private final PlanningJourService planningService;
    private final JourTravailRepository jourRepository;
    private final AuditService auditService;

    public PlanningJourController(PlanningJourService planningService, JourTravailRepository jourRepository,
                                  AuditService auditService) {
        this.planningService = planningService;
        this.jourRepository = jourRepository;
        this.auditService = auditService;
    }

    @GetMapping("/jours")
    public String jours(@RequestParam(required = false) String mois,
                        HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        YearMonth periode = mois == null || mois.isBlank() ? YearMonth.now() : YearMonth.parse(mois);
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("jours", planningService.genererEtLister(periode));
        model.addAttribute("periode", periode);
        model.addAttribute("periodeLibelle", periode.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + periode.getYear());
        model.addAttribute("periodePrecedente", periode.minusMonths(1));
        model.addAttribute("periodeSuivante", periode.plusMonths(1));
        return "rh/planning-jours";
    }

    @PostMapping("/jours/{id}/heures")
    public String enregistrerHeures(@PathVariable Long id,
                                    @RequestParam String entree,
                                    @RequestParam String sortie,
                                    @RequestParam String mois,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            planningService.enregistrerHeures(id,
                    LocalTime.parse(entree, DateTimeFormatter.ISO_LOCAL_TIME),
                    LocalTime.parse(sortie, DateTimeFormatter.ISO_LOCAL_TIME));
            redirectAttributes.addFlashAttribute("message", "Les heures de la journée ont été enregistrées.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Heures non enregistrées : " + ex.getMessage());
        }
        return "redirect:/rh/planning/jours?mois=" + mois;
    }

    @PostMapping("/jours/{id}/valider")
    public String valider(@PathVariable Long id,
                          @RequestParam String mois,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            var jour = jourRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Journée introuvable."));
            String ancienStatut = jour.getStatut();
            planningService.validerJournee(id);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE, AuditService.JOUR_TRAVAIL,
                    id, AuditService.valeurs("statut", ancienStatut), AuditService.valeurs("statut", "POINTAGE_VALIDE"));
            redirectAttributes.addFlashAttribute("message", "Journée validée par les RH.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Validation impossible : " + ex.getMessage());
        }
        return "redirect:/rh/planning/jours?mois=" + mois;
    }
}
