package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.HistoriqueProposition;

public interface HistoriquePropositionRepository extends JpaRepository<HistoriqueProposition, Long> {

    List<HistoriqueProposition> findByProposition_IdOrderByDateChangementAscIdAsc(Long propositionId);
}
