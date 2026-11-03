package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.EntretienEvaluateur;

public interface EntretienEvaluateurRepository extends JpaRepository<EntretienEvaluateur, EntretienEvaluateur.EntretienEvaluateurId> {

    List<EntretienEvaluateur> findByEntretien_Id(Long idEntretien);

    boolean existsByEntretien_IdAndEvaluateur_IdUtilisateur(Long idEntretien, Long idUtilisateur);
}
