package com.example.logiciel_RH.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.AffectationHoraire;
import com.example.logiciel_RH.entity.Absence;
import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.JourTravail;
import com.example.logiciel_RH.entity.HeureSupplementaire;
import com.example.logiciel_RH.repository.AffectationHoraireRepository;
import com.example.logiciel_RH.repository.AbsenceRepository;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.HeureSupplementaireRepository;
import com.example.logiciel_RH.repository.JourTravailRepository;

@Service
public class PlanningJourService {

    private final AffectationHoraireRepository affectationRepository;
    private final JourTravailRepository jourRepository;
    private final ContratRepository contratRepository;
    private final AbsenceRepository absenceRepository;
    private final HeureSupplementaireRepository heureSupplementaireRepository;

    public PlanningJourService(AffectationHoraireRepository affectationRepository,
                               JourTravailRepository jourRepository,
                               ContratRepository contratRepository,
                               AbsenceRepository absenceRepository,
                               HeureSupplementaireRepository heureSupplementaireRepository) {
        this.affectationRepository = affectationRepository;
        this.jourRepository = jourRepository;
        this.contratRepository = contratRepository;
        this.absenceRepository = absenceRepository;
        this.heureSupplementaireRepository = heureSupplementaireRepository;
    }

    @Transactional
    public List<JourTravail> genererEtLister(YearMonth mois) {
        LocalDate debutMois = mois.atDay(1);
        LocalDate finMois = mois.atEndOfMonth();

        for (AffectationHoraire affectation : affectationRepository.findAll()) {
            if (!affectation.isActif() || affectation.getDateDebut().isAfter(finMois)
                    || affectation.getDateFin() != null && affectation.getDateFin().isBefore(debutMois)) {
                continue;
            }

            LocalDate debut = affectation.getDateDebut().isAfter(debutMois)
                    ? affectation.getDateDebut() : debutMois;
            LocalDate fin = affectation.getDateFin() != null && affectation.getDateFin().isBefore(finMois)
                    ? affectation.getDateFin() : finMois;
            Contrat contrat = contratRepository
                    .findByEmploye_IdAndStatut(affectation.getEmploye().getId(), Contrat.Statut.EN_COURS)
                    .stream().findFirst().orElse(null);

            for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {
                if (!affectation.getJoursSemaine().contains(date.getDayOfWeek().getValue())) continue;
                boolean absenceValidee = absenceRepository.estAbsent(
                        affectation.getEmploye().getId(), date, Absence.Statut.VALIDEE);
                var jourExistant = jourRepository.findByEmploye_IdAndDateTravail(
                        affectation.getEmploye().getId(), date);
                if (absenceValidee) {
                    if (jourExistant.isPresent()) {
                        JourTravail jour = jourExistant.get();
                        if (!"POINTAGE_VALIDE".equals(jour.getStatut())
                                && !"VALIDEE".equals(jour.getStatut())) {
                            jour.setStatut("ABSENCE");
                            jour.setHeureEntreeReelle(null);
                            jour.setHeureSortieReelle(null);
                            jourRepository.save(jour);
                        }
                    } else {
                        JourTravail jour = new JourTravail();
                        jour.setEmploye(affectation.getEmploye());
                        jour.setContrat(contrat);
                        jour.setAffectationHoraire(affectation);
                        jour.setDateTravail(date);
                        jour.setHeureDebutPrevue(affectation.getModeleHoraire().getHeureDebut());
                        jour.setHeureFinPrevue(affectation.getModeleHoraire().getHeureFin());
                        jour.setPausePrevueMinutes(affectation.getModeleHoraire().getPausePrevueMinutes());
                        jour.setStatut("ABSENCE");
                        jourRepository.save(jour);
                    }
                    continue;
                }
                if (jourExistant.isPresent()) continue;

                JourTravail jour = new JourTravail();
                jour.setEmploye(affectation.getEmploye());
                jour.setContrat(contrat);
                jour.setAffectationHoraire(affectation);
                jour.setDateTravail(date);
                jour.setHeureDebutPrevue(affectation.getModeleHoraire().getHeureDebut());
                jour.setHeureFinPrevue(affectation.getModeleHoraire().getHeureFin());
                jour.setPausePrevueMinutes(affectation.getModeleHoraire().getPausePrevueMinutes());
                jour.setStatut("PLANIFIEE");
                jourRepository.save(jour);
            }
        }
        return jourRepository.findByDateTravailBetweenOrderByDateTravailAscEmploye_IdAsc(debutMois, finMois);
    }

