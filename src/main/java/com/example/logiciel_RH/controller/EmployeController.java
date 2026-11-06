package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.PosteRepository;
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/employes")
public class EmployeController {
    private final EmployeRepository employeRepository;
    private final ContratRepository contratRepository;
    private final PosteRepository posteRepository;
    private final AuditService auditService;

    public EmployeController(EmployeRepository employeRepository,
                             ContratRepository contratRepository,
                             PosteRepository posteRepository,
                             AuditService auditService) {
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.posteRepository = posteRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employes", employeRepository.findAllByOrderByDateCreationDesc());
        return "rh/employes";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, HttpSession session, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        auditService.enregistrer(SessionUtils.getUtilisateur(session),
                AuditService.CONSULTATION, AuditService.EMPLOYE, id, null, null);
        model.addAttribute("employe", employe);
        model.addAttribute("contrats", contratRepository.findByEmploye_IdOrderByDateDebutDesc(id));
        model.addAttribute("postes", posteRepository.findAll());
        model.addAttribute("typesContrat", Contrat.Type.values());
        return "rh/employe-detail";
    }

    @PostMapping("/{id}/statut")
    @Transactional
    public String modifierStatut(@PathVariable("id") Long id, @RequestParam String statut,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            Employe.Statut nouveauStatut = Employe.Statut.valueOf(statut);
            Employe.Statut ancienStatut = employe.getStatut();
            LocalDate ancienneDateFin = employe.getDateFin();
            employe.setStatut(nouveauStatut);
            employe.setDateFin(nouveauStatut == Employe.Statut.SORTI ? LocalDate.now() : null);
            employeRepository.save(employe);
            auditService.enregistrer(SessionUtils.getUtilisateur(session),
                    AuditService.UPDATE, AuditService.EMPLOYE, id,
                    AuditService.valeurs("statut", ancienStatut, "dateFin", ancienneDateFin),
                    AuditService.valeurs("statut", nouveauStatut, "dateFin", employe.getDateFin()));
            redirectAttributes.addFlashAttribute("message", "Statut de l'employé mis à jour.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id;
    }

    @PostMapping("/{id}/contrats")
    @Transactional
    public String creerContrat(@PathVariable("id") Long id, @RequestParam Long posteId,
                                @RequestParam String typeContrat, @RequestParam String dateDebut,
                                @RequestParam(required = false) String dateFin,
                                @RequestParam BigDecimal salaireMensuel,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            Poste poste = posteRepository.findById(posteId)
                    .orElseThrow(() -> new IllegalArgumentException("Poste introuvable."));
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = dateFin == null || dateFin.isBlank() ? null : LocalDate.parse(dateFin);
            if (salaireMensuel == null || salaireMensuel.signum() < 0)
                throw new IllegalArgumentException("Le salaire doit être positif.");
            if (fin != null && fin.isBefore(debut))
                throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");

            for (Contrat precedent : contratRepository.findByEmploye_IdAndStatut(id, Contrat.Statut.EN_COURS)) {
                precedent.setStatut(Contrat.Statut.TERMINE);
                precedent.setDateFin(debut.minusDays(1));
                contratRepository.save(precedent);
            }
            Contrat contrat = new Contrat();
            contrat.setEmploye(employe);
            contrat.setPoste(poste);
            contrat.setTypeContrat(Contrat.Type.valueOf(typeContrat));
            contrat.setDateDebut(debut);
            contrat.setDateFin(fin);
            contrat.setSalaireMensuel(salaireMensuel);
            contrat.setDevise("Ar");
            contrat.setStatut(Contrat.Statut.EN_COURS);
            contratRepository.save(contrat);
            employe.setStatut(Employe.Statut.ACTIF);
            employe.setDateFin(null);
            employeRepository.save(employe);
            redirectAttributes.addFlashAttribute("message",
                    "Nouveau contrat créé. Le contrat précédent est terminé.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Contrat non créé : " + ex.getMessage());
        }
        return "redirect:/rh/employes/" + id;
    }

    @PostMapping("/{employeId}/contrats/{contratId}/terminer")
    @Transactional
    public String terminerContrat(@PathVariable("employeId") Long employeId, @PathVariable("contratId") Long contratId,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Contrat contrat = contratRepository.findById(contratId)
                    .orElseThrow(() -> new IllegalArgumentException("Contrat introuvable."));
            if (contrat.getEmploye() == null || !employeId.equals(contrat.getEmploye().getId()))
                throw new IllegalArgumentException("Contrat non associé à cet employé.");
            contrat.setStatut(Contrat.Statut.TERMINE);
            contrat.setDateFin(LocalDate.now());
            contratRepository.save(contrat);
            redirectAttributes.addFlashAttribute("message", "Contrat terminé.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + employeId;
    }
}