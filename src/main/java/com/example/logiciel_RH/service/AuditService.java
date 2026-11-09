package com.example.logiciel_RH.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.JournalAudit;
import com.example.logiciel_RH.entity.TypeAction;
import com.example.logiciel_RH.entity.TypeEntite;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.FichePaieRepository;
import com.example.logiciel_RH.repository.JournalAuditRepository;
import com.example.logiciel_RH.repository.TypeActionRepository;
import com.example.logiciel_RH.repository.TypeEntiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuditService {
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String CONSULTATION = "CONSULTATION";
    public static final String EMPLOYE = "EMPLOYE";
    public static final String CONTRAT = "CONTRAT";
    public static final String FICHE_PAIE = "FICHE_PAIE";

    private final JournalAuditRepository journalRepository;
    private final TypeActionRepository actionRepository;
    private final TypeEntiteRepository entiteRepository;
    private final EmployeRepository employeRepository;
    private final ContratRepository contratRepository;
    private final FichePaieRepository fichePaieRepository;
    private final ObjectMapper objectMapper;

    public AuditService(JournalAuditRepository journalRepository, TypeActionRepository actionRepository,
                        TypeEntiteRepository entiteRepository, EmployeRepository employeRepository,
                        ContratRepository contratRepository, FichePaieRepository fichePaieRepository,
                        ObjectMapper objectMapper) {
        this.journalRepository = journalRepository;
        this.actionRepository = actionRepository;
        this.entiteRepository = entiteRepository;
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.fichePaieRepository = fichePaieRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public JournalAudit enregistrer(Utilisateur utilisateur, String codeAction, String codeEntite,
                                   Long entiteId, Map<String, Object> anciennes,
                                   Map<String, Object> nouvelles) {
        if (utilisateur == null || entiteId == null) {
            throw new IllegalArgumentException("Utilisateur et entité obligatoires pour l'audit.");
        }
        TypeAction action = actionRepository.findByCode(codeAction)
                .orElseThrow(() -> new IllegalArgumentException("Type d'action inconnu : " + codeAction));
        TypeEntite entite = entiteRepository.findByCode(codeEntite)
                .orElseThrow(() -> new IllegalArgumentException("Type d'entité inconnu : " + codeEntite));
        if (!DELETE.equals(codeAction) && !entiteExiste(codeEntite, entiteId)) {
            throw new IllegalArgumentException("Entité introuvable pour l'audit : " + codeEntite + " #" + entiteId);
        }
        JournalAudit ligne = new JournalAudit();
        ligne.setUtilisateur(utilisateur);
        ligne.setAction(action);
        ligne.setTypeEntite(entite);
        ligne.setEntiteId(entiteId);
        ligne.setAnciennesValeurs(enJson(anciennes));
        ligne.setNouvellesValeurs(enJson(nouvelles));
        return journalRepository.save(ligne);
    }

    public static Map<String, Object> valeurs(Object... elements) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        for (int i = 0; i + 1 < elements.length; i += 2) {
            resultat.put(String.valueOf(elements[i]), elements[i + 1]);
        }
        return resultat;
    }

    private boolean entiteExiste(String code, Long id) {
        return switch (code) {
            case EMPLOYE -> employeRepository.existsById(id);
            case CONTRAT -> contratRepository.existsById(id);
            case FICHE_PAIE -> fichePaieRepository.existsById(id);
            default -> true;
        };
    }

    private String enJson(Map<String, Object> valeurs) {
        if (valeurs == null || valeurs.isEmpty()) return null;
        try { return objectMapper.writeValueAsString(valeurs); }
        catch (JsonProcessingException e) { throw new IllegalStateException("Valeurs d'audit non sérialisables.", e); }
    }
}
