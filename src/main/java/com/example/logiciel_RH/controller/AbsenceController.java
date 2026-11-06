package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Absence;
import com.example.logiciel_RH.repository.AbsenceRepository;
import com.example.logiciel_RH.repository.EmployeRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/absences")
public class AbsenceController {

    private final AbsenceRepository absenceRepository;
    private final EmployeRepository employeRepository;

    public AbsenceController(AbsenceRepository absenceRepository,
                             EmployeRepository employeRepository) {
        this.absenceRepository = absenceRepository;
        this.employeRepository = employeRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("absences", absenceRepository.findAllByOrderByDateDebutDesc());
        model.addAttribute("employes", employeRepository.findAllByOrderByDateCreationDesc());
        model.addAttribute("typesAbsence", Absence.TypeAbsence.values());
        return "rh/absences";
    }

    @PostMapping
    @Transactional
    public String creer(@RequestParam Long employeId,
                        @RequestParam String dateDebut,
                        @RequestParam String dateFin,
                        @RequestParam String typeAbsence,
                        @RequestParam(required = false) String motif,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = LocalDate.parse(dateFin);
            if (fin.isBefore(debut)) throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
            Absence absence = new Absence();
            absence.setEmploye(employeRepository.findById(employeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable.")));
            absence.setDateDebut(debut);
            absence.setDateFin(fin);
            absence.setTypeAbsence(Absence.TypeAbsence.valueOf(typeAbsence));
            absence.setMotif(motif == null || motif.isBlank() ? null : motif.trim());
            absence.setStatut(Absence.Statut.VALIDEE);
            absence.setValidePar(SessionUtils.getUtilisateur(session));
            absence.setDateDemande(LocalDateTime.now());
            absence.setDateValidation(LocalDateTime.now());
            absenceRepository.save(absence);
            redirectAttributes.addFlashAttribute("message", "Absence enregistrée et validée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Absence non enregistrée : " + ex.getMessage());
        }
        return "redirect:/rh/absences";
    }
}
