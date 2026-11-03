package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Contrat;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByEmploye_IdOrderByDateDebutDesc(Long employeId);

    List<Contrat> findByEmploye_IdAndStatut(Long employeId, Contrat.Statut statut);

    List<Contrat> findAllByOrderByDateDebutDesc();

    List<Contrat> findByStatut(Contrat.Statut statut);
}
