package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Statut;

public interface StatutRepository
        extends JpaRepository<Statut, Long> {

    Optional<Statut> findByLibelle(String libelle);
}
