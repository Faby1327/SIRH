package com.example.logiciel_RH.controller;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

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
import com.example.logiciel_RH.entity.Absence;
import com.example.logiciel_RH.repository.AbsenceRepository;
import com.example.logiciel_RH.entity.DetailJourTravail;
import com.example.logiciel_RH.repository.DetailJourTravailRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.AffectationHoraireRepository;
import com.example.logiciel_RH.repository.JourTravailRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;
import com.example.logiciel_RH.service.PlanningJourService;
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employe")
public class EspaceEmployeController {

    private final EmployeRepository employeRepository;
    private final JourTravailRepository jourRepository;
    private final PlanningJourService planningService;
    private final ParticipationMissionRepository participationRepository;
    private final DetailJourTravailRepository detailRepository;
    private final AbsenceRepository absenceRepository;
    private final AffectationHoraireRepository affectationRepository;
    private final AuditService auditService;

    public EspaceEmployeController(EmployeRepository employeRepository,
                                   JourTravailRepository jourRepository,
                                   PlanningJourService planningService,
                                   ParticipationMissionRepository participationRepository,
                                   DetailJourTravailRepository detailRepository,
                                   AbsenceRepository absenceRepository,
                                   AffectationHoraireRepository affectationRepository,
                                   AuditService auditService) {
        this.employeRepository = employeRepository;
        this.jourRepository = jourRepository;
        this.planningService = planningService;
        this.participationRepository = participationRepository;
        this.detailRepository = detailRepository;
        this.absenceRepository = absenceRepository;
        this.affectationRepository = affectationRepository;
        this.auditService = auditService;
    }