    @Transactional
    public void enregistrerHeures(Long id, java.time.LocalTime entree, java.time.LocalTime sortie) {
        JourTravail jour = jourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journée de travail introuvable."));
        if ("POINTAGE_VALIDE".equals(jour.getStatut())
                || "DETAIL_A_VERIFIER".equals(jour.getStatut())
                || "VALIDEE".equals(jour.getStatut())
                || "ABSENCE".equals(jour.getStatut())) {
            throw new IllegalArgumentException("Le pointage de cette journée a déjà été validé par les RH.");
        }
        if (entree == null || sortie == null) throw new IllegalArgumentException("Les deux heures sont obligatoires.");
        if (sortie.isBefore(entree)) throw new IllegalArgumentException("L'heure de sortie doit être après l'entrée.");

        int total = (int) (java.time.Duration.between(entree, sortie).toMinutes());
        int travaille = Math.max(0, total - jour.getPausePrevueMinutes());
        int retard = 0;
        if (jour.getHeureDebutPrevue() != null && entree.isAfter(jour.getHeureDebutPrevue())) {
            retard = (int) java.time.Duration.between(jour.getHeureDebutPrevue(), entree).toMinutes();
        }
        int supplementaire = 0;
        if (jour.getHeureFinPrevue() != null && sortie.isAfter(jour.getHeureFinPrevue())) {
            supplementaire = (int) java.time.Duration.between(jour.getHeureFinPrevue(), sortie).toMinutes();
        }
        jour.setHeureEntreeReelle(entree);
        jour.setHeureSortieReelle(sortie);
        jour.setMinutesTravaillees(travaille);
        jour.setMinutesRetard(retard);
        jour.setMinutesSupplementaires(supplementaire);
        // La validation du pointage est distincte de la validation des détails.
        jour.setStatut("POINTAGE_A_VERIFIER");
        jourRepository.save(jour);
    }

    @Transactional
    public void validerJournee(Long id) {
        JourTravail jour = jourRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Journée de travail introuvable."));
        if (jour.getHeureEntreeReelle() == null || jour.getHeureSortieReelle() == null) {
            throw new IllegalArgumentException("Les heures d'entrée et de sortie doivent être renseignées avant la validation.");
        }
        jour.setStatut("POINTAGE_VALIDE");
        jourRepository.save(jour);
        creerDemandeHeuresSupplementaires(jour);
    }

    private void creerDemandeHeuresSupplementaires(JourTravail jour) {
        Integer minutes = jour.getMinutesSupplementaires();
        if (minutes == null || minutes <= 0 || jour.getHeureSortieReelle() == null
                || jour.getHeureFinPrevue() == null
                || heureSupplementaireRepository.findByJourTravail_Id(jour.getId()).isPresent()) {
            return;
        }

        HeureSupplementaire heure = new HeureSupplementaire();
        heure.setJourTravail(jour);
        heure.setEmploye(jour.getEmploye());
        heure.setDateTravail(jour.getDateTravail());
        heure.setHeureDebut(jour.getHeureFinPrevue());
        heure.setHeureFin(jour.getHeureSortieReelle());
        heure.setNombreMinutes(minutes);
        heure.setMotif("Heures supplémentaires calculées à partir du pointage validé.");
        heure.setStatut("DEMANDEE");
        heure.setDateDemande(java.time.LocalDateTime.now());
        heureSupplementaireRepository.save(heure);
    }

    @Transactional(readOnly = true)
    public boolean estValidee(Long id) {
        return jourRepository.findById(id)
                .map(jour -> "VALIDEE".equals(jour.getStatut()))
                .orElse(false);
    }
}
