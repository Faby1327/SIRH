package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.example.logiciel_RH.entity.HistoriqueEmploye;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.PosteRepository;
import com.example.logiciel_RH.service.AuditService;
import com.example.logiciel_RH.service.HistoriqueService;

import jakarta.servlet.http.HttpSession;

/** Historique des changements professionnels d'un employé (bloc 5). */
@Controller
@RequestMapping("/rh/employes/{id}")
public class HistoriqueController {

    private final EmployeRepository employeRepository;
    private final PosteRepository posteRepository;
    private final HistoriqueService historiqueService;
    private final AuditService auditService;

    public HistoriqueController(EmployeRepository employeRepository,
                                PosteRepository posteRepository,
                                HistoriqueService historiqueService,
                                AuditService auditService) {
        this.employeRepository = employeRepository;
        this.posteRepository = posteRepository;
        this.historiqueService = historiqueService;
        this.auditService = auditService;
    }

    @GetMapping("/historique")
    public String historique(@PathVariable("id") Long id, HttpSession session, Model model,
                             RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        Utilisateur acteur = SessionUtils.getUtilisateur(session);
        auditService.enregistrer(acteur, AuditService.CONSULTATION, AuditService.EMPLOYE, id, null, null);

        List<HistoriqueEmploye> lignes = historiqueService.historique(id);
        List<Employe> superieurs = new ArrayList<>(employeRepository.findAll());
        superieurs.removeIf(e -> e.getId().equals(id));

        model.addAttribute("utilisateurConnecte", acteur);
        model.addAttribute("employe", employe);
        model.addAttribute("lignes", lignes);
        model.addAttribute("courant", lignes.isEmpty() ? null : lignes.get(0));
        model.addAttribute("postes", posteRepository.findAll());
        model.addAttribute("superieurs", superieurs);
        return "audit/historiqueEmploye";
    }

    @PostMapping("/historique")
    public String enregistrer(@PathVariable("id") Long id,
                              @RequestParam("posteId") Long posteId,
                              @RequestParam("salaire") BigDecimal salaire,
                              @RequestParam("dateEffet")
                              @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEffet,
                              @RequestParam(name = "superieurId", required = false) Long superieurId,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            Poste poste = posteRepository.findById(posteId)
                    .orElseThrow(() -> new IllegalArgumentException("Poste introuvable."));
            Employe superieur = superieurId == null ? null
                    : employeRepository.findById(superieurId)
                            .orElseThrow(() -> new IllegalArgumentException("Supérieur introuvable."));
            historiqueService.modifierSituationProfessionnelle(
                    SessionUtils.getUtilisateur(session), employe, poste, salaire, dateEffet, superieur);
            redirectAttributes.addFlashAttribute("message",
                    "Changement professionnel enregistré dans l'historique.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id + "/historique";
    }
}