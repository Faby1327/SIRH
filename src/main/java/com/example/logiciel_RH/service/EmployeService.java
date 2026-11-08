package com.example.logiciel_RH.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.logiciel_RH.dto.EmployeForm;
import com.example.logiciel_RH.entity.Departement;
import com.example.logiciel_RH.entity.DocumentEmploye;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.Personne;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.entity.TypeDocument;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.DepartementRepository;
import com.example.logiciel_RH.repository.DocumentEmployeRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.PersonneRepository;
import com.example.logiciel_RH.repository.PosteRepository;
import com.example.logiciel_RH.repository.TypeDocumentRepository;
import com.example.logiciel_RH.repository.UtilisateurRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

/** Dossier des employés (bloc 5) : création, modification, recherche, documents. */
@Service
public class EmployeService {

    private static final Set<String> SEXES = Set.of("M", "F");
    private static final Set<String> SITUATIONS =
            Set.of("CELIBATAIRE", "MARIE", "DIVORCE", "VEUF", "UNION_LIBRE");

    private final EmployeRepository employeRepository;
    private final PersonneRepository personneRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final DocumentEmployeRepository documentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final StockageFichierService stockage;

    public EmployeService(EmployeRepository employeRepository,
                          PersonneRepository personneRepository,
                          DepartementRepository departementRepository,
                          PosteRepository posteRepository,
                          TypeDocumentRepository typeDocumentRepository,
                          DocumentEmployeRepository documentRepository,
                          UtilisateurRepository utilisateurRepository,
                          PasswordEncoder passwordEncoder,
                          StockageFichierService stockage) {
        this.employeRepository = employeRepository;
        this.personneRepository = personneRepository;
        this.departementRepository = departementRepository;
        this.posteRepository = posteRepository;
        this.typeDocumentRepository = typeDocumentRepository;
        this.documentRepository = documentRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.stockage = stockage;
    }

    // ------------------------------------------------------------------ lecture

    /** Recherche multicritère ; chaque critère vide est ignoré. */
    @Transactional(readOnly = true)
    public List<Employe> rechercher(String q, Long departementId, String statut) {
        String terme = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        return employeRepository.findAllByOrderByDateCreationDesc().stream()
                .filter(e -> terme.isEmpty() || contient(e, terme))
                .filter(e -> departementId == null
                        || (e.getDepartement() != null
                            && departementId.equals(e.getDepartement().getIdDepartement())))
                .filter(e -> statut == null || statut.isBlank() || e.getStatut().name().equals(statut))
                .toList();
    }

    private boolean contient(Employe e, String terme) {
        Personne p = e.getPersonne();
        String texte = (p.getNomComplet() + " " + p.getNom() + " " + e.getMatricule() + " "
                + nullable(p.getEmail()) + " " + nullable(p.getTelephone()) + " "
                + nullable(e.getCinNumero())).toLowerCase(Locale.ROOT);
        return texte.contains(terme);
    }

    /**
     * Règles d'accès en lecture : RH et directeur voient tout, un manager voit
     * son département, chacun voit son propre dossier.
     */
    @Transactional(readOnly = true)
    public boolean peutConsulter(Utilisateur utilisateur, Employe employe) {
        if (utilisateur == null || employe == null) return false;
        String role = utilisateur.getRole();
        if ("RH".equals(role) || "DIRECTEUR".equals(role)) return true;
        if (estLeSien(utilisateur, employe)) return true;
        return "MANAGER".equals(role)
                && utilisateur.getDepartement() != null
                && employe.getDepartement() != null
                && utilisateur.getDepartement().getIdDepartement()
                        .equals(employe.getDepartement().getIdDepartement());
    }

    public boolean estLeSien(Utilisateur utilisateur, Employe employe) {
        return utilisateur != null && utilisateur.getPersonne() != null
                && employe.getPersonne().getIdPersonne().equals(utilisateur.getPersonne().getIdPersonne());
    }

