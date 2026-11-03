package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.PropositionEmbauche;

public interface PropositionEmbaucheRepository extends JpaRepository<PropositionEmbauche, Long> {

    Optional<PropositionEmbauche> findByCandidature_IdCandidature(Long idCandidature);

    boolean existsByCandidature_IdCandidature(Long idCandidature);

    @Query("""
            select p from PropositionEmbauche p
            join fetch p.candidature c
            join fetch p.offre o
            left join fetch p.proposePar
            where o.idOffre = :idOffre
            order by p.dateProposition desc
            """)
    List<PropositionEmbauche> findByOffre(@Param("idOffre") Long idOffre);

    @Query("""
            select count(p) from PropositionEmbauche p
            where p.offre.idOffre = :idOffre
              and p.statut in :statuts
            """)
    long countByOffreIdAndStatutIn(@Param("idOffre") Long idOffre, @Param("statuts") List<String> statuts);

    @Query("""
            select p from PropositionEmbauche p
            join fetch p.candidature c
            join fetch c.candidat cand
            join fetch p.offre o
            left join fetch p.proposePar
            where cand.idCandidat = :idCandidat
              and p.statut in ('PROPOSEE', 'DEMANDE_SALAIRE', 'DEMANDE_CONTRAT', 'DEMANDE_CONDITIONS', 'EN_ATTENTE_CONFIRMATION', 'CONFIRMEE', 'ACCEPTEE', 'REFUSEE')
            order by p.dateProposition desc
            """)
    Optional<PropositionEmbauche> findActivePourCandidat(@Param("idCandidat") Long idCandidat);

    @Query("""
            select p from PropositionEmbauche p
            join fetch p.candidature c
            join fetch c.candidat cand
            join fetch p.offre o
            where p.id = :id
            """)
    Optional<PropositionEmbauche> findByIdAvecRelations(@Param("id") Long id);
}
