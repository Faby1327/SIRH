package com.example.logiciel_RH.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.domain.Sort;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/audit")
public class AuditController {
    private final AuditConsultationService consultationService;
    private final JournalAuditRepository repository;
    private final TypeActionRepository actionRepository;
    private final TypeEntiteRepository entiteRepository;
    private final ObjectMapper objectMapper;
    public AuditController(AuditConsultationService consultationService, JournalAuditRepository repository,
                            TypeActionRepository actionRepository, TypeEntiteRepository entiteRepository,
                            ObjectMapper objectMapper) {
        this.consultationService = consultationService; this.repository = repository;
        this.actionRepository = actionRepository; this.entiteRepository = entiteRepository; this.objectMapper = objectMapper;
    }
    @GetMapping
    public String liste(@RequestParam(required = false) String action, @RequestParam(required = false) String entite,
                        @RequestParam(required = false) Long entiteId, @RequestParam(required = false) String utilisateur,
                        @RequestParam(required = false) String texte, HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("evenements", consultationService.rechercher(action, entite, entiteId, utilisateur, null, null, texte));
        model.addAttribute("typesAction", actionRepository.findAll(Sort.by("id")));
        model.addAttribute("typesEntite", entiteRepository.findAll(Sort.by("id")));
        model.addAttribute("fAction", action); model.addAttribute("fEntite", entite); model.addAttribute("fEntiteId", entiteId);
        model.addAttribute("fUtilisateur", utilisateur); model.addAttribute("fTexte", texte);
        return "rh/audit";
    }
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        JournalAudit evenement = repository.findById(id).orElse(null);
        if (evenement == null) { redirectAttributes.addFlashAttribute("error", "Événement introuvable."); return "redirect:/rh/audit"; }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session)); model.addAttribute("evenement", evenement);
        model.addAttribute("anciennes", lire(evenement.getAnciennesValeurs())); model.addAttribute("nouvelles", lire(evenement.getNouvellesValeurs()));
        return "rh/audit-detail";
    }
    private Map<String, Object> lire(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try { return objectMapper.readValue(json, new TypeReference<LinkedHashMap<String, Object>>() {}); }
        catch (Exception ex) { return Map.of("valeurs", json); }
    }
}
