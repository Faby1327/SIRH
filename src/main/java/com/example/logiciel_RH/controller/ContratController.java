package com.example.logiciel_RH.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.logiciel_RH.repository.ContratRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/contrats")
public class ContratController {

    private final ContratRepository contratRepository;

    public ContratController(ContratRepository contratRepository) {
        this.contratRepository = contratRepository;
    }

    @GetMapping
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) {
            return "redirect:/login";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("contrats", contratRepository.findAllByOrderByDateDebutDesc());
        return "rh/contrats";
    }
}
