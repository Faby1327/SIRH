package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.AffectationHoraire;

public interface AffectationHoraireRepository extends JpaRepository<AffectationHoraire, Long> {
    List<AffectationHoraire> findAllByOrderByDateDebutDesc();
    List<AffectationHoraire> findByEmploye_IdOrderByDateDebutDesc(Long employeId);
}
