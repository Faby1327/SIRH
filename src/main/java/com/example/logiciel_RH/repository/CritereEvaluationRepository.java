package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.CritereEvaluation;

public interface CritereEvaluationRepository extends JpaRepository<CritereEvaluation, Long> {

    List<CritereEvaluation> findByOffre_IdOffreOrderByOrdreAscIdAsc(Long idOffre);

    long countByOffre_IdOffre(Long idOffre);
}
