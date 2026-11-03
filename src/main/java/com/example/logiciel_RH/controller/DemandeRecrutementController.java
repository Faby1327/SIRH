package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.DemandeRecrutementService;
import com.example.logiciel_RH.service.PosteService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/demande")
public class DemandeRecrutementController {

    private final DemandeRecrutementService demandeRecrutementService;
    private final PosteService posteService;

    public DemandeRecrutementController(DemandeRecrutementService demandeRecrutementService,
                                        PosteService posteService) {
        this.demandeRecrutementService = demandeRecrutementService;
        this.posteService = posteService;
    }

    /** Recupere le manager connecte, ou null si non connecte. */
    private Utilisateur getManagerConnecte(HttpSession session) {
        return (Utilisateur) session.getAttribute("utilisateurConnecte");
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model model) {
        Utilisateur manager = getManagerConnecte(session);
        if (manager == null) {
            return "redirect:/login";
        }
        return "redirect:/manager/accueil";
    }

    @GetMapping("/new")
    public String nouveau(HttpSession session, Model model) {
        Utilisateur manager = getManagerConnecte(session);
        if (manager == null) {
            return "redirect:/login";
        }
        return "redirect:/manager/nouvelle";
    }

    @PostMapping("/save")
    public String save(@RequestParam Long idPoste,
                       @RequestParam Integer nombrePersonnes,
                       @RequestParam BigDecimal budgetDemande,
                       @RequestParam String motif,
                       @RequestParam(required = false) String dateSouhaitee,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {

        Utilisateur manager = getManagerConnecte(session);
        if (manager == null) {
            return "redirect:/login";
        }

        LocalDate date = (dateSouhaitee != null && !dateSouhaitee.isEmpty())
                ? LocalDate.parse(dateSouhaitee) : null;

        demandeRecrutementService.creerDemande(
                manager, idPoste, nombrePersonnes, budgetDemande, motif, date);

        redirectAttributes.addFlashAttribute("message",
                "Demande de recrutement soumise avec succès.");

        return "redirect:/manager/accueil";
    }
}
