package com.example.logiciel_RH.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Candidature;
import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.HistoriqueProposition;
import com.example.logiciel_RH.entity.OffreEmploi;
import com.example.logiciel_RH.entity.PropositionEmbauche;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.CandidatureRepository;
import com.example.logiciel_RH.repository.OffreEmploiRepository;
import com.example.logiciel_RH.repository.PropositionEmbaucheRepository;
import com.example.logiciel_RH.repository.HistoriquePropositionRepository;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.EmployeRepository;

@Service
public class PropositionEmbaucheService {

    private final PropositionEmbaucheRepository propositionRepository;
    private final CandidatureRepository candidatureRepository;
    private final OffreEmploiRepository offreRepository;
    private final HistoriquePropositionRepository historiqueRepository;
    private final EmployeRepository employeRepository;
    private final ContratRepository contratRepository;

    public PropositionEmbaucheService(PropositionEmbaucheRepository propositionRepository,
                                      CandidatureRepository candidatureRepository,
                                      OffreEmploiRepository offreRepository,
                                      HistoriquePropositionRepository historiqueRepository,
                                      EmployeRepository employeRepository,
                                      ContratRepository contratRepository) {
        this.propositionRepository = propositionRepository;
        this.candidatureRepository = candidatureRepository;
        this.offreRepository = offreRepository;
        this.historiqueRepository = historiqueRepository;
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
    }

    public List<Candidature> candidatsEligibles(Long idOffre) {
        if (idOffre == null) {
            return List.of();
        }
        List<Candidature> candidatures = candidatureRepository.findByOffre_IdOffreOrderByDateCandidatureDesc(idOffre);
        List<PropositionEmbauche> existantes = propositionRepository.findByOffre(idOffre);
        Set<Long> dejaProposees = new HashSet<>();
        for (PropositionEmbauche p : existantes) {
            if (p.getCandidature() != null) {
                dejaProposees.add(p.getCandidature().getIdCandidature());
            }
        }
        List<Candidature> resultat = new ArrayList<>();
        for (Candidature c : candidatures) {
            if (c.getIdCandidature() == null) {
                continue;
            }
            if (dejaProposees.contains(c.getIdCandidature())) {
                continue;
            }
            if (!Candidature.STATUT_EVALUEE.equals(c.getStatut())) {
                continue;
            }
            if (Candidature.STATUT_REFUSEE.equals(c.getStatut())) {
                continue;
            }
            if (Candidature.STATUT_RETENUE.equals(c.getStatut())) {
                continue;
            }
            resultat.add(c);
        }
        return resultat;
    }

    public int placesDemandees(Long idOffre) {
        OffreEmploi offre = offreRepository.findById(idOffre).orElse(null);
        if (offre == null || offre.getDemande() == null) {
            return 0;
        }
        return offre.getDemande().getNombrePersonnes() == null ? 0 : offre.getDemande().getNombrePersonnes();
    }

    public long placesPrises(Long idOffre) {
        if (idOffre == null) {
            return 0;
        }
        return propositionRepository.countByOffreIdAndStatutIn(idOffre,
                List.of(PropositionEmbauche.STATUT_PROPOSEE,
                        PropositionEmbauche.STATUT_DEMANDE_SALAIRE,
                        PropositionEmbauche.STATUT_DEMANDE_CONTRAT,
                        PropositionEmbauche.STATUT_DEMANDE_CONDITIONS,
                        PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION,
                        PropositionEmbauche.STATUT_CONFIRMEE));
    }

    public boolean peutProposer(Long idOffre) {
        int demandees = placesDemandees(idOffre);
        if (demandees <= 0) {
            return true;
        }
        return placesPrises(idOffre) < demandees;
    }

