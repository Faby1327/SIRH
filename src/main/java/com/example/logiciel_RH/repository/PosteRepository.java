package com.example.logiciel_RH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.example.logiciel_RH.entity.Poste;

public interface PosteRepository
        extends JpaRepository<Poste, Long> {

    List<Poste> findByDepartement_IdDepartement(Long idDepartement);

}
