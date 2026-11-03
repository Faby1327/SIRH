package com.example.logiciel_RH.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Candidat;
import com.example.logiciel_RH.entity.CritereCandidature;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.service.CandidatService;
import com.example.logiciel_RH.service.CandidatureService;
import com.example.logiciel_RH.service.OffreEmploiService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Site public de recrutement (bloc 2 — Recrutement).
 *
 * La consultation des offres est libre. La candidature demande un compte
 * candidat : l'identité est alors reprise du profil, pas du formulaire.
 */
@Controller
public class OffresController {

    private final OffreEmploiService offreService;
    private final CandidatureService candidatureService;
    private final CandidatService candidatService;

    public OffresController(OffreEmploiService offreService,
                            CandidatureService candidatureService,
                            CandidatService candidatService) {
        this.offreService = offreService;
        this.candidatureService = candidatureService;
        this.candidatService = candidatService;
    }

    /** Accueil public : présentation et contact du service RH. */
    @GetMapping("/")
    public String accueil(Model model, HttpSession session) {
        ajouterContexte(session, model);
        return "public/index";
    }

    /** Liste des offres publiées par les RH. */
    @GetMapping("/offres")
    public String liste(Model model, HttpSession session) {
        model.addAttribute("offres", offreService.findPubliees());
        ajouterContexte(session, model);
        return "public/offres";
    }

    @GetMapping("/offres/{id}")
    public String detail(@PathVariable("id") Long idOffre,
                         Model model,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        OffreEmploi offre = offreService.findPublique(idOffre).orElse(null);
        if (offre == null) {
            redirectAttributes.addFlashAttribute("error", "Cette offre n'est plus disponible.");
            return "redirect:/offres";
        }

        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        Optional<Candidat> candidat = candidatService.parUtilisateur(utilisateur);

        model.addAttribute("offre", offre);
        model.addAttribute("candidatConnecte", candidat.isPresent());
        model.addAttribute("dejaPostule", candidat
                .map(c -> candidatureService.dejaPostule(idOffre, c))
                .orElse(false));
        ajouterContexte(utilisateur, model);
        return "public/offre-detail";
    }

    // ------------------------------------------------------------------
    // Formulaire de candidature (bloc 2 — Recrutement)
    // ------------------------------------------------------------------

    @GetMapping("/offres/{id}/postuler")
    public String formulaire(@PathVariable("id") Long idOffre,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        OffreEmploi offre = offreService.findPublique(idOffre).orElse(null);
        if (offre == null) {
            redirectAttributes.addFlashAttribute("error", "Cette offre n'est plus disponible.");
            return "redirect:/offres";
        }

        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null) {
            return "redirect:/login?next=/offres/" + idOffre + "/postuler";
        }
        if (!"CANDIDAT".equals(utilisateur.getRole())) {
            redirectAttributes.addFlashAttribute("error",
                    "Connectez-vous avec un compte candidat pour postuler à cette offre.");
            return "redirect:/offres/" + idOffre;
        }

        Optional<Candidat> candidat = candidatService.parUtilisateur(utilisateur);
        if (candidat.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Votre profil candidat est incomplet. Reconnectez-vous ou créez un nouveau compte.");
            return "redirect:/offres/" + idOffre;
        }

        if (candidatureService.dejaPostule(idOffre, candidat.get())) {
            redirectAttributes.addFlashAttribute("error",
                    "Vous avez déjà postulé à cette offre.");
            return "redirect:/offres/" + idOffre;
        }

