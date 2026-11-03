package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Personne;

public interface PersonneRepository extends JpaRepository<Personne, Long> {

    Optional<Personne> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
