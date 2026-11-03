package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.HistoriqueStatutDemande;

public interface HistoriqueStatutDemandeRepository
        extends JpaRepository<HistoriqueStatutDemande, Long> {

    List<HistoriqueStatutDemande> findByDemande_IdDemandeOrderByDateChangementAscIdHistoriqueAsc(Long idDemande);

    void deleteAllByDemande_IdDemande(Long idDemande);
}