    @Transactional
    public PropositionEmbauche proposer(Long idOffre, Long idCandidature, Long idProposePar, String commentaireRh) {
        if (!peutProposer(idOffre)) {
            throw new IllegalArgumentException("Nombre de propositions atteint pour cette offre.");
        }
        if (propositionRepository.existsByCandidature_IdCandidature(idCandidature)) {
            throw new IllegalArgumentException("Une proposition existe déjà pour ce candidat.");
        }
        Candidature candidature = candidatureRepository.findById(idCandidature)
                .orElseThrow(() -> new IllegalArgumentException("Candidature introuvable"));
        if (!Candidature.STATUT_EVALUEE.equals(candidature.getStatut())) {
            throw new IllegalArgumentException("Seul un candidat déjà évalué peut recevoir une proposition.");
        }
        if (candidature.getOffre() == null || !idOffre.equals(candidature.getOffre().getIdOffre())) {
            throw new IllegalArgumentException("Candidature ne correspond pas à cette offre.");
        }

        OffreEmploi offre = offreRepository.findById(idOffre)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable"));
        Utilisateur proposePar = new Utilisateur();
        proposePar.setIdUtilisateur(idProposePar);

        PropositionEmbauche proposition = new PropositionEmbauche();
        proposition.setCandidature(candidature);
        proposition.setOffre(offre);
        proposition.setProposePar(proposePar);
        proposition.setStatut(PropositionEmbauche.STATUT_PROPOSEE);
        proposition.setTypeContratPropose("CE");
        proposition.setSalairePropose(offre.getDemande() == null
                ? null : offre.getDemande().getSalaireMensuelEstime());
        proposition.setCommentaireRh(commentaireRh == null || commentaireRh.isBlank() ? null : commentaireRh.trim());
        proposition = propositionRepository.save(proposition);
        enregistrerHistorique(proposition, null, proposePar, commentaireRh);

        candidature.setStatut(Candidature.STATUT_RETENUE);
        candidatureRepository.save(candidature);

        return propositionRepository.findByIdAvecRelations(proposition.getId()).orElse(proposition);
    }

    public List<PropositionEmbauche> propositionsPourOffre(Long idOffre) {
        if (idOffre == null) {
            return List.of();
        }
        return propositionRepository.findByOffre(idOffre);
    }

    public Optional<PropositionEmbauche> propositionActivePourCandidat(Long idCandidat) {
        if (idCandidat == null) {
            return Optional.empty();
        }
        return propositionRepository.findActivePourCandidat(idCandidat);
    }

    @Transactional
    public void repondreCandidat(Long propositionId, Long idCandidat, BigDecimal salaireDemande,
                                 String typeContratDemande, String commentaire, boolean accepter) {
        PropositionEmbauche proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable."));
        if (proposition.getCandidature() == null
                || proposition.getCandidature().getCandidat() == null
                || !idCandidat.equals(proposition.getCandidature().getCandidat().getIdCandidat())) {
            throw new IllegalArgumentException("Cette proposition ne correspond pas à votre profil.");
        }
        if (!PropositionEmbauche.STATUT_PROPOSEE.equals(proposition.getStatut())) {
            throw new IllegalArgumentException("Cette proposition a déjà été traitée.");
        }
        String ancienStatut = proposition.getStatut();
        if (accepter) {
            proposition.setStatut(PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION);
            proposition.setDateAcceptationCandidat(LocalDateTime.now());
        } else if (salaireDemande != null || (typeContratDemande != null && !typeContratDemande.isBlank())) {
            if (salaireDemande != null && salaireDemande.signum() < 0) {
                throw new IllegalArgumentException("Le salaire demandé doit être positif.");
            }
            if (salaireDemande != null) {
                proposition.setSalaireDemandeCandidat(salaireDemande);
            }
            String typeDemande = typeContratDemande == null ? null : typeContratDemande.trim().toUpperCase(Locale.ROOT);
            if (typeDemande != null && !typeDemande.isBlank()
                    && !"CDD".equals(typeDemande) && !"CDI".equals(typeDemande)) {
                throw new IllegalArgumentException("Le type de contrat demandé doit être CDD ou CDI.");
            }
            proposition.setTypeContratDemandeCandidat(typeDemande == null || typeDemande.isBlank() ? null : typeDemande);
            proposition.setStatut(salaireDemande != null && proposition.getTypeContratDemandeCandidat() != null
                    ? PropositionEmbauche.STATUT_DEMANDE_CONDITIONS
                    : salaireDemande != null
                    ? PropositionEmbauche.STATUT_DEMANDE_SALAIRE
                    : PropositionEmbauche.STATUT_DEMANDE_CONTRAT);
        } else {
            proposition.setStatut(PropositionEmbauche.STATUT_REFUSEE);
        }
        proposition.setCommentaireCandidat(commentaire == null || commentaire.isBlank() ? null : commentaire.trim());
        proposition.setDateReponse(LocalDateTime.now());
        propositionRepository.save(proposition);
        enregistrerHistorique(proposition, ancienStatut,
                proposition.getCandidature().getCandidat().getUtilisateur(), commentaire);
        if (accepter) {
            mettreEnAttenteConfirmation(proposition.getOffre());
        }
    }

