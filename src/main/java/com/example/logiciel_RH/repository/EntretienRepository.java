package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Entretien;

public interface EntretienRepository extends JpaRepository<Entretien, Long> {

    @Query("""
            select distinct e from Entretien e
            join fetch e.candidature c
            join fetch c.offre o
            join fetch o.demande d
            join fetch d.poste p
            left join fetch e.evaluationsDesignees ed
            left join fetch ed.evaluateur
            where c.offre.idOffre = :idOffre
            order by e.dateHeure desc
            """)
    List<Entretien> findByOffre(@Param("idOffre") Long idOffre);

    @Query("""
            select distinct e from Entretien e
            join fetch e.candidature c
            join fetch c.offre o
            join fetch o.demande d
            join fetch d.poste p
            left join fetch e.evaluationsDesignees ed
            left join fetch ed.evaluateur
            order by e.dateHeure desc
            """)
    List<Entretien> findAllAvecRelations();

    @Query("""
            select distinct e from Entretien e
            join fetch e.candidature c
            join fetch c.offre o
            join fetch o.demande d
            join fetch d.poste p
            left join fetch e.evaluationsDesignees ed
            left join fetch ed.evaluateur
            where c.idCandidature = :idCandidature
            order by e.dateHeure desc
            """)
    List<Entretien> findByCandidature(@Param("idCandidature") Long idCandidature);

    @Query("""
            select distinct e from Entretien e
            join fetch e.candidature c
            join fetch c.offre o
            join fetch o.demande d
            join fetch d.poste p
            left join fetch e.evaluationsDesignees ed
            left join fetch ed.evaluateur
            where e.id = :id
            """)
    Optional<Entretien> findByIdAvecRelations(@Param("id") Long id);

    @Query("""
            select distinct e from Entretien e
            join fetch e.candidature c
            join fetch c.offre o
            join fetch o.demande d
            join fetch d.poste p
            left join fetch e.evaluationsDesignees ed
            join ed.evaluateur u
            where u.idUtilisateur = :idEvaluateur
            order by e.dateHeure desc
            """)
    List<Entretien> findPourEvaluateur(@Param("idEvaluateur") Long idEvaluateur);
}
