package com.example.logiciel_RH.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.logiciel_RH.entity.HistoriqueDemandeConge;

public interface HistoriqueDemandeCongeRepository extends JpaRepository<HistoriqueDemandeConge, Long> {

    List<HistoriqueDemandeConge> findByDemande_IdOrderByDateActionAsc(Long id);
}
