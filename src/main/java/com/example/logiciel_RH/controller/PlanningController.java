package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.AffectationHoraire;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.ModeleHoraire;
import com.example.logiciel_RH.entity.RegimeTravail;
import com.example.logiciel_RH.repository.AffectationHoraireRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.ModeleHoraireRepository;
import com.example.logiciel_RH.repository.RegimeTravailRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/planning")
public class PlanningController {

    private final AffectationHoraireRepository affectationRepository;
    private final EmployeRepository employeRepository;
    private final RegimeTravailRepository regimeRepository;
    private final ModeleHoraireRepository modeleRepository;

    public PlanningController(AffectationHoraireRepository affectationRepository,
                              EmployeRepository employeRepository,
                              RegimeTravailRepository regimeRepository,
                              ModeleHoraireRepository modeleRepository) {
        this.affectationRepository = affectationRepository;
        this.employeRepository = employeRepository;
        this.regimeRepository = regimeRepository;
        this.modeleRepository = modeleRepository;
    }

    @GetMapping("/affectations")
    @Transactional(readOnly = true)
    public String affectations(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("affectations", affectationRepository.findAllByOrderByDateDebutDesc());
        model.addAttribute("employes", employeRepository.findAllByOrderByDateCreationDesc());
        model.addAttribute("regimes", regimeRepository.findByActifTrueOrderByNomAsc());
        model.addAttribute("modeles", modeleRepository.findByActifTrueOrderByHeureDebutAsc());
        return "rh/planning-affectations";
    }

    @PostMapping("/affectations")
    @Transactional
    public String creerAffectation(@RequestParam Long employeId,
                                   @RequestParam Long regimeId,
                                   @RequestParam Long modeleId,
                                   @RequestParam String dateDebut,
                                   @RequestParam(required = false) String dateFin,
                                   @RequestParam(value = "jours", required = false) List<Integer> jours,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            if (jours == null || jours.isEmpty()) {
                throw new IllegalArgumentException("Sélectionnez au moins un jour travaillé.");
            }

            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = dateFin == null || dateFin.isBlank() ? null : LocalDate.parse(dateFin);
            if (fin != null && fin.isBefore(debut)) {
                throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
            }

            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            RegimeTravail regime = regimeRepository.findById(regimeId)
                    .orElseThrow(() -> new IllegalArgumentException("Régime de travail introuvable."));
            ModeleHoraire modele = modeleRepository.findById(modeleId)
                    .orElseThrow(() -> new IllegalArgumentException("Modèle horaire introuvable."));

            AffectationHoraire affectation = new AffectationHoraire();
            affectation.setEmploye(employe);
            affectation.setRegimeTravail(regime);
            affectation.setModeleHoraire(modele);
            affectation.setDateDebut(debut);
            affectation.setDateFin(fin);
            affectation.setActif(true);
            affectation.setJoursSemaine(new LinkedHashSet<>(jours));
            affectationRepository.save(affectation);

            redirectAttributes.addFlashAttribute("message", "Affectation horaire créée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Affectation non créée : " + ex.getMessage());
        }
        return "redirect:/rh/planning/affectations";
    }
}
