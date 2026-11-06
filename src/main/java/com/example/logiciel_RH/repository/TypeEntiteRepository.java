package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.TypeEntite;

public interface TypeEntiteRepository extends JpaRepository<TypeEntite, Long> {

    Optional<TypeEntite> findByCode(String code);
}