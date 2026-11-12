package com.example.logiciel_RH.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.DetailJourTravail;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.JourTravail;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.DetailJourTravailRepository;
import com.example.logiciel_RH.repository.JourTravailRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;
import com.example.logiciel_RH.service.DetailJourTravailService;
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/planning/jours")
public class DetailJourTravailController {

    private final JourTravailRepository jourRepository;
    private final EmployeRepository employeRepository;
    private final ParticipationMissionRepository participationRepository;
    private final DetailJourTravailRepository detailRepository;
    private final AuditService auditService;
    private final DetailJourTravailService detailService;

    public DetailJourTravailController(JourTravailRepository jourRepository,
                                       EmployeRepository employeRepository,
                                       ParticipationMissionRepository participationRepository,
                                       DetailJourTravailService detailService,
                                       DetailJourTravailRepository detailRepository,
                                       AuditService auditService) {
        this.jourRepository = jourRepository;
        this.employeRepository = employeRepository;
        this.participationRepository = participationRepository;
        this.detailService = detailService;
        this.detailRepository = detailRepository;
        this.auditService = auditService;
    }

    @GetMapping("/{id}/details")
    @Transactional(readOnly = true)
    public String detailEmploye(@PathVariable Long id, HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        JourTravail jour = jourRepository.findById(id).orElse(null);
        if (jour == null || employe == null || !employe.getId().equals(jour.getEmploye().getId())) {
            redirectAttributes.addFlashAttribute("error", "Journée introuvable.");
            return "redirect:/employe/jours";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("jour", jour);
        model.addAttribute("details", detailService.trouverParJour(id));
        model.addAttribute("participations", participationRepository.findByEmploye_IdOrderByDateAffectationDesc(employe.getId()));
        model.addAttribute("types", DetailJourTravail.TypeDetail.values());
        model.addAttribute("espace", "employe");
        return "detail-jour-travail";
    }

    @GetMapping("/rh/{id}/details")
    @Transactional(readOnly = true)
    public String detailRh(@PathVariable Long id, HttpSession session, Model model,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        JourTravail jour = jourRepository.findById(id).orElse(null);
        if (jour == null) {
            redirectAttributes.addFlashAttribute("error", "Journée introuvable.");
            return "redirect:/rh/planning/jours";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("jour", jour);
        model.addAttribute("details", detailService.trouverParJour(id));
        model.addAttribute("types", DetailJourTravail.TypeDetail.values());
        model.addAttribute("espace", "rh");
        return "detail-jour-travail";
    }

    @PostMapping("/{id}/details")
    public String ajouter(@PathVariable Long id,
                          @RequestParam String type,
                          @RequestParam String heureDebut,
                          @RequestParam String heureFin,
                          @RequestParam(required = false) Long participationId,
                          @RequestParam(required = false) String description,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        boolean employe = SessionUtils.hasRole(session, "EMPLOYE");
        boolean rh = SessionUtils.hasRole(session, "RH");
        if (!employe && !rh) return "redirect:/login";
        try {
            JourTravail jour = jourRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Journée introuvable."));
            Long employeId = employe ? employeConnecte(session).getId() : jour.getEmploye().getId();
            detailService.ajouter(id, employeId, type,
                    LocalTime.parse(heureDebut, DateTimeFormatter.ISO_LOCAL_TIME),
                    LocalTime.parse(heureFin, DateTimeFormatter.ISO_LOCAL_TIME),
                    participationId, description);
            redirectAttributes.addFlashAttribute("message", "Détail de journée ajouté et transmis pour vérification.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Détail non ajouté : " + ex.getMessage());
        }
        return employe ? "redirect:/planning/jours/" + id + "/details" : "redirect:/planning/jours/rh/" + id + "/details";
    }

    @PostMapping("/rh/details/{detailId}/statut")
    public String statut(@PathVariable Long detailId,
                         @RequestParam String statut,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            DetailJourTravail detail = detailRepository.findById(detailId)
                    .orElseThrow(() -> new IllegalArgumentException("Détail introuvable."));
            DetailJourTravail.Statut ancienStatut = detail.getStatut();
            DetailJourTravail.Statut nouveauStatut = DetailJourTravail.Statut.valueOf(statut);
            detailService.changerStatut(detailId, nouveauStatut);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE,
                    AuditService.DETAIL_JOUR_TRAVAIL, detailId, AuditService.valeurs("statut", ancienStatut),
                    AuditService.valeurs("statut", nouveauStatut));
            redirectAttributes.addFlashAttribute("message", "Statut du détail mis à jour.");
            return "redirect:/planning/jours/rh/" + detail.getJourTravail().getId() + "/details";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Statut non modifié : " + ex.getMessage());
        }
        return "redirect:/rh/planning/jours";
    }

    private Employe employeConnecte(HttpSession session) {
        var utilisateur = SessionUtils.getUtilisateur(session);
        return utilisateur == null ? null : employeRepository.findByPersonne_IdPersonne(utilisateur.getPersonne().getIdPersonne()).orElse(null);
    }
}
