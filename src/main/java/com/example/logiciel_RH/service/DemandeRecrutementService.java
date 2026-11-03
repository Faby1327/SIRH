package com.example.logiciel_RH.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.DemandeRecrutement;
import com.example.logiciel_RH.entity.HistoriqueStatutDemande;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.entity.BudgetEntreprise;
import com.example.logiciel_RH.entity.RegleBudgetaire;
import com.example.logiciel_RH.entity.Statut;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.BudgetEntrepriseRepository;
import com.example.logiciel_RH.repository.DemandeRecrutementRepository;
import com.example.logiciel_RH.repository.HistoriqueStatutDemandeRepository;
import com.example.logiciel_RH.repository.RegleBudgetaireRepository;
import com.example.logiciel_RH.repository.StatutRepository;

@Service
public class DemandeRecrutementService {

    private final DemandeRecrutementRepository repository;
    private final StatutRepository statutRepository;
    private final PosteService posteService;
    private final HistoriqueStatutDemandeRepository historiqueRepository;
    private final RegleBudgetaireRepository regleBudgetaireRepository;
    private final BudgetEntrepriseRepository budgetEntrepriseRepository;

    public DemandeRecrutementService(DemandeRecrutementRepository repository,
                                     StatutRepository statutRepository,
                                     PosteService posteService,
                                     HistoriqueStatutDemandeRepository historiqueRepository,
                                     RegleBudgetaireRepository regleBudgetaireRepository,
                                     BudgetEntrepriseRepository budgetEntrepriseRepository) {
        this.repository = repository;
        this.statutRepository = statutRepository;
        this.posteService = posteService;
        this.historiqueRepository = historiqueRepository;
        this.regleBudgetaireRepository = regleBudgetaireRepository;
        this.budgetEntrepriseRepository = budgetEntrepriseRepository;
    }

    public List<DemandeRecrutement> findByManager(Utilisateur manager) {
        return repository.findByManagerOrderByDateDemandeDesc(manager);
    }

    public Optional<DemandeRecrutement> findByIdForManager(Long idDemande, Utilisateur manager) {
        return repository.findByIdDemandeAndManager(idDemande, manager);
    }

    public Optional<DemandeRecrutement> findById(Long idDemande) {
        return repository.findById(idDemande);
    }

    public List<DemandeRecrutement> findAllOrdered() {
        return repository.findAllByOrderByDateDemandeDesc();
    }

    public List<DemandeRecrutement> findByStatuts(List<String> statuts) {
        return repository.findByStatutActuel_LibelleInOrderByDateDemandeDesc(statuts);
    }

    public List<HistoriqueStatutDemande> findHistorique(Long idDemande) {
        return historiqueRepository.findByDemande_IdDemandeOrderByDateChangementAscIdHistoriqueAsc(idDemande);
    }

    /** Retourne le résultat de traitement propre à un rôle, indépendamment du statut global actuel. */
    public String getStatutParRole(DemandeRecrutement demande, String typeValidation) {
        List<HistoriqueStatutDemande> historique = findHistorique(demande.getIdDemande());
        HistoriqueStatutDemande derniereActionRole = null;
        int etapeRefus = 0;
        for (int i = historique.size() - 1; i >= 0; i--) {
            HistoriqueStatutDemande action = historique.get(i);
            String statutAction = action.getStatut().getLibelle();
            if (derniereActionRole == null && typeValidation.equals(action.getTypeValidation())) {
                derniereActionRole = action;
            }
            if (etapeRefus == 0 && List.of("REFUSEE", "BUDGET_INSUFFISANT").contains(statutAction)) {
                etapeRefus = etapePourRole(action.getTypeValidation());
            }
            if (derniereActionRole != null && etapeRefus != 0) {
                break;
            }
        }
        if (derniereActionRole != null) {
            String statutAction = derniereActionRole.getStatut().getLibelle();
            return List.of("REFUSEE", "BUDGET_INSUFFISANT").contains(statutAction) ? "REFUSEE" : "VALIDEE";
        }

        String statutGlobal = demande.getStatutActuel().getLibelle();
        if ("REFUSEE".equals(statutGlobal) || "BUDGET_INSUFFISANT".equals(statutGlobal)) {
            int etapeRole = etapePourRole(typeValidation);
            if (etapeRefus > etapeRole) return "VALIDEE";
            if (etapeRefus == etapeRole && etapeRefus != 0) return "REFUSEE";
            return "EN_ATTENTE";
        }
        boolean roleDejaValide = switch (typeValidation) {
            case "RH" -> List.of("EN_VERIFICATION_FINANCIERE", "EN_ATTENTE_VALIDATION", "VALIDEE").contains(statutGlobal);
            case "FINANCIERE" -> List.of("EN_ATTENTE_VALIDATION", "VALIDEE").contains(statutGlobal);
            case "FINALE" -> "VALIDEE".equals(statutGlobal);
            default -> false;
        };
        if (roleDejaValide) {
            return "VALIDEE";
        }
        return "EN_ATTENTE";
    }

