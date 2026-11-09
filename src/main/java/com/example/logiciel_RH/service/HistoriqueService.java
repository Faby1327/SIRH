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

@Service
public class HistoriqueService {
    private final HistoriqueEmployeRepository repository;
    private final AuditService auditService;

    public HistoriqueService(HistoriqueEmployeRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<HistoriqueEmploye> historique(Long employeId) {
        return repository.findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(employeId);
    }

    @Transactional
    public HistoriqueEmploye enregistrerChangementProfessionnel(Employe employe, Poste poste,
                                                                 BigDecimal salaire, LocalDate dateEffet) {
        return enregistrer(employe, poste, salaire, dateEffet, superieurActuel(employe));
    }

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
        if (employe == null || employe.getId() == null || poste == null || poste.getDepartement() == null
                || salaire == null || salaire.signum() < 0 || dateEffet == null) {
            throw new IllegalArgumentException("Données professionnelles invalides pour l'historique.");
        }
        if (superieur != null && (superieur.getId() == null || superieur.getId().equals(employe.getId()))) {
            throw new IllegalArgumentException("Supérieur hiérarchique invalide.");
        }
        HistoriqueEmploye ligne = new HistoriqueEmploye();
        ligne.setEmploye(employe); ligne.setPoste(poste); ligne.setDepartement(poste.getDepartement());
        ligne.setSuperieur(superieur); ligne.setSalaire(salaire); ligne.setDateEffet(dateEffet);
        return repository.save(ligne);
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
        if (ligne == null) return null;
        return AuditService.valeurs("poste", ligne.getPoste().getIntitule(),
                "departement", ligne.getDepartement().getNom(), "salaire", ligne.getSalaire(),
                "dateEffet", ligne.getDateEffet());
    }
}
