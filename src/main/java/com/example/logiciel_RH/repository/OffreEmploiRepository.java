package com.example.logiciel_RH.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.OffreEmploi;

public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long> {

    List<OffreEmploi> findByStatutOrderByDatePublicationDesc(String statut);

    List<OffreEmploi> findAllByOrderByDatePublicationDesc();

    Optional<OffreEmploi> findByDemande_IdDemande(Long idDemande);
}
