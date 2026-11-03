package com.example.logiciel_RH.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Departement;

public interface DepartementRepository
        extends JpaRepository<Departement, Long> {

}