    private int etapePourRole(String typeValidation) {
        return switch (typeValidation) {
            case "MANAGER" -> 1;
            case "RH" -> 2;
            case "FINANCIERE" -> 3;
            case "FINALE" -> 4;
            default -> 0;
        };
    }

    /** Étape du circuit où la demande a été refusée, pour colorer le bon segment d'avancement. */
    public int getEtapeRefus(DemandeRecrutement demande) {
        List<HistoriqueStatutDemande> historique = findHistorique(demande.getIdDemande());
        for (int i = historique.size() - 1; i >= 0; i--) {
            HistoriqueStatutDemande action = historique.get(i);
            String statut = action.getStatut().getLibelle();
            if (List.of("REFUSEE", "BUDGET_INSUFFISANT").contains(statut)) {
                int etape = etapePourRole(action.getTypeValidation());
                return etape == 0 ? 4 : etape;
            }
        }
        return 4;
    }

    /**
     * Cree une demande de recrutement pour un manager.
     * Par défaut, le manager soumet directement sa demande.
     */
    @Transactional
    public DemandeRecrutement creerDemande(Utilisateur manager,
                                           Long idPoste,
                                           Integer nombrePersonnes,
                                           BigDecimal budgetDemande,
                                           String motif,
                                           LocalDate dateSouhaitee) {
        return creerDemande(manager, idPoste, nombrePersonnes, budgetDemande, motif, dateSouhaitee, "CREATION");
    }

    @Transactional
    public DemandeRecrutement creerDemande(Utilisateur manager,
                                           Long idPoste,
                                           Integer nombrePersonnes,
                                           BigDecimal budgetDemande,
                                           String motif,
                                           LocalDate dateSouhaitee,
                                           String typeRecrutement) {

        return creerDemande(manager, idPoste, nombrePersonnes, budgetDemande, motif, dateSouhaitee,
                typeRecrutement, "SOUMISE");
    }

    @Transactional
    public DemandeRecrutement creerDemande(Utilisateur manager,
                                           Long idPoste,
                                           Integer nombrePersonnes,
                                           BigDecimal budgetDemande,
                                           String motif,
                                           LocalDate dateSouhaitee,
                                           String typeRecrutement,
                                           String statutInitial) {

        Poste poste = posteService.getById(idPoste);
        if (poste == null || poste.getDepartement() == null || manager.getDepartement() == null
                || !Objects.equals(poste.getDepartement().getIdDepartement(), manager.getDepartement().getIdDepartement())) {
            throw new IllegalArgumentException("Le poste doit appartenir au département du manager.");
        }
        if (!"SOUMISE".equals(statutInitial) && !"BROUILLON".equals(statutInitial)) {
            throw new IllegalArgumentException("Statut initial invalide: " + statutInitial);
        }
        Statut statut = getStatutObligatoire(statutInitial);
        if (!"CREATION".equals(typeRecrutement) && !"REMPLACEMENT".equals(typeRecrutement)) {
            throw new IllegalArgumentException("Type de recrutement invalide: " + typeRecrutement);
        }

        DemandeRecrutement demande = new DemandeRecrutement();
        demande.setPoste(poste);
        demande.setDepartement(manager.getDepartement());
        demande.setManager(manager);
        demande.setNombrePersonnes(nombrePersonnes);
        demande.setBudgetDemande(budgetDemande);
        demande.setSalaireMensuelEstime(budgetDemande);
        demande.setMotif(motif);
        demande.setDateDemande(LocalDate.now());
        demande.setTypeRecrutement(typeRecrutement);
        demande.setDateSouhaitee(dateSouhaitee);
        demande.setStatutActuel(statut);
        demande.setDateModification(LocalDateTime.now());

        DemandeRecrutement sauvegardee = repository.save(demande);
        String remarque = "SOUMISE".equals(statutInitial)
                ? "Demande créée et soumise par le manager."
                : "Demande créée comme brouillon par le manager.";
        creerHistorique(sauvegardee, statut, manager, remarque);
        return sauvegardee;
    }

    @Transactional
    public boolean supprimerParManager(Long idDemande, Utilisateur manager) {
        Optional<DemandeRecrutement> optionalDemande = repository.findByIdDemandeAndManager(idDemande, manager);

        if (optionalDemande.isEmpty()) {
            return false;
        }

        DemandeRecrutement demande = optionalDemande.get();
        if (!isSupprimableParManager(demande)) {
            return false;
        }

        historiqueRepository.deleteAllByDemande_IdDemande(demande.getIdDemande());
        repository.delete(demande);
        return true;
    }

