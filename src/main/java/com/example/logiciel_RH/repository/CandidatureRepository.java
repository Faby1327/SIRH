package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Candidature;

public interface CandidatureRepository extends JpaRepository<Candidature, Long> {

    List<Candidature> findByOffre_IdOffreOrderByDateCandidatureDesc(Long idOffre);

    List<Candidature> findByStatutInOrderByDateCandidatureAsc(List<String> statuts);

    List<Candidature> findByOffre_Demande_Poste_Departement_IdDepartementAndStatutInOrderByDateCandidatureAsc(
            Long idDepartement, List<String> statuts);

    long countByOffre_IdOffre(Long idOffre);

    boolean existsByOffre_IdOffreAndEmailIgnoreCase(Long idOffre, String email);

    boolean existsByOffre_IdOffreAndCandidat_IdCandidat(Long idOffre, Long idCandidat);
}
