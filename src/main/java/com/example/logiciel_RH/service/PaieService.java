package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.FichePaie;
import com.example.logiciel_RH.entity.PaiementSalaire;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.FichePaieRepository;
import com.example.logiciel_RH.repository.PaiementSalaireRepository;

@Service
public class PaieService {

    private final ContratRepository contratRepository;
    private final FichePaieRepository fichePaieRepository;
    private final PaiementSalaireRepository paiementRepository;

    public PaieService(ContratRepository contratRepository,
                       FichePaieRepository fichePaieRepository,
                       PaiementSalaireRepository paiementRepository) {
        this.contratRepository = contratRepository;
        this.fichePaieRepository = fichePaieRepository;
        this.paiementRepository = paiementRepository;
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
        return fichePaieRepository.findByAnneeAndMoisOrderByIdDesc(annee, mois);
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
        fiche.setStatut(FichePaie.Statut.VALIDEE);
        fiche.setDateValidation(LocalDateTime.now());
        return fichePaieRepository.save(fiche);
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
