package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.DetailJourTravail;

public interface DetailJourTravailRepository extends JpaRepository<DetailJourTravail, Long> {
    List<DetailJourTravail> findByJourTravail_IdOrderByHeureDebutAsc(Long jourTravailId);
}
