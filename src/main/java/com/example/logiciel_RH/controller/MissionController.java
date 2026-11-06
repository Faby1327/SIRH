package com.example.logiciel_RH.controller;

import java.time.LocalDate;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.DetailJourTravail;
import com.example.logiciel_RH.entity.Mission;
import com.example.logiciel_RH.entity.ParticipationMission;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.DetailJourTravailRepository;
import com.example.logiciel_RH.repository.MissionRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/missions")
public class MissionController {

    private final MissionRepository missionRepository;
    private final ParticipationMissionRepository participationRepository;
    private final EmployeRepository employeRepository;
    private final DetailJourTravailRepository detailRepository;

    public MissionController(MissionRepository missionRepository,
                             ParticipationMissionRepository participationRepository,
                             EmployeRepository employeRepository,
                             DetailJourTravailRepository detailRepository) {
        this.missionRepository = missionRepository;
        this.participationRepository = participationRepository;
        this.employeRepository = employeRepository;
        this.detailRepository = detailRepository;
    }

    @GetMapping
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("missions", missionRepository.findAllByOrderByDateDebutDesc());
        return "rh/missions";
    }

    @GetMapping("/nouveau")
    public String nouveau(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("priorites", Mission.Priorite.values());
        return "rh/mission-form";
    }

    @PostMapping
    @Transactional
    public String creer(@RequestParam String titre,
                        @RequestParam(required = false) String description,
                        @RequestParam String dateDebut,
                        @RequestParam(required = false) String dateFinPrevue,
                        @RequestParam(required = false) String lieu,
                        @RequestParam String priorite,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            if (titre == null || titre.isBlank()) throw new IllegalArgumentException("Le titre est obligatoire.");
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = dateFinPrevue == null || dateFinPrevue.isBlank() ? null : LocalDate.parse(dateFinPrevue);
            if (fin != null && fin.isBefore(debut)) throw new IllegalArgumentException("La fin prévue doit être postérieure au début.");

            Mission mission = new Mission();
            mission.setTitre(titre.trim());
            mission.setDescription(description);
            mission.setDateDebut(debut);
            mission.setDateFinPrevue(fin);
            mission.setLieu(lieu);
            mission.setPriorite(Mission.Priorite.valueOf(priorite));
            mission.setResponsable(SessionUtils.getUtilisateur(session));
            mission.setStatut(Mission.Statut.A_FAIRE);
            missionRepository.save(mission);
            redirectAttributes.addFlashAttribute("message", "Mission créée.");
            return "redirect:/rh/missions/" + mission.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Mission non créée : " + ex.getMessage());
            return "redirect:/rh/missions/nouveau";
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String detail(@PathVariable Long id, HttpSession session, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Mission mission = missionRepository.findById(id).orElse(null);
        if (mission == null) {
            redirectAttributes.addFlashAttribute("error", "Mission introuvable.");
            return "redirect:/rh/missions";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("mission", mission);
        var participations = participationRepository.findByMission_IdOrderByDateAffectationAsc(id);
        boolean toutesAffectationsTerminees = !participations.isEmpty()
                && participations.stream().allMatch(participation ->
                        participation.getStatut() == ParticipationMission.Statut.TERMINEE);
        java.util.Map<Long, String> dureesParticipation = new java.util.LinkedHashMap<>();
        int totalMinutes = 0;
        for (var participation : participations) {
            int minutes = detailRepository.totalMinutesMission(
                    participation.getId(), DetailJourTravail.TypeDetail.MISSION);
            totalMinutes += minutes;
            dureesParticipation.put(participation.getId(), formaterDuree(minutes));
        }
        model.addAttribute("participations", participations);
        model.addAttribute("toutesAffectationsTerminees", toutesAffectationsTerminees);
        model.addAttribute("dureesParticipation", dureesParticipation);
        model.addAttribute("dureeTotaleMission", formaterDuree(totalMinutes));
        model.addAttribute("employes", employeRepository.findAllByOrderByDateCreationDesc());
        model.addAttribute("participationStatuts", ParticipationMission.Statut.values());
        return "rh/mission-detail";
    }

    @PostMapping("/{id}/terminer")
    @Transactional
    public String terminer(@PathVariable Long id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Mission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable."));
            if (mission.getStatut() == Mission.Statut.TERMINEE) {
                throw new IllegalArgumentException("Cette mission est déjà terminée.");
            }
            if (LocalDate.now().isBefore(mission.getDateDebut())) {
                throw new IllegalArgumentException("La mission ne peut pas être terminée avant sa date de début.");
            }
            var participations = participationRepository.findByMission_IdOrderByDateAffectationAsc(id);
            if (participations.isEmpty()) {
                throw new IllegalArgumentException("La mission ne peut pas être terminée : aucun employé n'est affecté.");
            }
            boolean toutesTerminees = participations.stream()
                    .allMatch(participation -> participation.getStatut() == ParticipationMission.Statut.TERMINEE);
            if (!toutesTerminees) {
                throw new IllegalArgumentException("La mission ne peut pas être terminée : toutes les affectations ne sont pas terminées.");
            }
            mission.setStatut(Mission.Statut.TERMINEE);
            mission.setDateFinReelle(LocalDate.now());
            missionRepository.save(mission);
            redirectAttributes.addFlashAttribute("message", "Mission terminée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/missions/" + id;
    }

    @PostMapping("/{id}/participants")
    @Transactional
    public String ajouterParticipant(@PathVariable Long id,
                                     @RequestParam Long employeId,
                                     @RequestParam String roleMission,
                                     @RequestParam String dateAffectation,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Mission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable."));
            if (mission.getStatut() == Mission.Statut.TERMINEE) {
                throw new IllegalArgumentException("Impossible d'affecter un employé : cette mission est terminée.");
            }
            Employe employe = employeRepository.findById(employeId)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            if (roleMission == null || roleMission.isBlank()) throw new IllegalArgumentException("Le rôle est obligatoire.");
            ParticipationMission participation = new ParticipationMission();
            participation.setMission(mission);
            participation.setEmploye(employe);
            participation.setRoleMission(roleMission.trim());
            participation.setDateAffectation(LocalDate.parse(dateAffectation));
            participation.setAvancement(0);
            participation.setStatut(ParticipationMission.Statut.AFFECTEE);
            participationRepository.save(participation);
            redirectAttributes.addFlashAttribute("message", "Employé affecté à la mission.");
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Cet employé participe déjà à cette mission.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Participation non créée : " + ex.getMessage());
        }
        return "redirect:/rh/missions/" + id;
    }

    @PostMapping("/{id}/statut")
    @Transactional
    public String modifierStatut(@PathVariable Long id,
                                 @RequestParam String statut,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Mission mission = missionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Mission introuvable."));
            Mission.Statut nouveau = Mission.Statut.valueOf(statut);
            mission.setStatut(nouveau);
            mission.setDateFinReelle(nouveau == Mission.Statut.TERMINEE ? LocalDate.now() : null);
            missionRepository.save(mission);
            redirectAttributes.addFlashAttribute("message", "Statut de la mission mis à jour.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Statut non modifié : " + ex.getMessage());
        }
        return "redirect:/rh/missions/" + id;
    }

    private String formaterDuree(int minutes) {
        return (minutes / 60) + " h " + String.format(java.util.Locale.ROOT, "%02d", minutes % 60);
    }
}
