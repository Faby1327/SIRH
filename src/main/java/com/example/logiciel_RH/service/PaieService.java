package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.Absence;
import com.example.logiciel_RH.entity.FichePaie;
import com.example.logiciel_RH.entity.HeureSupplementaire;
import com.example.logiciel_RH.entity.LigneFichePaie;
import com.example.logiciel_RH.entity.PaiementSalaire;
import com.example.logiciel_RH.entity.JourTravail;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.FichePaieRepository;
import com.example.logiciel_RH.repository.PaiementSalaireRepository;
import com.example.logiciel_RH.repository.HeureSupplementaireRepository;
import com.example.logiciel_RH.repository.LigneFichePaieRepository;
import com.example.logiciel_RH.repository.AbsenceRepository;
import com.example.logiciel_RH.service.PlanningJourService;

@Service
public class PaieService {

    private static final BigDecimal HEURES_MENSUELLES = new BigDecimal("173.33");
    private static final BigDecimal TAUX_MAJORATION = new BigDecimal("1.30");

    private final ContratRepository contratRepository;
    private final FichePaieRepository fichePaieRepository;
    private final PaiementSalaireRepository paiementRepository;
    private final HeureSupplementaireRepository heureSupplementaireRepository;
    private final LigneFichePaieRepository ligneRepository;
    private final AbsenceRepository absenceRepository;
    private final PlanningJourService planningJourService;

    public PaieService(ContratRepository contratRepository,
                       FichePaieRepository fichePaieRepository,
                       PaiementSalaireRepository paiementRepository,
                       HeureSupplementaireRepository heureSupplementaireRepository,
                       LigneFichePaieRepository ligneRepository,
                       AbsenceRepository absenceRepository,
                       PlanningJourService planningJourService) {
        this.contratRepository = contratRepository;
        this.fichePaieRepository = fichePaieRepository;
        this.paiementRepository = paiementRepository;
        this.heureSupplementaireRepository = heureSupplementaireRepository;
        this.ligneRepository = ligneRepository;
        this.absenceRepository = absenceRepository;
        this.planningJourService = planningJourService;
    }

    @Transactional
    public List<FichePaie> genererFichesDuMois() {
        LocalDate aujourdHui = LocalDate.now();
        int mois = aujourdHui.getMonthValue();
        int annee = aujourdHui.getYear();

        for (Contrat contrat : contratRepository.findByStatut(Contrat.Statut.EN_COURS)) {
            if (contrat.getDateDebut() != null && contrat.getDateDebut().isAfter(aujourdHui)) {
                continue;
            }
            if (contrat.getDateFin() != null && contrat.getDateFin().isBefore(aujourdHui)) {
                continue;
            }
            if (contrat.getEmploye() == null || contrat.getEmploye().getId() == null) {
                continue;
            }
            boolean existe = fichePaieRepository
                    .findByEmploye_IdAndContrat_IdAndMoisAndAnnee(
                            contrat.getEmploye().getId(), contrat.getId(), mois, annee)
                    .isPresent();
            if (existe) {
                continue;
            }

            BigDecimal salaire = contrat.getSalaireMensuel() == null
                    ? BigDecimal.ZERO : contrat.getSalaireMensuel();
            FichePaie fiche = new FichePaie();
            fiche.setEmploye(contrat.getEmploye());
            fiche.setContrat(contrat);
            fiche.setMois(mois);
            fiche.setAnnee(annee);
            fiche.setSalaireBase(salaire);
            fiche.setTotalPrimes(BigDecimal.ZERO);
            fiche.setTotalRetenues(BigDecimal.ZERO);
            fiche.setNetAPayer(salaire);
            fiche.setStatut(FichePaie.Statut.BROUILLON);
            fiche.setDateCreation(LocalDateTime.now());
            fichePaieRepository.save(fiche);
        }
        List<FichePaie> fiches = fichePaieRepository.findByAnneeAndMoisOrderByIdDesc(annee, mois);
        List<JourTravail> jours = planningJourService.genererEtLister(YearMonth.of(annee, mois));
        fiches.stream().filter(fiche -> fiche.getStatut() == FichePaie.Statut.BROUILLON)
                .forEach(fiche -> synchroniserLignesVariables(fiche, jours));
        return fiches;
    }

    public List<FichePaie> fichesDuMois() {
        LocalDate aujourdHui = LocalDate.now();
        return fichePaieRepository.findByAnneeAndMoisOrderByIdDesc(
                aujourdHui.getYear(), aujourdHui.getMonthValue());
    }

    @Transactional
    public FichePaie valider(Long ficheId) {
        FichePaie fiche = fichePaieRepository.findById(ficheId)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie introuvable."));
        if (fiche.getStatut() != FichePaie.Statut.BROUILLON) {
            throw new IllegalArgumentException("Seule une fiche brouillon peut être validée.");
        }
        List<JourTravail> jours = planningJourService.genererEtLister(
                YearMonth.of(fiche.getAnnee(), fiche.getMois()));
        synchroniserLignesVariables(fiche, jours);
        fiche.setStatut(FichePaie.Statut.VALIDEE);
        fiche.setDateValidation(LocalDateTime.now());
        return fichePaieRepository.save(fiche);
    }

