package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.FichePaie;
import com.example.logiciel_RH.repository.FichePaieRepository;
import com.example.logiciel_RH.repository.PaiementSalaireRepository;
import com.example.logiciel_RH.service.PaieService;
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/paie")
public class PaieController {

    private final PaieService paieService;
    private final FichePaieRepository ficheRepository;
    private final PaiementSalaireRepository paiementRepository;
    private final AuditService auditService;

    public PaieController(PaieService paieService,
                          FichePaieRepository ficheRepository,
                          PaiementSalaireRepository paiementRepository,
                          AuditService auditService) {
        this.paieService = paieService;
        this.ficheRepository = ficheRepository;
        this.paiementRepository = paiementRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        LocalDate date = LocalDate.now();
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("fiches", paieService.genererFichesDuMois());
        model.addAttribute("moisCourant", date.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH)
                + " " + date.getYear());
        return "rh/paie";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        FichePaie fiche = ficheRepository.findById(id).orElse(null);
        if (fiche == null) {
            redirectAttributes.addFlashAttribute("error", "Fiche de paie introuvable.");
            return "redirect:/rh/paie";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("fiche", fiche);
        model.addAttribute("lignes", paieService.lignes(id));
        model.addAttribute("paiement", paiementRepository.findByFichePaie_Id(id).orElse(null));
        auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.CONSULTATION,
                AuditService.FICHE_PAIE, id, null, null);
        return "rh/paie-detail";
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            FichePaie fiche = paieService.valider(id);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE,
                    AuditService.FICHE_PAIE, id, AuditService.valeurs("statut", FichePaie.Statut.BROUILLON),
                    AuditService.valeurs("statut", fiche.getStatut(), "dateValidation", fiche.getDateValidation()));
            redirectAttributes.addFlashAttribute("message", "Fiche de paie validée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/paie/" + id;
    }

    @PostMapping("/{id}/payer")
    public String payer(@PathVariable Long id, HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            var paiement = paieService.payer(id);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE,
                    AuditService.FICHE_PAIE, id, AuditService.valeurs("statut", FichePaie.Statut.VALIDEE),
                    AuditService.valeurs("statut", FichePaie.Statut.PAYEE, "referencePaiement", paiement.getReference()));
            redirectAttributes.addFlashAttribute("message",
                    "Paiement enregistré et fiche marquée comme payée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/paie/" + id;
    }
}
