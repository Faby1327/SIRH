package com.example.logiciel_RH.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.HeureSupplementaire;
import com.example.logiciel_RH.repository.HeureSupplementaireRepository;

@Service
public class HeureSupplementaireService {

    private final HeureSupplementaireRepository repository;

    public HeureSupplementaireService(HeureSupplementaireRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<HeureSupplementaire> lister() {
        return repository.findAllByOrderByDateDemandeDesc();
    }

    @Transactional
    public HeureSupplementaire valider(Long id) {
        return changerStatut(id, HeureSupplementaire.Statut.VALIDEE);
    }

    @Transactional
    public HeureSupplementaire refuser(Long id) {
        return changerStatut(id, HeureSupplementaire.Statut.REFUSEE);
    }

    private HeureSupplementaire changerStatut(Long id, HeureSupplementaire.Statut statut) {
        HeureSupplementaire heure = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Heure supplémentaire introuvable."));
        if (!HeureSupplementaire.Statut.DEMANDEE.name().equals(heure.getStatut())) {
            throw new IllegalArgumentException("Cette demande a déjà été traitée.");
        }
        heure.setStatut(statut.name());
        heure.setDateValidation(LocalDateTime.now());
        return repository.save(heure);
    }
}
