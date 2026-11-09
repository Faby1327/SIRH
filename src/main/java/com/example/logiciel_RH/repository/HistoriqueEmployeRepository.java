package com.example.logiciel_RH.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.logiciel_RH.entity.HistoriqueEmploye;

public interface HistoriqueEmployeRepository extends JpaRepository<HistoriqueEmploye, Long> {
    List<HistoriqueEmploye> findByEmploye_IdOrderByDateEffetDescDateEnregistrementDesc(Long employeId);
}
