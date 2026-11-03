package com.example.logiciel_RH.controller;

import java.util.Optional;
import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Candidat;
import com.example.logiciel_RH.entity.PropositionEmbauche;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.CandidatService;
import com.example.logiciel_RH.service.PropositionEmbaucheService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/candidat")
public class CandidatController {

    private final CandidatService candidatService;
    private final PropositionEmbaucheService propositionEmbaucheService;

    public CandidatController(CandidatService candidatService,
                              PropositionEmbaucheService propositionEmbaucheService) {
        this.candidatService = candidatService;
        this.propositionEmbaucheService = propositionEmbaucheService;
    }

    @GetMapping("/proposition")
    public String proposition(HttpSession session, Model model) {
        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null || !"CANDIDAT".equals(utilisateur.getRole())) {
            return "redirect:/login";
        }

        Optional<Candidat> candidat = candidatService.parUtilisateur(utilisateur);
        if (candidat.isEmpty()) {
            return "redirect:/";
        }

        Optional<PropositionEmbauche> proposition = propositionEmbaucheService
                .propositionActivePourCandidat(candidat.get().getIdCandidat());
        model.addAttribute("utilisateurConnecte", utilisateur);
        model.addAttribute("candidat", candidat.get());
        model.addAttribute("proposition", proposition.orElse(null));
        return "candidat/proposition";
    }

    @GetMapping("/espace")
    public String espace(HttpSession session, Model model) {
        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null || !"CANDIDAT".equals(utilisateur.getRole())) {
            return "redirect:/login";
        }

        Optional<Candidat> candidat = candidatService.parUtilisateur(utilisateur);
        if (candidat.isEmpty()) {
            return "redirect:/";
        }

        Optional<PropositionEmbauche> proposition = propositionEmbaucheService
                .propositionActivePourCandidat(candidat.get().getIdCandidat());
        model.addAttribute("utilisateurConnecte", utilisateur);
        model.addAttribute("candidat", candidat.get());
        model.addAttribute("proposition", proposition.orElse(null));
        return "candidat/espace";
    }

    @PostMapping("/proposition/accepter")
    public String accepter(@RequestParam Long propositionId, HttpSession session,
                           RedirectAttributes redirectAttributes) {
        return repondre(propositionId, null, null, null, true, session, redirectAttributes);
    }

    @PostMapping("/proposition/refuser")
    public String refuser(@RequestParam Long propositionId,
                          @RequestParam(required = false) String commentaire,
                          HttpSession session, RedirectAttributes redirectAttributes) {
        return repondre(propositionId, null, null, commentaire, false, session, redirectAttributes);
    }

    @PostMapping("/proposition/demander-salaire")
    public String demanderSalaire(@RequestParam Long propositionId,
                                  @RequestParam(required = false) BigDecimal salaireDemande,
                                  @RequestParam(required = false) String typeContratDemande,
                                  @RequestParam(required = false) String commentaire,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (salaireDemande == null && (typeContratDemande == null || typeContratDemande.isBlank())) {
            redirectAttributes.addFlashAttribute("error",
                    "Indiquez un salaire et/ou un type de contrat à demander.");
            return "redirect:/candidat/proposition";
        }
        return repondre(propositionId, salaireDemande, typeContratDemande, commentaire, false, session, redirectAttributes);
    }

    private String repondre(Long propositionId, BigDecimal salaireDemande, String typeContratDemande, String commentaire,
                            boolean accepter, HttpSession session, RedirectAttributes redirectAttributes) {
        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null || !"CANDIDAT".equals(utilisateur.getRole())) {
            return "redirect:/login";
        }
        try {
            Candidat candidat = candidatService.parUtilisateur(utilisateur)
                    .orElseThrow(() -> new IllegalArgumentException("Profil candidat introuvable."));
            propositionEmbaucheService.repondreCandidat(propositionId, candidat.getIdCandidat(),
                    salaireDemande, typeContratDemande, commentaire, accepter);
            redirectAttributes.addFlashAttribute("message", accepter
                    ? "Proposition acceptée."
                    : salaireDemande != null || (typeContratDemande != null && !typeContratDemande.isBlank())
                    ? "Votre demande de modification a été envoyée aux RH."
                    : "Proposition refusée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage() == null
                    ? "Impossible d'enregistrer votre réponse. Veuillez réessayer."
                    : ex.getMessage());
        }
        return "redirect:/candidat/proposition";
    }
}
