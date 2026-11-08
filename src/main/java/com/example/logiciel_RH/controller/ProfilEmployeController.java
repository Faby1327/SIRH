package com.example.logiciel_RH.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.EmployeService;

import jakarta.servlet.http.HttpSession;

/** Un employé consulte son propre profil (lecture seule). */
@Controller
@RequestMapping("/employe")
public class ProfilEmployeController {

    private final EmployeService employeService;

    public ProfilEmployeController(EmployeService employeService) {
        this.employeService = employeService;
    }

    @GetMapping("/profil")
    public String profil(HttpSession session, Model model) {
        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null) return "redirect:/login";
        Employe employe = employeService.pourUtilisateur(utilisateur);
        if (employe == null) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", utilisateur);
        model.addAttribute("employe", employe);
        model.addAttribute("documents", employeService.documents(employe.getId()));
        return "employe/profilEmploye";
    }
}
