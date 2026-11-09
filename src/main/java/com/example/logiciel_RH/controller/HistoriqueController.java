package com.example.logiciel_RH.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.service.AuditService;
import com.example.logiciel_RH.service.HistoriqueService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/employes/{id}")
public class HistoriqueController {
    private final EmployeRepository employeRepository;
    private final HistoriqueService historiqueService;
    private final AuditService auditService;
    public HistoriqueController(EmployeRepository employeRepository, HistoriqueService historiqueService, AuditService auditService) {
        this.employeRepository = employeRepository; this.historiqueService = historiqueService; this.auditService = auditService;
    }
    @GetMapping("/historique")
    public String historique(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        var employe = employeRepository.findById(id).orElse(null);
        if (employe == null) { redirectAttributes.addFlashAttribute("error", "Employé introuvable."); return "redirect:/rh/employes"; }
        var acteur = SessionUtils.getUtilisateur(session);
        auditService.enregistrer(acteur, AuditService.CONSULTATION, AuditService.EMPLOYE, id, null, null);
        model.addAttribute("utilisateurConnecte", acteur); model.addAttribute("employe", employe);
        model.addAttribute("lignes", historiqueService.historique(id));
        return "rh/historique-employe";
    }
}
