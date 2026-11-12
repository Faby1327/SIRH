package com.example.logiciel_RH.repository;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.ParticipationMission;

public interface ParticipationMissionRepository extends JpaRepository<ParticipationMission, Long> {
    List<ParticipationMission> findByMission_IdOrderByDateAffectationAsc(Long missionId);
    List<ParticipationMission> findByEmploye_IdOrderByDateAffectationDesc(Long employeId);

    @Query("select count(p) > 0 from ParticipationMission p where p.employe.id = :employeId "
         + "and p.statut in (com.example.logiciel_RH.entity.ParticipationMission.Statut.AFFECTEE, com.example.logiciel_RH.entity.ParticipationMission.Statut.EN_COURS) "
         + "and p.mission.statut in (com.example.logiciel_RH.entity.Mission.Statut.A_FAIRE, com.example.logiciel_RH.entity.Mission.Statut.EN_COURS) "
         + "and p.mission.dateDebut <= :fin and coalesce(p.mission.dateFinPrevue, p.mission.dateDebut) >= :debut")
    boolean estAffectePendant(@Param("employeId") Long employeId, @Param("debut") LocalDate debut,
                              @Param("fin") LocalDate fin);
}