    public boolean isSupprimableParManager(DemandeRecrutement demande) {
        if (demande.getStatutActuel() == null || demande.getStatutActuel().getLibelle() == null) {
            return false;
        }

        String statut = demande.getStatutActuel().getLibelle();
        return "REFUSEE".equals(statut) || "BUDGET_INSUFFISANT".equals(statut);
    }

    @Transactional
    public DemandeRecrutement changerStatut(DemandeRecrutement demande,
                                            String nouveauStatut,
                                            Utilisateur utilisateur,
                                            String remarque) {
        Statut statut = getStatutObligatoire(nouveauStatut);
        demande.setStatutActuel(statut);
        demande.setDateModification(LocalDateTime.now());

        DemandeRecrutement sauvegardee = repository.save(demande);
        creerHistorique(sauvegardee, statut, utilisateur, remarque);
        return sauvegardee;
    }

    public boolean estBudgetCompatible(DemandeRecrutement demande) {
        return estBudgetDansFourchette(demande) && estBudgetEntrepriseCompatible(demande);
    }

    public boolean estBudgetDansFourchette(DemandeRecrutement demande) {
        if (demande.getPoste() == null || demande.getPoste().getIdPoste() == null || demande.getBudgetDemande() == null) {
            return false;
        }

        Optional<RegleBudgetaire> optionalRegle = regleBudgetaireRepository.findByPoste_IdPoste(demande.getPoste().getIdPoste());
        if (optionalRegle.isEmpty()) {
            return false;
        }

        RegleBudgetaire regle = optionalRegle.get();
        return demande.getBudgetDemande().compareTo(regle.getBudgetMin()) >= 0
                && demande.getBudgetDemande().compareTo(regle.getBudgetMax()) <= 0;
    }

    public boolean estBudgetEntrepriseCompatible(DemandeRecrutement demande) {
        if (demande == null || demande.getDepartement() == null || demande.getDepartement().getIdDepartement() == null
                || demande.getDateDemande() == null || demande.getBudgetTotalEstime() == null) {
            return false;
        }
        Optional<BudgetEntreprise> optionalBudget = budgetEntrepriseRepository
                .findByDepartement_IdDepartementAndAnnee(
                        demande.getDepartement().getIdDepartement(),
                        demande.getDateDemande().getYear());
        if (optionalBudget.isEmpty()) {
            return false;
        }
        return demande.getBudgetTotalEstime().compareTo(optionalBudget.get().getMontantDisponible()) <= 0;
    }

    public BigDecimal getBudgetEntrepriseDisponible(DemandeRecrutement demande) {
        if (demande == null || demande.getDepartement() == null || demande.getDepartement().getIdDepartement() == null
                || demande.getDateDemande() == null) {
            return BigDecimal.ZERO;
        }
        return budgetEntrepriseRepository
                .findByDepartement_IdDepartementAndAnnee(
                        demande.getDepartement().getIdDepartement(),
                        demande.getDateDemande().getYear())
                .map(BudgetEntreprise::getMontantDisponible)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getBudgetMin(Long idPoste) {
        return regleBudgetaireRepository.findByPoste_IdPoste(idPoste)
                .map(RegleBudgetaire::getBudgetMin)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getBudgetMax(Long idPoste) {
        return regleBudgetaireRepository.findByPoste_IdPoste(idPoste)
                .map(RegleBudgetaire::getBudgetMax)
                .orElse(BigDecimal.ZERO);
    }

    private Statut getStatutObligatoire(String libelle) {
        return statutRepository.findByLibelle(libelle)
                .orElseThrow(() -> new IllegalStateException("Statut introuvable: " + libelle));
    }

    private void creerHistorique(DemandeRecrutement demande,
                                 Statut statut,
                                 Utilisateur utilisateur,
                                 String remarque) {
        HistoriqueStatutDemande historique = new HistoriqueStatutDemande();
        historique.setDemande(demande);
        historique.setStatut(statut);
        historique.setUtilisateur(utilisateur);
        historique.setTypeValidation(typeValidationPour(utilisateur));
        historique.setDateChangement(LocalDateTime.now());
        historique.setRemarque(Objects.requireNonNullElse(remarque, ""));

        historiqueRepository.save(historique);
    }

    private String typeValidationPour(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getRole() == null) {
            throw new IllegalArgumentException("Le validateur doit avoir un rôle");
        }

        return switch (utilisateur.getRole()) {
            case "MANAGER" -> "MANAGER";
            case "RH" -> "RH";
            case "FINANCIER" -> "FINANCIERE";
            case "DIRECTEUR" -> "FINALE";
            default -> throw new IllegalArgumentException(
                    "Ce rôle ne peut pas créer l'historique d'une demande: " + utilisateur.getRole());
        };
    }
}
