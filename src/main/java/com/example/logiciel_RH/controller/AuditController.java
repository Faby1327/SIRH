package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.JournalAudit;
import com.example.logiciel_RH.repository.JournalAuditRepository;
import com.example.logiciel_RH.repository.TypeActionRepository;
import com.example.logiciel_RH.repository.TypeEntiteRepository;
import com.example.logiciel_RH.service.AuditConsultationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

/** Pages du journal d'audit (bloc 5) : liste avec filtres et détail d'une action. */
@Controller
@RequestMapping("/rh/audit")
public class AuditController {

    private final AuditConsultationService consultationService;
    private final JournalAuditRepository journalAuditRepository;
    private final TypeActionRepository typeActionRepository;
    private final TypeEntiteRepository typeEntiteRepository;
    private final ObjectMapper objectMapper;

    public AuditController(AuditConsultationService consultationService,
                           JournalAuditRepository journalAuditRepository,
                           TypeActionRepository typeActionRepository,
                           TypeEntiteRepository typeEntiteRepository,
                           ObjectMapper objectMapper) {
        this.consultationService = consultationService;
        this.journalAuditRepository = journalAuditRepository;
        this.typeActionRepository = typeActionRepository;
        this.typeEntiteRepository = typeEntiteRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String journal(
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "entite", required = false) String entite,
            @RequestParam(name = "entiteId", required = false) Long entiteId,
            @RequestParam(name = "utilisateur", required = false) String utilisateur,
            @RequestParam(name = "du", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate du,
            @RequestParam(name = "au", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate au,
            @RequestParam(name = "texte", required = false) String texte,
            HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("evenements",
                consultationService.rechercher(action, entite, entiteId, utilisateur, du, au, texte));
        model.addAttribute("limite", AuditConsultationService.LIMITE);
        model.addAttribute("typesAction", typeActionRepository.findAll(Sort.by("id")));
        model.addAttribute("typesEntite", typeEntiteRepository.findAll(Sort.by("id")));

        // Valeurs saisies, pour réafficher le formulaire de recherche.
        model.addAttribute("fAction", action);
        model.addAttribute("fEntite", entite);
        model.addAttribute("fEntiteId", entiteId);
        model.addAttribute("fUtilisateur", utilisateur);
        model.addAttribute("fDu", du);
        model.addAttribute("fAu", au);
        model.addAttribute("fTexte", texte);
        return "audit/journalAudit";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, HttpSession session, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";

        JournalAudit evenement = journalAuditRepository.findById(id).orElse(null);
        if (evenement == null) {
            redirectAttributes.addFlashAttribute("error", "Événement introuvable.");
            return "redirect:/rh/audit";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("evenement", evenement);
        model.addAttribute("lignes",
                comparer(evenement.getAnciennesValeurs(), evenement.getNouvellesValeurs()));
        return "audit/detailAudit";
    }

    /** Une ligne par champ : nom, ancienne valeur, nouvelle valeur, et s'il a changé. */
    private List<Map<String, Object>> comparer(String anciennesJson, String nouvellesJson) {
        Map<String, Object> anciennes = lire(anciennesJson);
        Map<String, Object> nouvelles = lire(nouvellesJson);

        Set<String> champs = new LinkedHashSet<>(anciennes.keySet());
        champs.addAll(nouvelles.keySet());

        List<Map<String, Object>> lignes = new ArrayList<>();
        for (String champ : champs) {
            Object ancienne = anciennes.get(champ);
            Object nouvelle = nouvelles.get(champ);
            Map<String, Object> ligne = new LinkedHashMap<>();
            ligne.put("champ", champ);
            ligne.put("ancienne", ancienne == null ? null : String.valueOf(ancienne));
            ligne.put("nouvelle", nouvelle == null ? null : String.valueOf(nouvelle));
            ligne.put("modifie", !Objects.equals(ancienne, nouvelle));
            lignes.add(ligne);
        }
        return lignes;
    }

    private Map<String, Object> lire(String json) {
        Map<String, Object> valeurs = new LinkedHashMap<>();
        if (json == null || json.isBlank()) {
            return valeurs;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() { });
        } catch (JsonProcessingException e) {
            valeurs.put("valeurs", json); // texte qui n'est pas du JSON : affiché tel quel
            return valeurs;
        }
    }
}