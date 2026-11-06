package com.example.logiciel_RH.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.CandidatService;
import com.example.logiciel_RH.service.UtilisateurService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UtilisateurService utilisateurService;
    private final CandidatService candidatService;

    public LoginController(UtilisateurService utilisateurService,
                           CandidatService candidatService) {
        this.utilisateurService = utilisateurService;
        this.candidatService = candidatService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String next, Model model) {
        model.addAttribute("next", next);
        return "login/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String motDePasse,
                        @RequestParam(required = false) String next,
                        Model model,
                        HttpSession session) {

        Utilisateur utilisateur = utilisateurService.login(email, motDePasse);

        if (utilisateur == null) {
            model.addAttribute("error", "Email ou mot de passe incorrect.");
            model.addAttribute("next", next);
            return "login/login";
        }

        session.setAttribute("utilisateurConnecte", utilisateur);

        // Retour direct au formulaire de candidature, uniquement pour un chemin
        // interne aux offres : pas d'open redirect.
        if ("CANDIDAT".equals(utilisateur.getRole()) && nextValide(next)) {
            return "redirect:" + next;
        }

        return "redirect:" + switch (utilisateur.getRole()) {
            case "MANAGER" -> "/manager/accueil";
            case "RH" -> "/rh/accueil";
            case "FINANCIER" -> "/finance/accueil";
            case "DIRECTEUR" -> "/directeur/accueil";
            case "CANDIDAT" -> "/candidat/espace";
            case "EMPLOYE" -> "/employe/jours";
            default -> "/login";
        };
    }

    /** Seuls les chemins internes des offres sont acceptés. */
    private static boolean nextValide(String next) {
        return next != null
                && next.startsWith("/offres/")
                && !next.startsWith("//")
                && !next.contains("..")
                && !next.contains("\r")
                && !next.contains("\n");
    }

    @GetMapping("/inscription")
    public String inscriptionPage() {
        return "candidat/inscription";
    }

    @PostMapping("/inscription")
    public String inscription(@RequestParam String nom,
                              @RequestParam String prenom,
                              @RequestParam String email,
                              @RequestParam(required = false) String telephone,
                              @RequestParam(required = false) String ville,
                              @RequestParam String motDePasse,
                              @RequestParam(required = false) String confirmation,
                              Model model) {
        String erreur = validerInscription(nom, prenom, email, telephone, motDePasse, confirmation);
        if (erreur != null) {
            model.addAttribute("error", erreur);
            return "candidat/inscription";
        }

        try {
            candidatService.inscrire(nom, prenom, email, telephone, ville, motDePasse);
            return "redirect:/login?inscription=ok";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "candidat/inscription";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "Un compte existe déjà avec cette adresse email.");
            return "candidat/inscription";
        }
    }

    private String validerInscription(String nom,
                                      String prenom,
                                      String email,
                                      String telephone,
                                      String motDePasse,
                                      String confirmation) {
        if (estVide(nom) || estVide(prenom)) {
            return "Indiquez votre nom et votre prénom.";
        }
        if (estVide(email) || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "L'adresse email n'est pas valide.";
        }
        if (estVide(telephone)) {
            return "Indiquez votre numéro de téléphone.";
        }
        if (motDePasse == null || motDePasse.length() < 8) {
            return "Le mot de passe doit contenir au moins 8 caractères.";
        }
        if (!motDePasse.equals(confirmation)) {
            return "Les deux mots de passe ne correspondent pas.";
        }
        return null;
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
