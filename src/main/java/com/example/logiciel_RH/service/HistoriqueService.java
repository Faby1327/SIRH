package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.HistoriqueEmploye;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.repository.HistoriqueEmployeRepository;

/**
 * Historique des changements professionnels d'un employé (bloc 5) :
 * poste, département, supérieur hiérarchique et salaire.
 */
@Service
public class HistoriqueService {

    private final HistoriqueEmployeRepository historiqueEmployeRepository;

    public HistoriqueService(HistoriqueEmployeRepository historiqueEmployeRepository) {
        this.historiqueEmployeRepository = historiqueEmployeRepository;
    }

    /**
     * Ajoute une ligne d'historique.
     * Le département se déduit du poste. Le supérieur hiérarchique est repris
     * de la ligne la plus récente, pour qu'un changement de salaire ne l'efface pas.
     */
    @Transactional
    public HistoriqueEmploye enregistrerChangementProfessionnel(Employe employe, Poste poste,
                                                                BigDecimal salaire, LocalDate dateEffet) {
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

        List<HistoriqueEmploye> anciens = historiqueEmployeRepository
                .findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(employe.getId());
        Employe superieur = anciens.isEmpty() ? null : anciens.get(0).getSuperieur();

        HistoriqueEmploye ligne = new HistoriqueEmploye();
        ligne.setEmploye(employe);
        ligne.setPoste(poste);
        ligne.setDepartement(poste.getDepartement());
        ligne.setSuperieur(superieur);
        ligne.setSalaire(salaire);
        ligne.setDateEffet(dateEffet);
        return historiqueEmployeRepository.save(ligne);
    }
}