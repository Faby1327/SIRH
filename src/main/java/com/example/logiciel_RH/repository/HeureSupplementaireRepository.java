package com.example.logiciel_RH.repository;

import java.util.Optional;
import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.HeureSupplementaire;

public interface HeureSupplementaireRepository extends JpaRepository<HeureSupplementaire, Long> {
    Optional<HeureSupplementaire> findByJourTravail_Id(Long jourTravailId);
    List<HeureSupplementaire> findAllByOrderByDateDemandeDesc();
    List<HeureSupplementaire> findByEmploye_IdAndDateTravailBetweenAndStatut(
            Long employeId, LocalDate debut, LocalDate fin, String statut);
}
