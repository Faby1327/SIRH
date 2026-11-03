package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.CritereCandidature;

public interface CritereCandidatureRepository extends JpaRepository<CritereCandidature, Long> {

    List<CritereCandidature> findByPoste_IdPosteOrderByOrdreAscIdCritereAsc(Long idPoste);
}
