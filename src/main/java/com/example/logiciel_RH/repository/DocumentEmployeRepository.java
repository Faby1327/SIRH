package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.DocumentEmploye;

public interface DocumentEmployeRepository extends JpaRepository<DocumentEmploye, Long> {

    List<DocumentEmploye> findByEmploye_IdOrderByDateAjoutDesc(Long employeId);

    long countByEmploye_Id(Long employeId);
}
