package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.HistoriqueEmploye;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.HistoriqueEmployeRepository;

/**
 * Historique des changements professionnels d'un employé (bloc 5) :
 * poste, département, supérieur hiérarchique et salaire.
 */
@Service
public class HistoriqueService {

    private final HistoriqueEmployeRepository historiqueEmployeRepository;
    private final AuditService auditService;

    public HistoriqueService(HistoriqueEmployeRepository historiqueEmployeRepository,
                             AuditService auditService) {
        this.historiqueEmployeRepository = historiqueEmployeRepository;
        this.auditService = auditService;
    }

    /** Lignes d'un employé, de la plus récente à la plus ancienne. */
    @Transactional(readOnly = true)
    public List<HistoriqueEmploye> historique(Long employeId) {
        return historiqueEmployeRepository
                .findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(employeId);
    }

    /**
     * Appelé quand un contrat est créé : le supérieur hiérarchique est repris
     * de la ligne la plus récente, pour qu'un changement de salaire ne l'efface pas.
     */
    @Transactional
    public HistoriqueEmploye enregistrerChangementProfessionnel(Employe employe, Poste poste,
                                                                BigDecimal salaire, LocalDate dateEffet) {
        return enregistrer(employe, poste, salaire, dateEffet, superieurActuel(employe));
    }

    /**
     * Saisie manuelle par un RH (par exemple un nouveau supérieur) : enregistre
     * la ligne et trace l'action dans le journal avec les valeurs avant et après.
     */
    @Transactional
    public HistoriqueEmploye modifierSituationProfessionnelle(Utilisateur acteur, Employe employe,
                                                              Poste poste, BigDecimal salaire,
                                                              LocalDate dateEffet, Employe superieur) {
        HistoriqueEmploye avant = derniereLigne(employe);
        HistoriqueEmploye apres = enregistrer(employe, poste, salaire, dateEffet, superieur);
        auditService.enregistrer(acteur, AuditService.UPDATE, AuditService.EMPLOYE, employe.getId(),
                valeursDe(avant), valeursDe(apres));
        return apres;
    }

    private HistoriqueEmploye enregistrer(Employe employe, Poste poste, BigDecimal salaire,
                                          LocalDate dateEffet, Employe superieur) {
        if (employe == null || employe.getId() == null) {
            throw new IllegalArgumentException("Employé obligatoire pour l'historique.");
        }
        if (poste == null) {
            throw new IllegalArgumentException("Poste obligatoire pour l'historique.");
        }
        if (poste.getDepartement() == null) {
            throw new IllegalArgumentException("Le poste n'a pas de département.");
        }
        if (salaire == null || salaire.signum() < 0) {
            throw new IllegalArgumentException("Salaire invalide pour l'historique.");
        }
        if (dateEffet == null) {
            throw new IllegalArgumentException("Date d'effet obligatoire pour l'historique.");
        }
        verifierSuperieur(employe, superieur);

        HistoriqueEmploye ligne = new HistoriqueEmploye();
        ligne.setEmploye(employe);
        ligne.setPoste(poste);
        ligne.setDepartement(poste.getDepartement());
        ligne.setSuperieur(superieur);
        ligne.setSalaire(salaire);
        ligne.setDateEffet(dateEffet);
        return historiqueEmployeRepository.save(ligne);
    }

    /** Refuse un supérieur qui est l'employé lui-même, ou qui créerait une boucle hiérarchique. */
    private void verifierSuperieur(Employe employe, Employe superieur) {
        if (superieur == null) {
            return;
        }
        if (superieur.getId() == null) {
            throw new IllegalArgumentException("Supérieur hiérarchique introuvable.");
        }
        if (superieur.getId().equals(employe.getId())) {
            throw new IllegalArgumentException("Un employé ne peut pas être son propre supérieur.");
        }
        Employe courant = superieur;
        for (int garde = 0; courant != null && garde < 50; garde++) {
            Employe suivant = superieurActuel(courant);
            if (suivant != null && suivant.getId().equals(employe.getId())) {
                throw new IllegalArgumentException(
                        "Ce supérieur créerait une boucle dans la hiérarchie.");
            }
            courant = suivant;
        }
    }

    private HistoriqueEmploye derniereLigne(Employe employe) {
        List<HistoriqueEmploye> lignes = historique(employe.getId());
        return lignes.isEmpty() ? null : lignes.get(0);
    }

    private Employe superieurActuel(Employe employe) {
        HistoriqueEmploye ligne = derniereLigne(employe);
        return ligne == null ? null : ligne.getSuperieur();
    }

    private Map<String, Object> valeursDe(HistoriqueEmploye ligne) {
        if (ligne == null) {
            return null;
        }
        Employe sup = ligne.getSuperieur();
        String superieur = sup == null ? null
                : sup.getMatricule() + " - " + sup.getPersonne().getNomComplet();
        return AuditService.valeurs(
                "poste", ligne.getPoste().getIntitule(),
                "departement", ligne.getDepartement().getNom(),
                "superieur", superieur,
                "salaire", ligne.getSalaire(),
                "dateEffet", ligne.getDateEffet());
    }
}