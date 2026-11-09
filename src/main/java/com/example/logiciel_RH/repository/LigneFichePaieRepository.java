package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.LigneFichePaie;

public interface LigneFichePaieRepository extends JpaRepository<LigneFichePaie, Long> {
    List<LigneFichePaie> findByFichePaie_IdOrderByIdAsc(Long fichePaieId);
    List<LigneFichePaie> findByFichePaie_IdAndSource(Long fichePaieId, String source);
    void deleteByFichePaie_IdAndSource(Long fichePaieId, String source);
}