    @Transactional
    public List<LigneFichePaie> lignes(Long ficheId) {
        return ligneRepository.findByFichePaie_IdOrderByIdAsc(ficheId);
    }

    private void synchroniserLignesVariables(FichePaie fiche, List<JourTravail> jours) {
        ligneRepository.deleteByFichePaie_IdAndSource(fiche.getId(), "HEURE_SUPPLEMENTAIRE");
        ligneRepository.deleteByFichePaie_IdAndSource(fiche.getId(), "ABSENCE");
        // Hibernate peut différer les DELETE jusqu'au commit. Il faut vider
        // la file SQL avant de réinsérer les mêmes heures supplémentaires.
        ligneRepository.flush();

        YearMonth periode = YearMonth.of(fiche.getAnnee(), fiche.getMois());
        List<HeureSupplementaire> heures = heureSupplementaireRepository
                .findByEmploye_IdAndDateTravailBetweenAndStatut(
                        fiche.getEmploye().getId(), periode.atDay(1), periode.atEndOfMonth(), "VALIDEE");

        BigDecimal totalPrimes = BigDecimal.ZERO;
        for (HeureSupplementaire heure : heures) {
            BigDecimal tauxHoraire = fiche.getSalaireBase()
                    .divide(HEURES_MENSUELLES, 10, RoundingMode.HALF_UP);
            BigDecimal montant = tauxHoraire.multiply(TAUX_MAJORATION)
                    .multiply(BigDecimal.valueOf(heure.getNombreMinutes()))
                    .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

            LigneFichePaie ligne = new LigneFichePaie();
            ligne.setFichePaie(fiche);
            ligne.setTypeLigne("PRIME");
            ligne.setLibelle("Prime heures supplémentaires du " + heure.getDateTravail());
            ligne.setMontant(montant);
            ligne.setSource("HEURE_SUPPLEMENTAIRE");
            ligne.setHeureSupplementaire(heure);
            ligne.setNombreMinutes(heure.getNombreMinutes());
            ligne.setTauxMajoration(TAUX_MAJORATION);
            ligneRepository.save(ligne);
            totalPrimes = totalPrimes.add(montant);
        }

        List<Absence> absences = absenceRepository.findAbsencesAvecRetenue(
                fiche.getEmploye().getId(), periode.atDay(1), periode.atEndOfMonth(), Absence.Statut.VALIDEE);
        List<JourTravail> joursEmploye = jours.stream()
                .filter(jour -> fiche.getEmploye().getId().equals(jour.getEmploye().getId()))
                .toList();
        long joursPlanifies = joursEmploye.size();
        BigDecimal totalRetenues = BigDecimal.ZERO;
        Set<LocalDate> datesDejaRetenues = new HashSet<>();

        for (Absence absence : absences) {
            long joursAbsents = joursEmploye.stream()
                    .filter(jour -> "ABSENCE".equals(jour.getStatut()))
                    .filter(jour -> !jour.getDateTravail().isBefore(absence.getDateDebut())
                            && !jour.getDateTravail().isAfter(absence.getDateFin()))
                    .map(JourTravail::getDateTravail)
                    .filter(datesDejaRetenues::add)
                    .count();
            if (joursAbsents == 0 || joursPlanifies == 0) continue;

            BigDecimal montant = fiche.getSalaireBase()
                    .divide(BigDecimal.valueOf(joursPlanifies), 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(joursAbsents))
                    .setScale(2, RoundingMode.HALF_UP);
            LigneFichePaie ligne = new LigneFichePaie();
            ligne.setFichePaie(fiche);
            ligne.setTypeLigne("RETENUE");
            ligne.setLibelle("Retenue pour absence du " + absence.getDateDebut()
                    + " au " + absence.getDateFin());
            ligne.setMontant(montant);
            ligne.setSource("ABSENCE");
            ligne.setAbsence(absence);
            ligne.setNombreJours((int) joursAbsents);
            ligneRepository.save(ligne);
            totalRetenues = totalRetenues.add(montant);
        }

        fiche.setTotalPrimes(totalPrimes);
        fiche.setTotalRetenues(totalRetenues);
        fiche.setNetAPayer(fiche.getSalaireBase().add(totalPrimes).subtract(totalRetenues));
        fichePaieRepository.save(fiche);
    }

    @Transactional
    public PaiementSalaire payer(Long ficheId) {
        FichePaie fiche = fichePaieRepository.findById(ficheId)
                .orElseThrow(() -> new IllegalArgumentException("Fiche de paie introuvable."));
        if (fiche.getStatut() != FichePaie.Statut.VALIDEE) {
            throw new IllegalArgumentException("La fiche doit être validée avant le paiement.");
        }
        if (paiementRepository.findByFichePaie_Id(ficheId).isPresent()) {
            throw new IllegalArgumentException("Cette fiche possède déjà un paiement.");
        }

        PaiementSalaire paiement = new PaiementSalaire();
        paiement.setFichePaie(fiche);
        paiement.setMontant(fiche.getNetAPayer());
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setModePaiement("MANUEL");
        paiement.setReference("PAY-" + LocalDate.now().toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paiement.setStatut(PaiementSalaire.Statut.EFFECTUE);
        paiement = paiementRepository.save(paiement);

        fiche.setStatut(FichePaie.Statut.PAYEE);
        fichePaieRepository.save(fiche);
        return paiement;
    }
}
