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

/**
 * Écrit les lignes du journal d'audit (bloc 5).
 *
 * L'appel se fait dans la même transaction que l'action tracée : si l'action
 * est annulée, la ligne d'audit l'est aussi.
 */
@Service
public class AuditService {

    // Codes de type_action
    public static final String INSERT = "INSERT";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    public static final String CONSULTATION = "CONSULTATION";

    // Codes de type_entite
    public static final String EMPLOYE = "EMPLOYE";
    public static final String CONTRAT = "CONTRAT";
    public static final String FICHE_PAIE = "FICHE_PAIE";

    private final JournalAuditRepository journalAuditRepository;
    private final TypeActionRepository typeActionRepository;
    private final TypeEntiteRepository typeEntiteRepository;
    private final EmployeRepository employeRepository;
    private final ContratRepository contratRepository;
    private final FichePaieRepository fichePaieRepository;
    private final ObjectMapper objectMapper;

    public AuditService(JournalAuditRepository journalAuditRepository,
                        TypeActionRepository typeActionRepository,
                        TypeEntiteRepository typeEntiteRepository,
                        EmployeRepository employeRepository,
                        ContratRepository contratRepository,
                        FichePaieRepository fichePaieRepository,
                        ObjectMapper objectMapper) {
        this.journalAuditRepository = journalAuditRepository;
        this.typeActionRepository = typeActionRepository;
        this.typeEntiteRepository = typeEntiteRepository;
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.fichePaieRepository = fichePaieRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Enregistre une action dans le journal.
     *
     * @param anciennes valeurs avant l'action (null pour INSERT et CONSULTATION)
     * @param nouvelles valeurs après l'action (null pour DELETE et CONSULTATION)
     */
    @Transactional
    public JournalAudit enregistrer(Utilisateur utilisateur, String codeAction, String codeEntite,
                                    Long entiteId, Map<String, Object> anciennes,
                                    Map<String, Object> nouvelles) {
        if (utilisateur == null) {
            throw new IllegalArgumentException("Utilisateur obligatoire pour l'audit.");
        }
        if (entiteId == null) {
            throw new IllegalArgumentException("Identifiant d'entité obligatoire pour l'audit.");
        }

        TypeAction action = typeActionRepository.findByCode(codeAction)
                .orElseThrow(() -> new IllegalArgumentException("Type d'action inconnu : " + codeAction));
        TypeEntite typeEntite = typeEntiteRepository.findByCode(codeEntite)
                .orElseThrow(() -> new IllegalArgumentException("Type d'entité inconnu : " + codeEntite));

        // Cohérence du lien polymorphe. Après une suppression, l'entité n'existe plus.
        if (!DELETE.equals(codeAction) && !entiteExiste(codeEntite, entiteId)) {
            throw new IllegalArgumentException(
                    "Aucune entité " + codeEntite + " avec l'identifiant " + entiteId + ".");
        }

        JournalAudit ligne = new JournalAudit();
        ligne.setUtilisateur(utilisateur);
        ligne.setAction(action);
        ligne.setTypeEntite(typeEntite);
        ligne.setEntiteId(entiteId);
        ligne.setAnciennesValeurs(enJson(anciennes));
        ligne.setNouvellesValeurs(enJson(nouvelles));
        return journalAuditRepository.save(ligne);
    }

    /** Construit une map ordonnée : valeurs("statut", "ACTIF", "salaire", 300000). */
    public static Map<String, Object> valeurs(Object... clesValeurs) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < clesValeurs.length; i += 2) {
            map.put(String.valueOf(clesValeurs[i]), clesValeurs[i + 1]);
        }
        return map;
    }

    /**
     * Un cas par table connue. Les types dont la table n'existe pas encore dans
     * ce projet (DOCUMENT, CONGE, MISSION) ne sont pas contrôlés : celui qui
     * ajoute la table ajoute aussi son cas ici.
     */
    private boolean entiteExiste(String codeEntite, Long id) {
        return switch (codeEntite) {
            case EMPLOYE -> employeRepository.existsById(id);
            case CONTRAT -> contratRepository.existsById(id);
            case FICHE_PAIE -> fichePaieRepository.existsById(id);
            default -> true;
        };
    }

    private String enJson(Map<String, Object> valeurs) {
        if (valeurs == null || valeurs.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(valeurs);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Valeurs d'audit non sérialisables.", e);
        }
    }
}