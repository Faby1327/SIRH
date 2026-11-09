package com.example.logiciel_RH.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.service.HeureSupplementaireService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/heures-supplementaires")
public class HeureSupplementaireController {

    private final HeureSupplementaireService service;

    public HeureSupplementaireController(HeureSupplementaireService service) {
        this.service = service;
    }

    @GetMapping
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("heuresSupplementaires", service.lister());
        return "rh/heures-supplementaires";
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            service.valider(id);
            redirectAttributes.addFlashAttribute("message", "Heure supplémentaire validée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Validation impossible : " + ex.getMessage());
        }
        return "redirect:/rh/heures-supplementaires";
    }

    @PostMapping("/{id}/refuser")
    public String refuser(@PathVariable Long id, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            service.refuser(id);
            redirectAttributes.addFlashAttribute("message", "Heure supplémentaire refusée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Refus impossible : " + ex.getMessage());
        }
        return "redirect:/rh/heures-supplementaires";
    }
}
