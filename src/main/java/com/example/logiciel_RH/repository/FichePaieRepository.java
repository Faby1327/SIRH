package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.FichePaie;

public interface FichePaieRepository extends JpaRepository<FichePaie, Long> {
    Optional<FichePaie> findByEmploye_IdAndContrat_IdAndMoisAndAnnee(
            Long employeId, Long contratId, Integer mois, Integer annee);
    List<FichePaie> findByAnneeAndMoisOrderByIdDesc(Integer annee, Integer mois);
}
