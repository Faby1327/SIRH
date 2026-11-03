package com.example.logiciel_RH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.EvaluationNote;

public interface EvaluationNoteRepository extends JpaRepository<EvaluationNote, Long> {

    @Modifying
    @Query("delete from EvaluationNote n where n.evaluation.id = :idEvaluation")
    void supprimerPourEvaluation(@Param("idEvaluation") Long idEvaluation);
}
