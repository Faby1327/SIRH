package com.example.logiciel_RH.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.HistoriqueSituationPersonnelle;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.HistoriqueSituationPersonnelleRepository;

/**
 * Historique des changements personnels d'un employé (bloc 5) : téléphone,
 * email personnel, adresse, situation matrimoniale et contact d'urgence.
 * Chaque ligne contient la situation complète à une date d'effet donnée.
 */
@Service
public class SituationPersonnelleService {

    /** Valeurs acceptées pour la situation matrimoniale. */
    public static final List<String> SITUATIONS =
            List.of("CELIBATAIRE", "MARIE", "UNION_LIBRE", "DIVORCE", "VEUF");

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final HistoriqueSituationPersonnelleRepository repository;
    private final AuditService auditService;

    public SituationPersonnelleService(HistoriqueSituationPersonnelleRepository repository,
                                       AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    /** Lignes d'un employé, de la plus récente à la plus ancienne. */
    @Transactional(readOnly = true)
    public List<HistoriqueSituationPersonnelle> historique(Long employeId) {
        return repository.findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(employeId);
    }

    /** Ajoute une ligne d'historique et trace l'action dans le journal d'audit. */
    @Transactional
    public HistoriqueSituationPersonnelle enregistrer(Utilisateur acteur, Employe employe,
                                                      String telephone, String emailPersonnel,
                                                      String adresse, String commune, String region,
                                                      String situationMatrimoniale, Integer nombreEnfants,
                                                      String contactUrgenceNom,
                                                      String contactUrgenceRelation,
                                                      String contactUrgenceTelephone,
                                                      LocalDate dateEffet) {
        if (employe == null || employe.getId() == null) {
            throw new IllegalArgumentException("Employé obligatoire pour l'historique.");
        }
        if (dateEffet == null) {
            throw new IllegalArgumentException("Date d'effet obligatoire pour l'historique.");
        }

        String situation = vide(situationMatrimoniale);
        if (situation != null && !SITUATIONS.contains(situation)) {
            throw new IllegalArgumentException("Situation matrimoniale invalide.");
        }
        if (nombreEnfants != null && (nombreEnfants < 0 || nombreEnfants > 50)) {
            throw new IllegalArgumentException("Le nombre d'enfants doit être compris entre 0 et 50.");
        }
        String email = texte(emailPersonnel, 150, "L'email personnel");
        if (email != null && !EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("L'email personnel n'est pas valide.");
        }

        HistoriqueSituationPersonnelle ligne = new HistoriqueSituationPersonnelle();
        ligne.setEmploye(employe);
        ligne.setTelephone(texte(telephone, 30, "Le téléphone"));
        ligne.setEmailPersonnel(email);
        ligne.setAdresse(texte(adresse, 255, "L'adresse"));
        ligne.setCommune(texte(commune, 100, "La commune"));
        ligne.setRegion(texte(region, 100, "La région"));
        ligne.setSituationMatrimoniale(situation);
        ligne.setNombreEnfants(nombreEnfants);
        ligne.setContactUrgenceNom(texte(contactUrgenceNom, 150, "Le nom du contact d'urgence"));
        ligne.setContactUrgenceRelation(texte(contactUrgenceRelation, 50, "La relation du contact d'urgence"));
        ligne.setContactUrgenceTelephone(texte(contactUrgenceTelephone, 30, "Le téléphone du contact d'urgence"));
        ligne.setDateEffet(dateEffet);

        if (toutVide(ligne)) {
            throw new IllegalArgumentException("Renseignez au moins une information.");
        }
        List<HistoriqueSituationPersonnelle> lignes = historique(employe.getId());
        HistoriqueSituationPersonnelle avant = lignes.isEmpty() ? null : lignes.get(0);
        if (avant != null && identique(avant, ligne)) {
            throw new IllegalArgumentException("Aucun changement par rapport à la situation actuelle.");
        }

        ligne = repository.save(ligne);
        auditService.enregistrer(acteur, AuditService.UPDATE, AuditService.EMPLOYE, employe.getId(),
                valeursDe(avant), valeursDe(ligne));
        return ligne;
    }

    private static String vide(String valeur) {
        if (valeur == null) {
            return null;
        }
        String t = valeur.trim();
        return t.isEmpty() ? null : t;
    }

    private static String texte(String valeur, int max, String libelle) {
        String t = vide(valeur);
        if (t != null && t.length() > max) {
            throw new IllegalArgumentException(libelle + " ne doit pas dépasser " + max + " caractères.");
        }
        return t;
    }

    private static boolean toutVide(HistoriqueSituationPersonnelle l) {
        return l.getTelephone() == null && l.getEmailPersonnel() == null && l.getAdresse() == null
                && l.getCommune() == null && l.getRegion() == null && l.getSituationMatrimoniale() == null
                && l.getNombreEnfants() == null && l.getContactUrgenceNom() == null
                && l.getContactUrgenceRelation() == null && l.getContactUrgenceTelephone() == null;
    }

    /** Compare les informations, sans la date d'effet. */
    private static boolean identique(HistoriqueSituationPersonnelle a, HistoriqueSituationPersonnelle b) {
        return Objects.equals(a.getTelephone(), b.getTelephone())
                && Objects.equals(a.getEmailPersonnel(), b.getEmailPersonnel())
                && Objects.equals(a.getAdresse(), b.getAdresse())
                && Objects.equals(a.getCommune(), b.getCommune())
                && Objects.equals(a.getRegion(), b.getRegion())
                && Objects.equals(a.getSituationMatrimoniale(), b.getSituationMatrimoniale())
                && Objects.equals(a.getNombreEnfants(), b.getNombreEnfants())
                && Objects.equals(a.getContactUrgenceNom(), b.getContactUrgenceNom())
                && Objects.equals(a.getContactUrgenceRelation(), b.getContactUrgenceRelation())
                && Objects.equals(a.getContactUrgenceTelephone(), b.getContactUrgenceTelephone());
    }

    private static Map<String, Object> valeursDe(HistoriqueSituationPersonnelle l) {
        if (l == null) {
            return null;
        }
        return AuditService.valeurs(
                "telephone", l.getTelephone(),
                "emailPersonnel", l.getEmailPersonnel(),
                "adresse", l.getAdresse(),
                "commune", l.getCommune(),
                "region", l.getRegion(),
                "situationMatrimoniale", l.getSituationMatrimoniale(),
                "nombreEnfants", l.getNombreEnfants(),
                "contactUrgenceNom", l.getContactUrgenceNom(),
                "contactUrgenceRelation", l.getContactUrgenceRelation(),
                "contactUrgenceTelephone", l.getContactUrgenceTelephone(),
                "dateEffet", l.getDateEffet());
    }
}