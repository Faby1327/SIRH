package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.BudgetEntreprise;

public interface BudgetEntrepriseRepository extends JpaRepository<BudgetEntreprise, Long> {

    Optional<BudgetEntreprise> findByDepartement_IdDepartementAndAnnee(Long idDepartement, Integer annee);
}
