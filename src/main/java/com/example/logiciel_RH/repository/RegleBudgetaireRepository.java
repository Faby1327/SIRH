package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.RegleBudgetaire;

public interface RegleBudgetaireRepository extends JpaRepository<RegleBudgetaire, Long> {

    Optional<RegleBudgetaire> findByPoste_IdPoste(Long idPoste);
}
