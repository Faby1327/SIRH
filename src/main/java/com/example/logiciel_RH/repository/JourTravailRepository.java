package com.example.logiciel_RH.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.JourTravail;

public interface JourTravailRepository extends JpaRepository<JourTravail, Long> {
    List<JourTravail> findByDateTravailBetweenOrderByDateTravailAscEmploye_IdAsc(LocalDate debut, LocalDate fin);
    Optional<JourTravail> findByEmploye_IdAndDateTravail(Long employeId, LocalDate dateTravail);
    List<JourTravail> findByEmploye_IdAndDateTravailBetween(Long employeId, LocalDate debut, LocalDate fin);
}