    @GetMapping("/conges")
    @Transactional(readOnly = true)
    public String conges(HttpSession session, Model model, RedirectAttributes flash) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        if (employe == null) return "redirect:/login";
        model.addAttribute("employe", employe);
        var absences = absenceRepository.findByEmploye_IdOrderByDateDebutDesc(employe.getId());
        model.addAttribute("absences", absences);
        Map<Long, Integer> joursDemandes = new LinkedHashMap<>();
        absences.forEach(a -> joursDemandes.put(a.getId(), joursPlanifies(employe, a.getDateDebut(), a.getDateFin())));
        model.addAttribute("joursDemandes", joursDemandes);
        model.addAttribute("soldeConge", calculerSoldeConge(employe));
        int joursEnAttente = joursCongesEnAttente(employe);
        if (joursEnAttente > calculerSoldeConge(employe)) {
            model.addAttribute("alerteSolde", "Solde insuffisant : vos demandes en attente représentent "
                    + joursEnAttente + " jour(s), alors que votre solde disponible est de "
                    + calculerSoldeConge(employe) + " jour(s).");
        }
        model.addAttribute("aujourdHui", java.time.LocalDate.now());
        return "employe-conges";
    }

    private int calculerSoldeConge(Employe employe) {
        int quota = employe.getSoldeConge() == null ? 30 : employe.getSoldeConge();
        int joursPris = absenceRepository.findByEmploye_IdAndTypeAbsenceAndStatut(employe.getId(),
                Absence.TypeAbsence.CONGE, Absence.Statut.VALIDEE).stream()
                .mapToInt(a -> joursPlanifies(employe, a.getDateDebut(), a.getDateFin())).sum();
        return Math.max(0, quota - joursPris);
    }

    private int joursPlanifies(Employe employe, LocalDate debut, LocalDate fin) {
        var affectations = affectationRepository.findByEmploye_IdOrderByDateDebutDesc(employe.getId());
        int jours = 0;
        for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {
            LocalDate jour = date;
            int jourSemaine = jour.getDayOfWeek().getValue();
            if (affectations.stream().anyMatch(a -> a.isActif() && !a.getDateDebut().isAfter(jour)
                    && (a.getDateFin() == null || !a.getDateFin().isBefore(jour))
                    && a.getJoursSemaine().contains(jourSemaine))) jours++;
        }
        return jours;
    }

    private int joursCongesEnAttente(Employe employe) {
        return absenceRepository.findByEmploye_IdOrderByDateDebutDesc(employe.getId()).stream()
                .filter(a -> a.getTypeAbsence() == Absence.TypeAbsence.CONGE
                        && a.getStatut() == Absence.Statut.DEMANDEE)
                .mapToInt(a -> joursPlanifies(employe, a.getDateDebut(), a.getDateFin())).sum();
    }

    @PostMapping("/conges")
    @Transactional
    public String demanderConge(@RequestParam String dateDebut, @RequestParam String dateFin,
                                @RequestParam(required = false) String motif, HttpSession session,
                                RedirectAttributes flash) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        try {
            Employe employe = employeConnecte(session);
            if (employe == null) throw new IllegalArgumentException("Dossier employé introuvable.");
            var debut = java.time.LocalDate.parse(dateDebut); var fin = java.time.LocalDate.parse(dateFin);
            if (fin.isBefore(debut)) throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
            if (debut.isBefore(java.time.LocalDate.now())) throw new IllegalArgumentException("Les dates passées ne peuvent pas être sélectionnées.");
            if (participationRepository.estAffectePendant(employe.getId(), debut, fin)) throw new IllegalArgumentException("Vous êtes affecté à une mission pendant cette période et ne pouvez pas vous absenter.");
            if (absenceRepository.aChevauchement(employe.getId(), debut, fin)) throw new IllegalArgumentException("Cette période chevauche déjà une autre absence ou demande.");
            int joursDemandes = joursPlanifies(employe, debut, fin);
            int soldeDisponible = Math.max(0, calculerSoldeConge(employe) - joursCongesEnAttente(employe));
            if (joursDemandes > soldeDisponible) throw new IllegalArgumentException("Solde insuffisant : " + soldeDisponible
                    + " jour(s) disponible(s), " + joursDemandes + " demandé(s).");
            Absence a = new Absence(); a.setEmploye(employe); a.setDateDebut(debut); a.setDateFin(fin);
            a.setTypeAbsence(Absence.TypeAbsence.CONGE); a.setMotif(motif == null || motif.isBlank() ? null : motif.trim());
            a.setStatut(Absence.Statut.DEMANDEE); a.setDateDemande(java.time.LocalDateTime.now());
            a = absenceRepository.save(a); auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.INSERT, AuditService.ABSENCE, a.getId(), null, AuditService.valeurs("type", "CONGE", "dateDebut", debut, "dateFin", fin, "joursDemandes", joursDemandes, "statut", "DEMANDEE")); flash.addFlashAttribute("message", "Votre demande de congé a été envoyée aux RH.");
        } catch (RuntimeException e) { flash.addFlashAttribute("error", "Demande non envoyée : " + e.getMessage()); }
        return "redirect:/employe/conges";
    }

    @GetMapping
    public String accueil() {
        return "redirect:/employe/jours";
    }

    @GetMapping("/missions")
    @Transactional(readOnly = true)
    public String missions(HttpSession session, Model model,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Votre dossier employé est introuvable.");
            return "redirect:/login";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        var participations = participationRepository
                .findByEmploye_IdOrderByDateAffectationDesc(employe.getId());
        Map<Long, String> dureesMission = new LinkedHashMap<>();
        Map<Long, Integer> minutesMission = new LinkedHashMap<>();
        for (var participation : participations) {
            int minutes = detailRepository.totalMinutesMission(
                    participation.getId(), DetailJourTravail.TypeDetail.MISSION);
            minutesMission.put(participation.getId(), minutes);
            dureesMission.put(participation.getId(), minutes <= 0 ? "Aucune heure saisie" : formaterDuree(minutes));
        }
        model.addAttribute("participations", participations);
        model.addAttribute("dureesMission", dureesMission);
        model.addAttribute("minutesMission", minutesMission);
        return "employe-missions";
    }

    @PostMapping("/missions/{id}/terminer")
    @Transactional
    public String terminerMission(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        try {
            Employe employe = employeConnecte(session);
            var participation = participationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Affectation introuvable."));
            if (employe == null || !employe.getId().equals(participation.getEmploye().getId())) {
                throw new IllegalArgumentException("Cette affectation ne vous appartient pas.");
            }
            if (participation.getStatut() == com.example.logiciel_RH.entity.ParticipationMission.Statut.TERMINEE) {
                throw new IllegalArgumentException("Cette affectation est déjà terminée.");
            }
            if (participation.getStatut() == com.example.logiciel_RH.entity.ParticipationMission.Statut.ANNULEE) {
                throw new IllegalArgumentException("Cette affectation est annulée.");
            }
            int minutes = detailRepository.totalMinutesMission(
                    participation.getId(), DetailJourTravail.TypeDetail.MISSION);
            if (minutes <= 0) {
                throw new IllegalArgumentException("Impossible de terminer cette affectation sans temps consacré.");
            }
            participation.setStatut(com.example.logiciel_RH.entity.ParticipationMission.Statut.TERMINEE);
            participation.setDateFinReelle(java.time.LocalDate.now());
            participationRepository.save(participation);
            redirectAttributes.addFlashAttribute("message", "Votre affectation a été terminée.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Affectation non terminée : " + ex.getMessage());
        }
        return "redirect:/employe/missions";
    }

    @GetMapping("/jours")
    @Transactional
    public String jours(@RequestParam(required = false) String mois,
                        HttpSession session, Model model,
                        RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        Employe employe = employeConnecte(session);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Votre dossier employé est introuvable.");
            return "redirect:/login";
        }

        YearMonth periode = mois == null || mois.isBlank() ? YearMonth.now() : YearMonth.parse(mois);
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        model.addAttribute("jours", planningService.genererEtLister(periode).stream()
                .filter(jour -> employe.getId().equals(jour.getEmploye().getId()))
                .toList());
        model.addAttribute("periode", periode);
        model.addAttribute("periodeLibelle", periode.getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + periode.getYear());
        model.addAttribute("periodePrecedente", periode.minusMonths(1));
        model.addAttribute("periodeSuivante", periode.plusMonths(1));
        return "employe";
    }

    @PostMapping("/jours/{id}/heures")
    public String enregistrerHeures(@PathVariable Long id,
                                    @RequestParam String entree,
                                    @RequestParam String sortie,
                                    @RequestParam String mois,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "EMPLOYE")) return "redirect:/login";
        try {
            Employe employe = employeConnecte(session);
            var jour = jourRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Journée introuvable."));
            if (employe == null || !employe.getId().equals(jour.getEmploye().getId())) {
                throw new IllegalArgumentException("Cette journée ne vous appartient pas.");
            }
            if ("POINTAGE_VALIDE".equals(jour.getStatut())
                    || "DETAIL_A_VERIFIER".equals(jour.getStatut())
                    || "VALIDEE".equals(jour.getStatut())) {
                throw new IllegalArgumentException("Le pointage de cette journée a déjà été validé par les RH.");
            }
            planningService.enregistrerHeures(id,
                    LocalTime.parse(entree, DateTimeFormatter.ISO_LOCAL_TIME),
                    LocalTime.parse(sortie, DateTimeFormatter.ISO_LOCAL_TIME));
            redirectAttributes.addFlashAttribute("message", "Vos heures ont été enregistrées.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Heures non enregistrées : " + ex.getMessage());
        }
        return "redirect:/employe/jours?mois=" + mois;
    }

    private Employe employeConnecte(HttpSession session) {
        var utilisateur = SessionUtils.getUtilisateur(session);
        return employeRepository.findByPersonne_IdPersonne(utilisateur.getPersonne().getIdPersonne()).orElse(null);
    }

    private String formaterDuree(int minutes) {
        int heures = minutes / 60;
        int minutesRestantes = minutes % 60;
        return heures + " h " + String.format(Locale.ROOT, "%02d", minutesRestantes);
    }
}
