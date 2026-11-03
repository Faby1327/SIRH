package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.PaiementSalaire;

public interface PaiementSalaireRepository extends JpaRepository<PaiementSalaire, Long> {
    Optional<PaiementSalaire> findByFichePaie_Id(Long fichePaieId);
}
