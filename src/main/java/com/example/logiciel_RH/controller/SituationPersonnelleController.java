package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.HistoriqueSituationPersonnelle;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.service.AuditService;
import com.example.logiciel_RH.service.SituationPersonnelleService;

import jakarta.servlet.http.HttpSession;

/** Historique des changements personnels d'un employé (bloc 5). */
@Controller
@RequestMapping("/rh/employes/{id}")
public class SituationPersonnelleController {

    private final EmployeRepository employeRepository;
    private final SituationPersonnelleService situationService;
    private final AuditService auditService;

    public SituationPersonnelleController(EmployeRepository employeRepository,
                                          SituationPersonnelleService situationService,
                                          AuditService auditService) {
        this.employeRepository = employeRepository;
        this.situationService = situationService;
        this.auditService = auditService;
    }

    @GetMapping("/situation")
    public String situation(@PathVariable("id") Long id, HttpSession session, Model model,
                            RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        Utilisateur acteur = SessionUtils.getUtilisateur(session);
        auditService.enregistrer(acteur, AuditService.CONSULTATION, AuditService.EMPLOYE, id, null, null);

        List<HistoriqueSituationPersonnelle> lignes = situationService.historique(id);
        model.addAttribute("utilisateurConnecte", acteur);
        model.addAttribute("employe", employe);
        model.addAttribute("lignes", lignes);
        model.addAttribute("courante", lignes.isEmpty() ? null : lignes.get(0));
        model.addAttribute("situations", SituationPersonnelleService.SITUATIONS);
        return "audit/historiquePersonnel";
    }

    @PostMapping("/situation")
    public String enregistrer(@PathVariable("id") Long id,
                              @RequestParam(name = "telephone", required = false) String telephone,
                              @RequestParam(name = "emailPersonnel", required = false) String emailPersonnel,
                              @RequestParam(name = "adresse", required = false) String adresse,
                              @RequestParam(name = "commune", required = false) String commune,
                              @RequestParam(name = "region", required = false) String region,
                              @RequestParam(name = "situationMatrimoniale", required = false) String situationMatrimoniale,
                              @RequestParam(name = "nombreEnfants", required = false) Integer nombreEnfants,
                              @RequestParam(name = "contactUrgenceNom", required = false) String contactUrgenceNom,
                              @RequestParam(name = "contactUrgenceRelation", required = false) String contactUrgenceRelation,
                              @RequestParam(name = "contactUrgenceTelephone", required = false) String contactUrgenceTelephone,
                              @RequestParam("dateEffet")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEffet,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            situationService.enregistrer(SessionUtils.getUtilisateur(session), employe,
                    telephone, emailPersonnel, adresse, commune, region,
                    situationMatrimoniale, nombreEnfants,
                    contactUrgenceNom, contactUrgenceRelation, contactUrgenceTelephone, dateEffet);
            redirectAttributes.addFlashAttribute("message",
                    "Situation personnelle enregistrée dans l'historique.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id + "/situation";
    }
}