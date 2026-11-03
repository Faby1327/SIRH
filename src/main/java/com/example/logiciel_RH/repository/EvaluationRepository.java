package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Evaluation;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByEntretien_IdAndEvaluateur_IdUtilisateur(Long idEntretien, Long idUtilisateur);

    List<Evaluation> findByEntretien_IdOrderByDateEvaluationAsc(Long idEntretien);

    @Query("""
            select ev from Evaluation ev
            join fetch ev.evaluateur
            join fetch ev.entretien e
            left join fetch ev.notes n
            left join fetch n.critere
            where e.candidature.idCandidature = :idCandidature
            order by ev.dateEvaluation asc
            """)
    List<Evaluation> findByCandidature(@Param("idCandidature") Long idCandidature);

    @Query("""
            select ev from Evaluation ev
            join fetch ev.evaluateur
            join fetch ev.entretien e
            where ev.evaluateur.idUtilisateur = :idEvaluateur
            order by ev.dateEvaluation desc
            """)
    List<Evaluation> findByEvaluateur(@Param("idEvaluateur") Long idEvaluateur);

    long countByEntretien_Id(Long idEntretien);
}
