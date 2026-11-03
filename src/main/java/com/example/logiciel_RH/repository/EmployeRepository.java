package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.logiciel_RH.entity.Employe;

public interface EmployeRepository extends JpaRepository<Employe, Long> {

    java.util.List<Employe> findAllByOrderByDateCreationDesc();

    Optional<Employe> findByPersonne_IdPersonne(Long idPersonne);

    Optional<Employe> findByMatricule(String matricule);

    @Query(value = "select nextval('employe_matricule_seq')", nativeQuery = true)
    Long prochainNumeroMatricule();
}
