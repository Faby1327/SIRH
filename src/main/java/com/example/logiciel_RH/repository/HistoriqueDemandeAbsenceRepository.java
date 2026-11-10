package com.example.logiciel_RH.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.logiciel_RH.entity.HistoriqueDemandeAbsence;

public interface HistoriqueDemandeAbsenceRepository extends JpaRepository<HistoriqueDemandeAbsence, Long> {

    List<HistoriqueDemandeAbsence> findByDemande_IdOrderByDateActionAsc(Long id);
}
