package com.example.logiciel_RH.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Absence;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findAllByOrderByDateDebutDesc();

    @Query("select count(a) > 0 from Absence a "
         + "where a.employe.id = :employeId "
         + "and a.statut = :statut "
         + "and a.dateDebut <= :dateTravail and a.dateFin >= :dateTravail")
    boolean estAbsent(@Param("employeId") Long employeId,
                      @Param("dateTravail") LocalDate dateTravail,
                      @Param("statut") Absence.Statut statut);
}