        model.addAttribute("offre", offre);
        model.addAttribute("candidat", candidat.get());
        model.addAttribute("criteres", candidatureService.criteresPour(offre));
        model.addAttribute("valeurs", new LinkedHashMap<Long, String>());
        ajouterContexte(utilisateur, model);
        return "offres/postuler";
    }

    @PostMapping("/offres/{id}/postuler")
    public String postuler(@PathVariable("id") Long idOffre,
                           @RequestParam(value = "message", required = false) String message,
                           HttpServletRequest request,
                           HttpSession session,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        OffreEmploi offre = offreService.findPublique(idOffre).orElse(null);
        if (offre == null) {
            redirectAttributes.addFlashAttribute("error", "Cette offre n'est plus disponible.");
            return "redirect:/offres";
        }

        Utilisateur utilisateur = SessionUtils.getUtilisateur(session);
        if (utilisateur == null) {
            return "redirect:/login?next=/offres/" + idOffre + "/postuler";
        }
        if (!"CANDIDAT".equals(utilisateur.getRole())) {
            redirectAttributes.addFlashAttribute("error",
                    "Connectez-vous avec un compte candidat pour postuler à cette offre.");
            return "redirect:/offres/" + idOffre;
        }

        Optional<Candidat> candidatOptional = candidatService.parUtilisateur(utilisateur);
        if (candidatOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Votre profil candidat est incomplet. Reconnectez-vous ou créez un nouveau compte.");
            return "redirect:/offres/" + idOffre;
        }
        Candidat candidat = candidatOptional.get();

        List<CritereCandidature> criteres = candidatureService.criteresPour(offre);
        Map<Long, String> valeurs = new LinkedHashMap<>();

        // Les cases à cocher renvoient plusieurs valeurs pour un même critère.
        for (CritereCandidature critere : criteres) {
            String[] brut = request.getParameterValues("critere_" + critere.getIdCritere());
            if (brut == null) {
                continue;
            }
            List<String> choix = Arrays.stream(brut)
                    .filter(valeur -> valeur != null && !valeur.isBlank())
                    .map(String::trim)
                    .toList();
            if (!choix.isEmpty()) {
                valeurs.put(critere.getIdCritere(), String.join(", ", choix));
            }
        }

        String erreur = valider(candidat, criteres, valeurs);
        if (erreur == null && candidatureService.dejaPostule(idOffre, candidat)) {
            erreur = "Vous avez déjà postulé à cette offre.";
        }

        if (erreur != null) {
            model.addAttribute("offre", offre);
            model.addAttribute("candidat", candidat);
            model.addAttribute("criteres", criteres);
            model.addAttribute("valeurs", valeurs);
            model.addAttribute("message", message);
            model.addAttribute("error", erreur);
            ajouterContexte(utilisateur, model);
            return "offres/postuler";
        }

        candidatureService.enregistrer(offre, candidat, message, valeurs);
        redirectAttributes.addFlashAttribute("messageCandidature",
                "Votre candidature a bien été envoyée. Le service RH la consultera prochainement.");
        return "redirect:/offres/" + idOffre;
    }

    /** Contrôles simples : profil complet et critères obligatoires renseignés. */
    private String valider(Candidat candidat,
                           List<CritereCandidature> criteres,
                           Map<Long, String> valeurs) {
        if (candidat.getUtilisateur() == null
                || estVide(candidat.getNom())
                || estVide(candidat.getPrenom())) {
            return "Votre profil candidat est incomplet.";
        }
        String email = candidat.getEmail();
        if (estVide(email) || !email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "L'adresse email de votre compte n'est pas valide.";
        }
        if (estVide(candidat.getTelephone())) {
            return "Indiquez votre numéro de téléphone dans votre profil candidat.";
        }
        for (CritereCandidature critere : criteres) {
            if (critere.isObligatoire() && estVide(valeurs.get(critere.getIdCritere()))) {
                return "Le champ « " + critere.getLibelle() + " » est obligatoire.";
            }
        }
        return null;
    }

    private boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

    // ------------------------------------------------------------------
    // Contexte commun aux pages publiques
    // ------------------------------------------------------------------

    private void ajouterContexte(HttpSession session, Model model) {
        ajouterContexte(SessionUtils.getUtilisateur(session), model);
    }

    private void ajouterContexte(Utilisateur utilisateur, Model model) {
        model.addAttribute("utilisateurConnecte", utilisateur);
        if (utilisateur != null) {
            model.addAttribute("espaceUrl", espaceUrl(utilisateur.getRole()));
        }
    }

    private static String espaceUrl(String role) {
        return switch (role) {
            case "MANAGER" -> "/manager/accueil";
            case "RH" -> "/rh/accueil";
            case "FINANCIER" -> "/finance/accueil";
            case "DIRECTEUR" -> "/directeur/accueil";
            case "CANDIDAT" -> "/candidat/espace";
            default -> "/login";
        };
    }
}
