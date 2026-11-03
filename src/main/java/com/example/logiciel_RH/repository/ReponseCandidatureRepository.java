package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.ReponseCandidature;

public interface ReponseCandidatureRepository extends JpaRepository<ReponseCandidature, Long> {

    List<ReponseCandidature> findByCandidature_IdCandidatureOrderByCritere_OrdreAscIdReponseAsc(Long idCandidature);
}