    @Transactional(readOnly = true)
    public Employe pourUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getPersonne() == null) return null;
        return employeRepository.findByPersonne_IdPersonne(utilisateur.getPersonne().getIdPersonne()).orElse(null);
    }

    @Transactional(readOnly = true)
    public EmployeForm versFormulaire(Employe e) {
        EmployeForm f = new EmployeForm();
        Personne p = e.getPersonne();
        f.setNom(p.getNom());
        f.setPrenom(p.getPrenom());
        f.setDateNaissance(p.getDateNaissance());
        f.setTelephone(p.getTelephone());
        f.setEmailPersonnel(p.getEmail());
        f.setLieuNaissance(e.getLieuNaissance());
        f.setSexe(e.getSexe());
        f.setNationalite(e.getNationalite());
        f.setCinNumero(e.getCinNumero());
        f.setCinDateDelivrance(e.getCinDateDelivrance());
        f.setCinLieuDelivrance(e.getCinLieuDelivrance());
        f.setAdresse(e.getAdresse());
        f.setCommune(e.getCommune());
        f.setRegion(e.getRegion());
        f.setSituationMatrimoniale(e.getSituationMatrimoniale());
        f.setNombreEnfants(e.getNombreEnfants());
        f.setContactUrgenceNom(e.getContactUrgenceNom());
        f.setContactUrgenceRelation(e.getContactUrgenceRelation());
        f.setContactUrgenceTelephone(e.getContactUrgenceTelephone());
        f.setDepartementId(e.getDepartement() == null ? null : e.getDepartement().getIdDepartement());
        f.setPosteId(e.getPoste() == null ? null : e.getPoste().getIdPoste());
        f.setSuperieurId(e.getSuperieur() == null ? null : e.getSuperieur().getId());
        f.setDateEmbauche(e.getDateEmbauche());
        f.setSalaireActuel(e.getSalaireActuel());
        f.setStatut(e.getStatut().name());
        f.setDateDepart(e.getDateFin());
        return f;
    }

    // ------------------------------------------------------------ écriture

    @Transactional
    public Employe creer(EmployeForm f, MultipartFile photo) {
        valider(f, null);
        String email = emailNormalise(f.getEmailPersonnel());
        if (email != null && personneRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Un compte ou un employé existe déjà avec cet email.");
        }
        Personne personne = new Personne();
        personne.setDateCreation(LocalDateTime.now());
        Employe employe = new Employe();
        employe.setPersonne(personne);
        employe.setMatricule(String.format(Locale.ROOT, "EMP-%05d", employeRepository.prochainNumeroMatricule()));
        employe.setDateCreation(LocalDateTime.now());
        appliquer(f, personne, employe);
        personneRepository.save(personne);
        employe = employeRepository.save(employe);

        if (photo != null && !photo.isEmpty()) {
            employe.setPhoto(stockage.enregistrer(photo, "photos", StockageFichierService.EXTENSIONS_PHOTO));
            employe = employeRepository.save(employe);
        }
        if (f.isCreerCompte()) {
            creerCompte(personne, employe, f.getMotDePasse());
        }
        return employe;
    }

    @Transactional
    public Employe modifier(Long id, EmployeForm f, MultipartFile photo) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
        valider(f, id);
        String email = emailNormalise(f.getEmailPersonnel());
        Personne personne = employe.getPersonne();
        if (email != null && !email.equalsIgnoreCase(nullable(personne.getEmail()))
                && personneRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        appliquer(f, personne, employe);
        String anciennePhoto = employe.getPhoto();
        if (photo != null && !photo.isEmpty()) {
            employe.setPhoto(stockage.enregistrer(photo, "photos", StockageFichierService.EXTENSIONS_PHOTO));
            stockage.supprimerSilencieusement(anciennePhoto);
        }
        personneRepository.save(personne);
        return employeRepository.save(employe);
    }

    private void appliquer(EmployeForm f, Personne personne, Employe employe) {
        personne.setNom(f.getNom().trim());
        personne.setPrenom(f.getPrenom().trim());
        personne.setDateNaissance(f.getDateNaissance());
        personne.setTelephone(vide(f.getTelephone()));
        personne.setEmail(emailNormalise(f.getEmailPersonnel()));
        personne.setVille(vide(f.getCommune()));

        employe.setLieuNaissance(vide(f.getLieuNaissance()));
        employe.setSexe(vide(f.getSexe()));
        employe.setNationalite(vide(f.getNationalite()));
        employe.setCinNumero(vide(f.getCinNumero()));
        employe.setCinDateDelivrance(f.getCinDateDelivrance());
        employe.setCinLieuDelivrance(vide(f.getCinLieuDelivrance()));
        employe.setAdresse(vide(f.getAdresse()));
        employe.setCommune(vide(f.getCommune()));
        employe.setRegion(vide(f.getRegion()));
        employe.setSituationMatrimoniale(vide(f.getSituationMatrimoniale()));
        employe.setNombreEnfants(f.getNombreEnfants() == null ? 0 : f.getNombreEnfants());
        employe.setContactUrgenceNom(vide(f.getContactUrgenceNom()));
        employe.setContactUrgenceRelation(vide(f.getContactUrgenceRelation()));
        employe.setContactUrgenceTelephone(vide(f.getContactUrgenceTelephone()));

        Departement departement = f.getDepartementId() == null ? null
                : departementRepository.findById(f.getDepartementId())
                        .orElseThrow(() -> new IllegalArgumentException("Département introuvable."));
        Poste poste = f.getPosteId() == null ? null
                : posteRepository.findById(f.getPosteId())
                        .orElseThrow(() -> new IllegalArgumentException("Poste introuvable."));
        Employe superieur = f.getSuperieurId() == null ? null
                : employeRepository.findById(f.getSuperieurId())
                        .orElseThrow(() -> new IllegalArgumentException("Supérieur hiérarchique introuvable."));
        employe.setDepartement(departement);
        employe.setPoste(poste);
        employe.setSuperieur(superieur);
        employe.setSalaireActuel(f.getSalaireActuel());
        employe.setDateEmbauche(f.getDateEmbauche());

        Employe.Statut statut = f.getStatut() == null || f.getStatut().isBlank()
                ? Employe.Statut.ACTIF : Employe.Statut.valueOf(f.getStatut());
        employe.setStatut(statut);
        employe.setDateFin(statut == Employe.Statut.SORTI
                ? (f.getDateDepart() != null ? f.getDateDepart() : LocalDate.now())
                : null);
    }

    private void valider(EmployeForm f, Long idExistant) {
        if (estVide(f.getNom()) || estVide(f.getPrenom())) {
            throw new IllegalArgumentException("Le nom et le prénom sont obligatoires.");
        }
        if (f.getDateEmbauche() == null) {
            throw new IllegalArgumentException("La date d'embauche est obligatoire.");
        }
        if (f.getDateNaissance() != null && !f.getDateNaissance().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de naissance doit être dans le passé.");
        }
        if (!estVide(f.getEmailPersonnel())
                && !f.getEmailPersonnel().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("L'adresse email n'est pas valide.");
        }
        if (!estVide(f.getSexe()) && !SEXES.contains(f.getSexe())) {
            throw new IllegalArgumentException("Sexe invalide.");
        }
        if (!estVide(f.getSituationMatrimoniale()) && !SITUATIONS.contains(f.getSituationMatrimoniale())) {
            throw new IllegalArgumentException("Situation matrimoniale invalide.");
        }
        if (f.getNombreEnfants() != null && f.getNombreEnfants() < 0) {
            throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif.");
        }
        if (f.getSalaireActuel() != null && f.getSalaireActuel().signum() < 0) {
            throw new IllegalArgumentException("Le salaire doit être positif.");
        }
        if (!estVide(f.getStatut())) {
            try {
                Employe.Statut.valueOf(f.getStatut());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Statut invalide.");
            }
        }
        if (f.getDateDepart() != null && f.getDateDepart().isBefore(f.getDateEmbauche())) {
            throw new IllegalArgumentException("La date de départ doit suivre la date d'embauche.");
        }
        if (idExistant != null && idExistant.equals(f.getSuperieurId())) {
            throw new IllegalArgumentException("Un employé ne peut pas être son propre supérieur.");
        }
        if (idExistant != null && f.getSuperieurId() != null && creeUneBoucle(idExistant, f.getSuperieurId())) {
            throw new IllegalArgumentException(
                    "Ce supérieur est lui-même placé sous cet employé : la hiérarchie serait circulaire.");
        }
        if (f.isCreerCompte()) {
            if (estVide(f.getEmailPersonnel())) {
                throw new IllegalArgumentException("Un email est nécessaire pour créer un compte de connexion.");
            }
            if (f.getMotDePasse() == null || f.getMotDePasse().length() < 8) {
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 8 caractères.");
            }
        }
    }

    /** Vrai si le futur supérieur dépend déjà (directement ou non) de l'employé. */
    private boolean creeUneBoucle(Long employeId, Long superieurId) {
        Employe courant = employeRepository.findById(superieurId).orElse(null);
        int garde = 0;
        while (courant != null && garde++ < 100) {
            if (employeId.equals(courant.getId())) return true;
            courant = courant.getSuperieur();
        }
        return false;
    }

    private void creerCompte(Personne personne, Employe employe, String motDePasse) {
        Long roleId = utilisateurRepository.findRoleIdByNom("EMPLOYE");
        if (roleId == null) {
            throw new IllegalStateException("Le rôle EMPLOYE est introuvable : exécutez data.sql.");
        }
        Utilisateur compte = new Utilisateur();
        compte.setPersonne(personne);
        compte.setRoleId(roleId);
        compte.setMotDePasseHash(passwordEncoder.encode(motDePasse));
        compte.setDepartement(employe.getDepartement());
        utilisateurRepository.save(compte);
    }

    // ------------------------------------------------------------ documents

    @Transactional(readOnly = true)
    public List<TypeDocument> typesDocument() {
        return typeDocumentRepository.findAllByOrderByLibelleAsc();
    }

    @Transactional(readOnly = true)
    public List<DocumentEmploye> documents(Long employeId) {
        return documentRepository.findByEmploye_IdOrderByDateAjoutDesc(employeId);
    }

    @Transactional
    public DocumentEmploye ajouterDocument(Long employeId, Long typeId, LocalDate expiration,
                                           MultipartFile fichier, Utilisateur acteur) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
        TypeDocument type = typeDocumentRepository.findById(typeId)
                .orElseThrow(() -> new IllegalArgumentException("Type de document introuvable."));
        String chemin = stockage.enregistrer(fichier, "employe-" + employeId,
                StockageFichierService.EXTENSIONS_DOCUMENT);
        DocumentEmploye document = new DocumentEmploye();
        document.setEmploye(employe);
        document.setTypeDocument(type);
        document.setNomFichier(nomAffichable(fichier.getOriginalFilename()));
        document.setCheminFichier(chemin);
        document.setDateAjout(LocalDateTime.now());
        document.setDateExpiration(expiration);
        document.setUtilisateur(acteur);
        return documentRepository.save(document);
    }

    @Transactional
    public void supprimerDocument(Long employeId, Long documentId) {
        DocumentEmploye document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable."));
        if (!employeId.equals(document.getEmploye().getId())) {
            throw new IllegalArgumentException("Ce document n'appartient pas à cet employé.");
        }
        documentRepository.delete(document);
        stockage.supprimerSilencieusement(document.getCheminFichier());
    }

    @Transactional(readOnly = true)
    public DocumentEmploye document(Long employeId, Long documentId) {
        DocumentEmploye document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable."));
        if (!employeId.equals(document.getEmploye().getId())) {
            throw new IllegalArgumentException("Ce document n'appartient pas à cet employé.");
        }
        return document;
    }

    // ------------------------------------------------------------ utilitaires

    private static String nomAffichable(String nom) {
        if (nom == null || nom.isBlank()) return "document";
        String propre = nom.replace('\\', '/');
        propre = propre.substring(propre.lastIndexOf('/') + 1).replaceAll("[\\r\\n]", "");
        return propre.length() > 200 ? propre.substring(propre.length() - 200) : propre;
    }

    private static String emailNormalise(String email) {
        return estVide(email) ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean estVide(String valeur) {
        return valeur == null || valeur.isBlank();
    }

    private static String vide(String valeur) {
        return estVide(valeur) ? null : valeur.trim();
    }

    private static String nullable(String valeur) {
        return valeur == null ? "" : valeur;
    }
}
