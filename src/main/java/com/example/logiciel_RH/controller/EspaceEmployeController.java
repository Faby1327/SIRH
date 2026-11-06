package com.example.logiciel_RH.controller;

import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.JourTravailRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;
import com.example.logiciel_RH.service.PlanningJourService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employe")
public class EspaceEmployeController {

    private final EmployeRepository employeRepository;
    private final JourTravailRepository jourRepository;
    private final PlanningJourService planningService;
    private final ParticipationMissionRepository participationRepository;

    public EspaceEmployeController(EmployeRepository employeRepository,
                                   JourTravailRepository jourRepository,
                                   PlanningJourService planningService,
                                   ParticipationMissionRepository participationRepository) {
        this.employeRepository = employeRepository;
        this.jourRepository = jourRepository;
        this.planningService = planningService;
        this.participationRepository = participationRepository;
    }

    @GetMapping
    public String accueil() {
        return "redirect:/employe/jours";
    }

    @GetMapping("/missions")
    @Transactional(readOnly = true)
    public String missions(HttpSession session, Model model,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Votre dossier employé est introuvable.");
            return "redirect:/login";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        model.addAttribute("participations", participationRepository
                .findByEmploye_IdOrderByDateAffectationDesc(employe.getId()));
        return "employe-missions";
    }

    @GetMapping("/jours")
    @Transactional
    public String jours(@RequestParam(required = false) String mois,
                        HttpSession session, Model model,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Votre dossier employé est introuvable.");
            return "redirect:/login";
        }

        YearMonth periode = mois == null || mois.isBlank() ? YearMonth.now() : YearMonth.parse(mois);
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        model.addAttribute("jours", planningService.genererEtLister(periode).stream()
                .filter(jour -> employe.getId().equals(jour.getEmploye().getId()))
                .toList());
        model.addAttribute("periode", periode);
        model.addAttribute("periodeLibelle", periode.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + periode.getYear());
        model.addAttribute("periodePrecedente", periode.minusMonths(1));
        model.addAttribute("periodeSuivante", periode.plusMonths(1));
        return "employe";
    }

    @PostMapping("/jours/{id}/heures")
    public String enregistrerHeures(@PathVariable Long id,
                                    @RequestParam String entree,
                                    @RequestParam String sortie,
                                    @RequestParam String mois,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        try {
            Employe employe = employeConnecte(session);
            var jour = jourRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Journée introuvable."));
            if (employe == null || !employe.getId().equals(jour.getEmploye().getId())) {
                throw new IllegalArgumentException("Cette journée ne vous appartient pas.");
            }
            if ("POINTAGE_VALIDE".equals(jour.getStatut())
                    || "DETAIL_A_VERIFIER".equals(jour.getStatut())
                    || "VALIDEE".equals(jour.getStatut())) {
                throw new IllegalArgumentException("Le pointage de cette journée a déjà été validé par les RH.");
            }
            planningService.enregistrerHeures(id,
                    LocalTime.parse(entree, DateTimeFormatter.ISO_LOCAL_TIME),
                    LocalTime.parse(sortie, DateTimeFormatter.ISO_LOCAL_TIME));
            redirectAttributes.addFlashAttribute("message", "Vos heures ont été enregistrées.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Heures non enregistrées : " + ex.getMessage());
        }
        return "redirect:/employe/jours?mois=" + mois;
    }

    private Employe employeConnecte(HttpSession session) {
        var utilisateur = SessionUtils.getUtilisateur(session);
        return employeRepository.findByPersonne_IdPersonne(utilisateur.getPersonne().getIdPersonne()).orElse(null);
    }
}
