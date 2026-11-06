package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.ModeleHoraire;

public interface ModeleHoraireRepository extends JpaRepository<ModeleHoraire, Long> {
    List<ModeleHoraire> findByActifTrueOrderByHeureDebutAsc();
}
