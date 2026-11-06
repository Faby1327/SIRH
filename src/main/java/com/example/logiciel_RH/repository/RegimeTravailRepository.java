package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.RegimeTravail;

public interface RegimeTravailRepository extends JpaRepository<RegimeTravail, Long> {
    List<RegimeTravail> findByActifTrueOrderByNomAsc();
}