    @Transactional
    public void traiterDemandeConditions(Long propositionId, Long idRh, boolean accepter, String commentaire) {
        PropositionEmbauche proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable."));
        if (!PropositionEmbauche.STATUT_DEMANDE_SALAIRE.equals(proposition.getStatut())
                && !PropositionEmbauche.STATUT_DEMANDE_CONTRAT.equals(proposition.getStatut())
                && !PropositionEmbauche.STATUT_DEMANDE_CONDITIONS.equals(proposition.getStatut())) {
            throw new IllegalArgumentException("Aucune demande de conditions contractuelles n'est en attente.");
        }
        String ancienStatut = proposition.getStatut();
        if (accepter) {
            if (proposition.getSalaireDemandeCandidat() != null) {
                proposition.setSalairePropose(proposition.getSalaireDemandeCandidat());
            }
            if (proposition.getTypeContratDemandeCandidat() != null) {
                proposition.setTypeContratPropose(proposition.getTypeContratDemandeCandidat());
            }
            proposition.setStatut(PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION);
        } else {
            proposition.setStatut(PropositionEmbauche.STATUT_REFUSEE);
        }
        proposition.setCommentaireRh(commentaire == null || commentaire.isBlank()
                ? proposition.getCommentaireRh() : commentaire.trim());
        proposition.setDateReponse(LocalDateTime.now());
        proposition.setDateDecisionRh(LocalDateTime.now());
        proposition.setCommentaireDecisionRh(commentaire);
        propositionRepository.save(proposition);
        Utilisateur rh = new Utilisateur();
        rh.setIdUtilisateur(idRh);
        enregistrerHistorique(proposition, ancienStatut, rh, commentaire);
        if (accepter) {
            mettreEnAttenteConfirmation(proposition.getOffre());
        }
    }

    @Transactional
    public void confirmerEmbauche(Long propositionId, Long idRh, String commentaire) {
        PropositionEmbauche proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable."));
        if (!PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION.equals(proposition.getStatut())) {
            throw new IllegalArgumentException("Cette proposition n'est pas en attente de confirmation.");
        }
        String ancienStatut = proposition.getStatut();
        proposition.setStatut(PropositionEmbauche.STATUT_CONFIRMEE);
        proposition.setSalaireFinal(proposition.getSalairePropose());
        proposition.setDateDecisionRh(LocalDateTime.now());
        if (commentaire != null && !commentaire.isBlank()) {
            proposition.setCommentaireRh(commentaire.trim());
        }
        proposition.setDateReponse(LocalDateTime.now());
        proposition.setCommentaireDecisionRh(commentaire);
        propositionRepository.save(proposition);
        Utilisateur rh = new Utilisateur();
        rh.setIdUtilisateur(idRh);
        enregistrerHistorique(proposition, ancienStatut, rh, commentaire);
        finaliserStatutOffre(proposition.getOffre());
    }

    public Optional<PropositionEmbauche> propositionPourOffre(Long idOffre, Long propositionId) {
        return propositionsPourOffre(idOffre).stream()
                .filter(proposition -> propositionId != null && propositionId.equals(proposition.getId()))
                .findFirst();
    }

