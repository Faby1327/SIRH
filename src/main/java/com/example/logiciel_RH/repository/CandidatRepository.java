package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Candidat;

public interface CandidatRepository extends JpaRepository<Candidat, Long> {

    Optional<Candidat> findByPersonne_Utilisateur_IdUtilisateur(Long idUtilisateur);
}
