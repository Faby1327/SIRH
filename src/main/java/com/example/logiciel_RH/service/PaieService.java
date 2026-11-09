package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.FichePaie;
import com.example.logiciel_RH.entity.HeureSupplementaire;
import com.example.logiciel_RH.entity.LigneFichePaie;
import com.example.logiciel_RH.entity.PaiementSalaire;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.FichePaieRepository;
import com.example.logiciel_RH.repository.PaiementSalaireRepository;
import com.example.logiciel_RH.repository.HeureSupplementaireRepository;
import com.example.logiciel_RH.repository.LigneFichePaieRepository;

@Service
public class PaieService {

    private static final BigDecimal HEURES_MENSUELLES = new BigDecimal("173.33");
    private static final BigDecimal TAUX_MAJORATION = new BigDecimal("1.30");

    private final ContratRepository contratRepository;
    private final FichePaieRepository fichePaieRepository;
    private final PaiementSalaireRepository paiementRepository;
    private final HeureSupplementaireRepository heureSupplementaireRepository;
    private final LigneFichePaieRepository ligneRepository;

    public PaieService(ContratRepository contratRepository,
                       FichePaieRepository fichePaieRepository,
                       PaiementSalaireRepository paiementRepository,
                       HeureSupplementaireRepository heureSupplementaireRepository,
                       LigneFichePaieRepository ligneRepository) {
        this.contratRepository = contratRepository;
        this.fichePaieRepository = fichePaieRepository;
        this.paiementRepository = paiementRepository;
        this.heureSupplementaireRepository = heureSupplementaireRepository;
        this.ligneRepository = ligneRepository;
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
        fiches.stream().filter(fiche -> fiche.getStatut() == FichePaie.Statut.BROUILLON)
                .forEach(this::synchroniserPrimesHeuresSupplementaires);
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
        synchroniserPrimesHeuresSupplementaires(fiche);
        fiche.setStatut(FichePaie.Statut.VALIDEE);
        fiche.setDateValidation(LocalDateTime.now());
        return fichePaieRepository.save(fiche);
    }

    @Transactional
    public List<LigneFichePaie> lignes(Long ficheId) {
        return ligneRepository.findByFichePaie_IdOrderByIdAsc(ficheId);
    }

    private void synchroniserPrimesHeuresSupplementaires(FichePaie fiche) {
        ligneRepository.deleteByFichePaie_IdAndSource(fiche.getId(), "HEURE_SUPPLEMENTAIRE");
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

        fiche.setTotalPrimes(totalPrimes);
        BigDecimal retenues = fiche.getTotalRetenues() == null ? BigDecimal.ZERO : fiche.getTotalRetenues();
        fiche.setNetAPayer(fiche.getSalaireBase().add(totalPrimes).subtract(retenues));
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
