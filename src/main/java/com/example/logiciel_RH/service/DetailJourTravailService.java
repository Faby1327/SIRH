package com.example.logiciel_RH.service;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.DetailJourTravail;
import com.example.logiciel_RH.entity.JourTravail;
import com.example.logiciel_RH.entity.ParticipationMission;
import com.example.logiciel_RH.repository.DetailJourTravailRepository;
import com.example.logiciel_RH.repository.JourTravailRepository;
import com.example.logiciel_RH.repository.ParticipationMissionRepository;

@Service
public class DetailJourTravailService {

    private final DetailJourTravailRepository detailRepository;
    private final JourTravailRepository jourRepository;
    private final ParticipationMissionRepository participationRepository;

    public DetailJourTravailService(DetailJourTravailRepository detailRepository,
                                    JourTravailRepository jourRepository,
                                    ParticipationMissionRepository participationRepository) {
        this.detailRepository = detailRepository;
        this.jourRepository = jourRepository;
        this.participationRepository = participationRepository;
    }

    public List<DetailJourTravail> trouverParJour(Long jourId) {
        return detailRepository.findByJourTravail_IdOrderByHeureDebutAsc(jourId);
    }

    @Transactional
    public void ajouter(Long jourId, Long employeId, String type, LocalTime debut,
                        LocalTime fin, Long participationId, String description) {
        JourTravail jour = jourRepository.findById(jourId)
                .orElseThrow(() -> new IllegalArgumentException("Journée de travail introuvable."));
        if (!jour.getEmploye().getId().equals(employeId)) {
            throw new IllegalArgumentException("Cette journée n'appartient pas à cet employé.");
        }
        if ("VALIDEE".equals(jour.getStatut()) || "ABSENCE".equals(jour.getStatut())) {
            throw new IllegalArgumentException("Cette journée est marquée comme absence et ne peut pas recevoir de détail.");
        }
        if (debut == null || fin == null || !fin.isAfter(debut)) {
            throw new IllegalArgumentException("Les horaires du détail sont invalides.");
        }
        if (jour.getHeureEntreeReelle() == null || jour.getHeureSortieReelle() == null) {
            throw new IllegalArgumentException("Renseignez d'abord l'entrée et la sortie de la journée.");
        }
        if (debut.isBefore(jour.getHeureEntreeReelle()) || fin.isAfter(jour.getHeureSortieReelle())) {
            throw new IllegalArgumentException("Le détail doit rester dans les heures réelles de présence.");
        }

        DetailJourTravail.TypeDetail typeDetail;
        try {
            typeDetail = DetailJourTravail.TypeDetail.valueOf(type);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Type de détail invalide.");
        }

        ParticipationMission participation = null;
        if (typeDetail == DetailJourTravail.TypeDetail.MISSION) {
            if (participationId == null) throw new IllegalArgumentException("Sélectionnez une mission.");
            participation = participationRepository.findById(participationId)
                    .orElseThrow(() -> new IllegalArgumentException("Participation à la mission introuvable."));
            if (!participation.getEmploye().getId().equals(employeId)) {
                throw new IllegalArgumentException("Cette mission ne vous est pas affectée.");
            }
        } else if (participationId != null) {
            throw new IllegalArgumentException("Une mission ne peut être associée qu'à un détail de type MISSION.");
        }

        for (DetailJourTravail existant : trouverParJour(jourId)) {
            if (debut.isBefore(existant.getHeureFin()) && fin.isAfter(existant.getHeureDebut())) {
                throw new IllegalArgumentException("Cette période chevauche déjà un autre détail de la journée.");
            }
        }

        DetailJourTravail detail = new DetailJourTravail();
        detail.setJourTravail(jour);
        detail.setParticipationMission(participation);
        detail.setTypeDetail(typeDetail);
        detail.setHeureDebut(debut);
        detail.setHeureFin(fin);
        detail.setNombreMinutes((int) Duration.between(debut, fin).toMinutes());
        detail.setDescription(description);
        detail.setStatut(DetailJourTravail.Statut.A_VERIFIER);
        detailRepository.save(detail);
        jour.setStatut("DETAIL_A_VERIFIER");
        jourRepository.save(jour);
    }

    @Transactional
    public void changerStatut(Long detailId, DetailJourTravail.Statut statut) {
        DetailJourTravail detail = detailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Détail introuvable."));
        detail.setStatut(statut);
        detailRepository.save(detail);
    }
}
