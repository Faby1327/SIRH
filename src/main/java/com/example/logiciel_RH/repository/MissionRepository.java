package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.Mission;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByOrderByDateDebutDesc();
}
