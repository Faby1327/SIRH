package com.example.logiciel_RH.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.logiciel_RH.repository.DocumentEmployeRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.ContratRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employe")
public class ProfilEmployeController {
    private final EmployeRepository employeRepository;
    private final DocumentEmployeRepository documentRepository;
    private final ContratRepository contratRepository;

    public ProfilEmployeController(EmployeRepository employeRepository, DocumentEmployeRepository documentRepository,
                                   ContratRepository contratRepository) {
        this.employeRepository = employeRepository; this.documentRepository = documentRepository;
        this.contratRepository = contratRepository;
    }

    @GetMapping("/profil")
    @Transactional(readOnly = true)
    public String profil(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        var utilisateur = SessionUtils.getUtilisateur(session);
        var employe = utilisateur == null || utilisateur.getPersonne() == null ? null
                : employeRepository.findByPersonne_IdPersonne(utilisateur.getPersonne().getIdPersonne()).orElse(null);
        if (employe == null) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", utilisateur); model.addAttribute("employe", employe);
        model.addAttribute("documents", documentRepository.findByEmploye_IdOrderByDateAjoutDesc(employe.getId()));
        model.addAttribute("contrats", contratRepository.findByEmploye_IdOrderByDateDebutDesc(employe.getId()));
        return "employe-profil";
    }
}
