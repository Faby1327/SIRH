package com.example.logiciel_RH.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Absence;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findAllByOrderByDateDebutDesc();

    List<Absence> findByEmploye_IdOrderByDateDebutDesc(Long employeId);

    List<Absence> findByEmploye_IdAndTypeAbsenceAndStatut(Long employeId, Absence.TypeAbsence type, Absence.Statut statut);

    @Query("select count(a) > 0 from Absence a where a.employe.id = :employeId and a.statut in (com.example.logiciel_RH.entity.Absence.Statut.DEMANDEE, com.example.logiciel_RH.entity.Absence.Statut.VALIDEE) and a.dateDebut <= :fin and a.dateFin >= :debut")
    boolean aChevauchement(@Param("employeId") Long employeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("select count(a) > 0 from Absence a where a.employe.id = :employeId and a.typeAbsence = :type and a.statut in (com.example.logiciel_RH.entity.Absence.Statut.DEMANDEE, com.example.logiciel_RH.entity.Absence.Statut.VALIDEE) and a.dateDebut <= :fin and a.dateFin >= :debut")
    boolean aChevauchementDeType(@Param("employeId") Long employeId, @Param("debut") LocalDate debut,
                                @Param("fin") LocalDate fin, @Param("type") Absence.TypeAbsence type);

    @Query("select count(a) > 0 from Absence a where a.employe.id = :employeId and a.statut in (com.example.logiciel_RH.entity.Absence.Statut.DEMANDEE, com.example.logiciel_RH.entity.Absence.Statut.VALIDEE) and a.dateDebut <= :fin and a.dateFin >= :debut")
    boolean estOccupe(@Param("employeId") Long employeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("select count(a) > 0 from Absence a where a.remplacant.id = :employeId and a.statut in (com.example.logiciel_RH.entity.Absence.Statut.DEMANDEE, com.example.logiciel_RH.entity.Absence.Statut.VALIDEE) and a.dateDebut <= :fin and a.dateFin >= :debut")
    boolean estEngageCommeRemplacant(@Param("employeId") Long employeId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("select a from Absence a where a.employe.id = :employeId "
         + "and a.statut = :statut and a.impactPaie = true "
         + "and a.dateDebut <= :fin and a.dateFin >= :debut "
         + "order by a.dateDebut")
    List<Absence> findAbsencesAvecRetenue(@Param("employeId") Long employeId,
                                          @Param("debut") LocalDate debut,
                                          @Param("fin") LocalDate fin,
                                          @Param("statut") Absence.Statut statut);

    @Query("select count(a) > 0 from Absence a "
         + "where a.employe.id = :employeId "
         + "and a.statut = :statut "
         + "and a.dateDebut <= :dateTravail and a.dateFin >= :dateTravail")
    boolean estAbsent(@Param("employeId") Long employeId,
                      @Param("dateTravail") LocalDate dateTravail,
                      @Param("statut") Absence.Statut statut);
}
