package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.HistoriqueSituationPersonnelle;

public interface HistoriqueSituationPersonnelleRepository
        extends JpaRepository<HistoriqueSituationPersonnelle, Long> {

    List<HistoriqueSituationPersonnelle> findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(Long employeId);

    Optional<HistoriqueSituationPersonnelle> findFirstByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(Long employeId);
}