    @Transactional
    public Contrat creerEmployeEtContrat(Long propositionId) {
        PropositionEmbauche proposition = propositionRepository.findById(propositionId)
                .orElseThrow(() -> new IllegalArgumentException("Proposition introuvable."));
        if (!PropositionEmbauche.STATUT_CONFIRMEE.equals(proposition.getStatut())) {
            throw new IllegalArgumentException("La proposition doit être confirmée avant de créer l'employé.");
        }
        if (proposition.getCandidature() == null || proposition.getCandidature().getCandidat() == null
                || proposition.getCandidature().getCandidat().getPersonne() == null) {
            throw new IllegalArgumentException("Le profil personne du candidat est introuvable.");
        }
        LocalDate aujourdHui = LocalDate.now();
        Contrat.Type type = Contrat.Type.valueOf(
                proposition.getTypeContratPropose() == null ? "CE" : proposition.getTypeContratPropose());
        BigDecimal salaireMensuel = proposition.getSalaireFinal() == null
                ? proposition.getSalairePropose() : proposition.getSalaireFinal();
        if (salaireMensuel == null || salaireMensuel.signum() < 0) {
            throw new IllegalArgumentException("Le salaire final de la proposition est introuvable.");
        }
        if (proposition.getOffre() == null || proposition.getOffre().getPoste() == null) {
            throw new IllegalArgumentException("Le poste de la proposition est introuvable.");
        }

        Employe employe = employeRepository
                .findByPersonne_IdPersonne(proposition.getCandidature().getCandidat().getPersonne().getIdPersonne())
                .orElse(null);
        if (employe == null) {
            employe = new Employe();
            employe.setPersonne(proposition.getCandidature().getCandidat().getPersonne());
            Long numero = employeRepository.prochainNumeroMatricule();
            employe.setMatricule(String.format(Locale.ROOT, "EMP-%05d", numero));
            employe.setDateEmbauche(aujourdHui);
            employe.setDateCreation(LocalDateTime.now());
        }
        employe.setDateFin(null);
        employe.setStatut(Employe.Statut.ACTIF);
        employe = employeRepository.save(employe);

        for (Contrat contratEnCours : contratRepository
                .findByEmploye_IdAndStatut(employe.getId(), Contrat.Statut.EN_COURS)) {
            contratEnCours.setStatut(Contrat.Statut.TERMINE);
            if (contratEnCours.getDateDebut() != null && aujourdHui.isAfter(contratEnCours.getDateDebut())) {
                contratEnCours.setDateFin(aujourdHui.minusDays(1));
            }
            contratRepository.save(contratEnCours);
        }

        Contrat contrat = new Contrat();
        contrat.setEmploye(employe);
        contrat.setProposition(proposition);
        contrat.setPoste(proposition.getOffre().getPoste());
        contrat.setTypeContrat(type);
        contrat.setDateDebut(aujourdHui);
        contrat.setDateFin(null);
        contrat.setSalaireMensuel(salaireMensuel);
        contrat.setDevise("Ar");
        contrat.setStatut(Contrat.Statut.EN_COURS);
        return contratRepository.save(contrat);
    }

    private void enregistrerHistorique(PropositionEmbauche proposition,
                                       String ancienStatut,
                                       Utilisateur acteur,
                                       String commentaire) {
        HistoriqueProposition historique = new HistoriqueProposition();
        historique.setProposition(proposition);
        historique.setActeur(acteur);
        historique.setAncienStatut(ancienStatut);
        historique.setNouveauStatut(proposition.getStatut());
        historique.setSalaireDemande(proposition.getSalaireDemandeCandidat());
        historique.setSalaireDecide(proposition.getSalaireFinal() == null
                ? proposition.getSalairePropose() : proposition.getSalaireFinal());
        historique.setTypeContratDemande(proposition.getTypeContratDemandeCandidat());
        historique.setTypeContratDecide(proposition.getTypeContratPropose());
        historique.setCommentaire(commentaire == null || commentaire.isBlank() ? null : commentaire.trim());
        historique.setDateChangement(LocalDateTime.now());
        historiqueRepository.save(historique);
    }

    private void mettreEnAttenteConfirmation(OffreEmploi offre) {
        if (offre == null || offre.getDemande() == null
                || offre.getDemande().getNombrePersonnes() == null) {
            return;
        }
        offre.setStatut(OffreEmploi.STATUT_EN_ATTENTE_CONFIRMATION);
        offreRepository.save(offre);
    }

    private void finaliserStatutOffre(OffreEmploi offre) {
        if (offre == null || offre.getDemande() == null
                || offre.getDemande().getNombrePersonnes() == null) {
            return;
        }
        int placesDemandees = offre.getDemande().getNombrePersonnes();
        long placesConfirmees = propositionRepository.countByOffreIdAndStatutIn(
                offre.getIdOffre(), List.of(PropositionEmbauche.STATUT_CONFIRMEE));
        if (placesConfirmees >= placesDemandees) {
            offre.setStatut(OffreEmploi.STATUT_CLOTUREE);
        } else if (propositionRepository.countByOffreIdAndStatutIn(
                offre.getIdOffre(), List.of(PropositionEmbauche.STATUT_EN_ATTENTE_CONFIRMATION)) > 0) {
            offre.setStatut(OffreEmploi.STATUT_EN_ATTENTE_CONFIRMATION);
        } else {
            offre.setStatut(OffreEmploi.STATUT_PUBLIEE);
        }
        offreRepository.save(offre);
    }
}
