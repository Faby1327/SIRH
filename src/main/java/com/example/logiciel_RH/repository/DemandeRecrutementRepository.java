package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.Utilisateur;

public interface DemandeRecrutementRepository
        extends JpaRepository<DemandeRecrutement, Long> {

    List<DemandeRecrutement> findByManagerOrderByDateDemandeDesc(Utilisateur manager);

    Optional<DemandeRecrutement> findByIdDemandeAndManager(Long idDemande, Utilisateur manager);

    List<DemandeRecrutement> findByStatutActuel_LibelleInOrderByDateDemandeDesc(List<String> statuts);

    List<DemandeRecrutement> findAllByOrderByDateDemandeDesc();
}
