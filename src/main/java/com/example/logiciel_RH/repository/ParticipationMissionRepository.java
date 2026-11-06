package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.ParticipationMission;

public interface ParticipationMissionRepository extends JpaRepository<ParticipationMission, Long> {
    List<ParticipationMission> findByMission_IdOrderByDateAffectationAsc(Long missionId);
    List<ParticipationMission> findByEmploye_IdOrderByDateAffectationDesc(Long employeId);
}
