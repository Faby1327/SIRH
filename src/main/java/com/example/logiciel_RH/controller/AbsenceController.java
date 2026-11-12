package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.Absence;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.repository.AbsenceRepository;
import com.example.logiciel_RH.repository.AffectationHoraireRepository;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;
import com.example.logiciel_RH.service.AuditService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/absences")
public class AbsenceController {
    private final AbsenceRepository absenceRepository;
    private final EmployeRepository employeRepository;
    private final AffectationHoraireRepository affectationRepository;
    private final ContratRepository contratRepository;
    private final AuditService auditService;
    private final ParticipationMissionRepository participationMissionRepository;

    public AbsenceController(AbsenceRepository absenceRepository, EmployeRepository employeRepository,
                             AffectationHoraireRepository affectationRepository, ContratRepository contratRepository,
                             AuditService auditService, ParticipationMissionRepository participationMissionRepository) {
        this.absenceRepository = absenceRepository;
        this.employeRepository = employeRepository;
        this.affectationRepository = affectationRepository;
        this.contratRepository = contratRepository;
        this.auditService = auditService;
        this.participationMissionRepository = participationMissionRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String liste(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        List<Employe> employes = employeRepository.findAllByOrderByDateCreationDesc();
        Map<Long, Integer> soldes = new LinkedHashMap<>();
        employes.forEach(e -> soldes.put(e.getId(), solde(e)));
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("absences", absenceRepository.findAllByOrderByDateDebutDesc());
        model.addAttribute("employes", employes);
        model.addAttribute("soldesConge", soldes);
        model.addAttribute("typesAbsence", java.util.Arrays.stream(Absence.TypeAbsence.values())
                .filter(type -> type != Absence.TypeAbsence.CONGE).toList());
        model.addAttribute("aujourdHui", LocalDate.now());
        return "rh/absences";
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String detail(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Absence a = absenceRepository.findById(id).orElseThrow();
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("absence", a);
        model.addAttribute("solde", solde(a.getEmploye()));
        model.addAttribute("joursDemandes", joursTravailles(a.getEmploye().getId(), a.getDateDebut(), a.getDateFin()).size());
        model.addAttribute("remplacants", candidatsDisponibles(a.getEmploye(), a.getDateDebut(), a.getDateFin(), departementId(a.getEmploye())));
        return "rh/absence-detail";
    }

    @PostMapping("/{id}/valider")
    @Transactional
    public String valider(@org.springframework.web.bind.annotation.PathVariable Long id,
                          @RequestParam(required = false) Long remplacantId,
                          @RequestParam(defaultValue = "false") boolean impactPaie,
                          HttpSession session, RedirectAttributes flash) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Absence a = absenceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Demande introuvable."));
            if (a.getStatut() != Absence.Statut.DEMANDEE) throw new IllegalArgumentException("Cette demande a déjà été traitée.");
            Long eid = a.getEmploye().getId(); LocalDate debut = a.getDateDebut(), fin = a.getDateFin();
            if (fin.isBefore(debut) || debut.isBefore(LocalDate.now())) throw new IllegalArgumentException("Les dates de cette demande ne sont plus valides.");
            if (absenceRepository.estEngageCommeRemplacant(eid, debut, fin)) throw new IllegalArgumentException("Cet employé est déjà engagé comme remplaçant pendant cette période.");
            if (absenceRepository.findAllByOrderByDateDebutDesc().stream().anyMatch(x -> !x.getId().equals(id) && x.getEmploye().getId().equals(eid) && (x.getStatut()==Absence.Statut.DEMANDEE || x.getStatut()==Absence.Statut.VALIDEE) && !x.getDateDebut().isAfter(fin) && !x.getDateFin().isBefore(debut))) throw new IllegalArgumentException("Cette période chevauche déjà une autre absence de l'employé.");
            if (participationMissionRepository.estAffectePendant(eid, debut, fin)) throw new IllegalArgumentException("Un congé ne peut pas chevaucher une mission à laquelle l'employé est affecté.");
            int jours = joursTravailles(eid, debut, fin).size(), soldeAvant = solde(a.getEmploye());
            if (jours > soldeAvant) throw new IllegalArgumentException("Solde insuffisant : " + soldeAvant + " jour(s) disponible(s), " + jours + " demandé(s).");
            List<Employe> candidats = candidatsDisponibles(a.getEmploye(), debut, fin, departementId(a.getEmploye()));
            if (!candidats.isEmpty() && remplacantId == null) throw new IllegalArgumentException("Choisissez un remplaçant disponible pour cette période.");
            Employe remp = null;
            if (remplacantId != null) {
                remp = candidats.stream().filter(e -> e.getId().equals(remplacantId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Le remplaçant choisi n'est pas disponible ou ne respecte pas les règles."));
            }
            a.setRemplacant(remp); a.setImpactPaie(impactPaie); a.setStatut(Absence.Statut.VALIDEE);
            a.setValidePar(SessionUtils.getUtilisateur(session)); a.setDateValidation(LocalDateTime.now()); absenceRepository.save(a);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE, AuditService.ABSENCE, id, null,
                    AuditService.valeurs("statut", "VALIDEE", "remplacantId", remp == null ? null : remp.getId(), "joursDeduits", jours, "soldeAvant", soldeAvant, "soldeApres", solde(a.getEmploye())));
            flash.addFlashAttribute("message", "Solde disponible : " + solde(a.getEmploye()) + " jour(s).");
            return "redirect:/rh/absences/" + id;
        } catch (RuntimeException e) { flash.addFlashAttribute("error", "Validation impossible : " + e.getMessage()); return "redirect:/rh/absences/" + id; }
    }

    @PostMapping("/{id}/refuser")
    @Transactional
    public String refuser(@org.springframework.web.bind.annotation.PathVariable Long id, HttpSession session, RedirectAttributes flash) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Absence a = absenceRepository.findById(id).orElseThrow();
        if (a.getStatut() == Absence.Statut.DEMANDEE) {
            a.setStatut(Absence.Statut.REFUSEE); a.setValidePar(SessionUtils.getUtilisateur(session)); a.setDateValidation(LocalDateTime.now()); absenceRepository.save(a);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.UPDATE, AuditService.ABSENCE,
                    id, AuditService.valeurs("statut", "DEMANDEE"), AuditService.valeurs("statut", "REFUSEE"));
        }
        flash.addFlashAttribute("message", "Demande refusée."); return "redirect:/rh/absences/" + id;
    }

    @GetMapping("/remplacants")
    @ResponseBody
    @Transactional(readOnly = true)
    public List<Map<String, Object>> remplacants(@RequestParam Long employeId, @RequestParam String dateDebut,
                                                  @RequestParam String dateFin, HttpSession session) {
        if (!SessionUtils.hasRole(session, "RH")) throw new IllegalArgumentException("Accès refusé.");
        Employe employe = employeRepository.findById(employeId).orElseThrow();
        LocalDate debut = LocalDate.parse(dateDebut), fin = LocalDate.parse(dateFin);
        if (fin.isBefore(debut) || debut.isBefore(LocalDate.now())) return List.of();
        Long departementId = departementId(employe);
        if (departementId == null) return List.of();
        return candidatsDisponibles(employe, debut, fin, departementId)
                .stream()
                .map(e -> Map.<String, Object>of("id", e.getId(), "nom", e.getPersonne().getNomComplet())).toList();
    }

    @PostMapping
    @Transactional
    public String creerAbsence(@RequestParam Long employeId, @RequestParam String dateDebut,
                               @RequestParam String dateFin, @RequestParam String typeAbsence,
                               @RequestParam(required = false) String motif,
                               @RequestParam(required = false) Long remplacantId,
                               @RequestParam(defaultValue = "false") boolean impactPaie,
                               HttpSession session, RedirectAttributes flash) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            LocalDate debut = LocalDate.parse(dateDebut), fin = LocalDate.parse(dateFin);
            if (fin.isBefore(debut)) throw new IllegalArgumentException("La date de fin doit être postérieure ou égale à la date de début.");
            if (debut.isBefore(LocalDate.now())) throw new IllegalArgumentException("Les dates passées ne peuvent pas être sélectionnées.");
            Absence.TypeAbsence type = Absence.TypeAbsence.valueOf(typeAbsence);
            if (type == Absence.TypeAbsence.CONGE) throw new IllegalArgumentException("Les congés doivent être demandés par l'employé.");
            Employe employe = employeRepository.findById(employeId).orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            if (participationMissionRepository.estAffectePendant(employeId, debut, fin)) throw new IllegalArgumentException("Cet employé est affecté à une mission pendant cette période et ne peut pas s'absenter.");
            if (absenceRepository.estEngageCommeRemplacant(employeId, debut, fin)) throw new IllegalArgumentException("Cet employé est déjà engagé comme remplaçant pendant cette période.");
            if (absenceRepository.aChevauchement(employeId, debut, fin)) throw new IllegalArgumentException("Cette période chevauche déjà une autre absence de l'employé.");
            List<Employe> candidats = candidatsDisponibles(employe, debut, fin, departementId(employe));
            if (!candidats.isEmpty() && remplacantId == null) throw new IllegalArgumentException("Choisissez un remplaçant disponible pour cette période.");
            Employe remp = null;
            if (remplacantId != null) remp = candidats.stream().filter(e -> e.getId().equals(remplacantId)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Le remplaçant choisi n'est pas disponible ou ne respecte pas les règles."));
            Absence a = new Absence(); a.setEmploye(employe); a.setDateDebut(debut); a.setDateFin(fin);
            a.setTypeAbsence(type); a.setRemplacant(remp); a.setMotif(motif == null || motif.isBlank() ? null : motif.trim());
            a.setImpactPaie(impactPaie); a.setStatut(Absence.Statut.VALIDEE); a.setValidePar(SessionUtils.getUtilisateur(session));
            a.setDateDemande(LocalDateTime.now()); a.setDateValidation(LocalDateTime.now()); a = absenceRepository.save(a);
            auditService.enregistrer(SessionUtils.getUtilisateur(session), AuditService.INSERT, AuditService.ABSENCE,
                    a.getId(), null, AuditService.valeurs("employeId", employeId, "type", type,
                            "dateDebut", debut, "dateFin", fin, "remplacantId", remp == null ? null : remp.getId(),
                            "statut", a.getStatut(), "impactPaie", impactPaie));
            flash.addFlashAttribute("message", "Absence enregistrée.");
        } catch (RuntimeException e) { flash.addFlashAttribute("error", "Absence non enregistrée : " + e.getMessage()); }
        return "redirect:/rh/absences";
    }

    private int solde(Employe employe) {
        if (employe.getSoldeConge() == null) employe.setSoldeConge(30);
        int pris = absenceRepository.findByEmploye_IdAndTypeAbsenceAndStatut(
                employe.getId(), Absence.TypeAbsence.CONGE, Absence.Statut.VALIDEE)
                .stream().mapToInt(a -> joursTravailles(employe.getId(), a.getDateDebut(), a.getDateFin()).size()).sum();
        return Math.max(0, employe.getSoldeConge() - pris);
    }

    private Long departementId(Employe employe) {
        if (employe.getDepartement() != null) return employe.getDepartement().getIdDepartement();
        if (employe.getPoste() != null && employe.getPoste().getDepartement() != null)
            return employe.getPoste().getDepartement().getIdDepartement();
        return contratRepository.findByEmploye_IdAndStatut(employe.getId(), com.example.logiciel_RH.entity.Contrat.Statut.EN_COURS)
                .stream().map(c -> c.getPoste().getDepartement().getIdDepartement()).findFirst().orElse(null);
    }

    private List<LocalDate> joursTravailles(Long employeId, LocalDate debut, LocalDate fin) {
        List<LocalDate> jours = new ArrayList<>();
        var affectations = affectationRepository.findByEmploye_IdOrderByDateDebutDesc(employeId);
        for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {
            LocalDate jour = date;
            int jourSemaine = jour.getDayOfWeek().getValue();
            if (affectations.stream().anyMatch(a -> a.isActif() && !a.getDateDebut().isAfter(jour)
                    && (a.getDateFin() == null || !a.getDateFin().isBefore(jour))
                    && a.getJoursSemaine().contains(jourSemaine))) jours.add(jour);
        }
        return jours;
    }

    private boolean estPlanifieSurTousLesJours(Long employeId, List<LocalDate> jours) {
        var affectations = affectationRepository.findByEmploye_IdOrderByDateDebutDesc(employeId);
        return jours.stream().allMatch(date -> affectations.stream().anyMatch(a -> a.isActif()
                && !a.getDateDebut().isAfter(date) && (a.getDateFin() == null || !a.getDateFin().isBefore(date))
                && a.getJoursSemaine().contains(date.getDayOfWeek().getValue())));
    }

    private List<Employe> candidatsDisponibles(Employe employe, LocalDate debut, LocalDate fin, Long departementId) {
        if (departementId == null || fin.isBefore(debut)) return List.of();
        List<LocalDate> jours = joursTravailles(employe.getId(), debut, fin);
        if (jours.isEmpty()) return List.of();
        return employeRepository.remplaçantsDuDépartement(departementId, employe.getId()).stream()
                .filter(e -> e.getStatut() == Employe.Statut.ACTIF)
                .filter(e -> !absenceRepository.aChevauchement(e.getId(), debut, fin))
                .filter(e -> !absenceRepository.estEngageCommeRemplacant(e.getId(), debut, fin))
                .filter(e -> !participationMissionRepository.estAffectePendant(e.getId(), debut, fin))
                .filter(e -> estPlanifieSurTousLesJours(e.getId(), jours))
                .toList();
    }
}
