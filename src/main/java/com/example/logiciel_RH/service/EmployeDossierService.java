package com.example.logiciel_RH.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Departement;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.EmployeForm;
import com.example.logiciel_RH.entity.Personne;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.DepartementRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.PersonneRepository;
import com.example.logiciel_RH.repository.PosteRepository;
import com.example.logiciel_RH.repository.UtilisateurRepository;

@Service
public class EmployeDossierService {
    private static final Set<String> SEXES = Set.of("M", "F");
    private static final Set<String> SITUATIONS = Set.of("CELIBATAIRE", "MARIE", "DIVORCE", "VEUF", "UNION_LIBRE");
    private final EmployeRepository employeRepository;
    private final PersonneRepository personneRepository;
    private final DepartementRepository departementRepository;
    private final PosteRepository posteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeDossierService(EmployeRepository employeRepository, PersonneRepository personneRepository,
                                 DepartementRepository departementRepository, PosteRepository posteRepository,
                                 UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.employeRepository = employeRepository; this.personneRepository = personneRepository;
        this.departementRepository = departementRepository; this.posteRepository = posteRepository;
        this.utilisateurRepository = utilisateurRepository; this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Employe creer(EmployeForm form) {
        valider(form, null);
        String email = normaliser(form.getEmailPersonnel());
        if (email != null && personneRepository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Cet email est déjà utilisé.");
        Personne personne = new Personne(); personne.setDateCreation(LocalDateTime.now());
        Employe employe = new Employe(); employe.setPersonne(personne); employe.setDateCreation(LocalDateTime.now());
        Long numeroMatricule = employeRepository.prochainNumeroMatriculeDisponible();
        employe.setMatricule(String.format(Locale.ROOT, "EMP-%05d", numeroMatricule));
        appliquer(form, personne, employe);
        personneRepository.save(personne); employeRepository.save(employe);
        if (form.isCreerCompte()) creerCompte(personne, employe, form.getMotDePasse());
        return employe;
    }

    @Transactional
    public Employe modifier(Long id, EmployeForm form) {
        Employe employe = employeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
        valider(form, id);
        String email = normaliser(form.getEmailPersonnel());
        if (email != null && !email.equalsIgnoreCase(nullToEmpty(employe.getPersonne().getEmail()))
                && personneRepository.existsByEmailIgnoreCase(email)) throw new IllegalArgumentException("Cet email est déjà utilisé.");
        appliquer(form, employe.getPersonne(), employe);
        personneRepository.save(employe.getPersonne()); return employeRepository.save(employe);
    }

    @Transactional(readOnly = true)
    public EmployeForm versFormulaire(Employe e) {
        EmployeForm f = new EmployeForm(); Personne p = e.getPersonne();
        f.setNom(p.getNom()); f.setPrenom(p.getPrenom()); f.setDateNaissance(p.getDateNaissance()); f.setTelephone(p.getTelephone()); f.setEmailPersonnel(p.getEmail());
        f.setLieuNaissance(e.getLieuNaissance()); f.setSexe(e.getSexe()); f.setNationalite(e.getNationalite()); f.setCinNumero(e.getCinNumero()); f.setCinDateDelivrance(e.getCinDateDelivrance()); f.setCinLieuDelivrance(e.getCinLieuDelivrance());
        f.setAdresse(e.getAdresse()); f.setCommune(e.getCommune()); f.setRegion(e.getRegion()); f.setSituationMatrimoniale(e.getSituationMatrimoniale()); f.setNombreEnfants(e.getNombreEnfants());
        f.setContactUrgenceNom(e.getContactUrgenceNom()); f.setContactUrgenceRelation(e.getContactUrgenceRelation()); f.setContactUrgenceTelephone(e.getContactUrgenceTelephone());
        f.setDepartementId(e.getDepartement() == null ? null : e.getDepartement().getIdDepartement()); f.setPosteId(e.getPoste() == null ? null : e.getPoste().getIdPoste()); f.setSuperieurId(e.getSuperieur() == null ? null : e.getSuperieur().getId());
        f.setDateEmbauche(e.getDateEmbauche()); f.setSalaireActuel(e.getSalaireActuel()); f.setStatut(e.getStatut().name()); f.setDateDepart(e.getDateFin()); return f;
    }

    private void appliquer(EmployeForm f, Personne p, Employe e) {
        p.setNom(f.getNom().trim()); p.setPrenom(f.getPrenom().trim()); p.setDateNaissance(f.getDateNaissance()); p.setTelephone(vide(f.getTelephone())); p.setEmail(normaliser(f.getEmailPersonnel())); p.setVille(vide(f.getCommune()));
        e.setLieuNaissance(vide(f.getLieuNaissance())); e.setSexe(vide(f.getSexe())); e.setNationalite(vide(f.getNationalite())); e.setCinNumero(vide(f.getCinNumero())); e.setCinDateDelivrance(f.getCinDateDelivrance()); e.setCinLieuDelivrance(vide(f.getCinLieuDelivrance()));
        e.setAdresse(vide(f.getAdresse())); e.setCommune(vide(f.getCommune())); e.setRegion(vide(f.getRegion())); e.setSituationMatrimoniale(vide(f.getSituationMatrimoniale())); e.setNombreEnfants(f.getNombreEnfants() == null ? 0 : f.getNombreEnfants());
        e.setContactUrgenceNom(vide(f.getContactUrgenceNom())); e.setContactUrgenceRelation(vide(f.getContactUrgenceRelation())); e.setContactUrgenceTelephone(vide(f.getContactUrgenceTelephone()));
        e.setDepartement(f.getDepartementId() == null ? null : departementRepository.findById(f.getDepartementId()).orElseThrow(() -> new IllegalArgumentException("Département introuvable.")));
        e.setPoste(f.getPosteId() == null ? null : posteRepository.findById(f.getPosteId()).orElseThrow(() -> new IllegalArgumentException("Poste introuvable.")));
        e.setSuperieur(f.getSuperieurId() == null ? null : employeRepository.findById(f.getSuperieurId()).orElseThrow(() -> new IllegalArgumentException("Supérieur hiérarchique introuvable.")));
        e.setSalaireActuel(f.getSalaireActuel()); e.setDateEmbauche(f.getDateEmbauche()); Employe.Statut s = f.getStatut() == null || f.getStatut().isBlank() ? Employe.Statut.ACTIF : Employe.Statut.valueOf(f.getStatut()); e.setStatut(s); e.setDateFin(s == Employe.Statut.SORTI ? (f.getDateDepart() == null ? LocalDate.now() : f.getDateDepart()) : null);
    }

    private void valider(EmployeForm f, Long id) {
        if (vide(f.getNom()) == null || vide(f.getPrenom()) == null) throw new IllegalArgumentException("Le nom et le prénom sont obligatoires.");
        if (f.getDateEmbauche() == null) throw new IllegalArgumentException("La date d'embauche est obligatoire.");
        if (f.getDateNaissance() != null && !f.getDateNaissance().isBefore(LocalDate.now())) throw new IllegalArgumentException("La date de naissance doit être dans le passé.");
        if (f.getEmailPersonnel() != null && !f.getEmailPersonnel().isBlank() && !f.getEmailPersonnel().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("L'adresse email n'est pas valide.");
        if (vide(f.getSexe()) != null && !SEXES.contains(f.getSexe())) throw new IllegalArgumentException("Sexe invalide.");
        if (vide(f.getSituationMatrimoniale()) != null && !SITUATIONS.contains(f.getSituationMatrimoniale())) throw new IllegalArgumentException("Situation matrimoniale invalide.");
        if (f.getNombreEnfants() != null && f.getNombreEnfants() < 0) throw new IllegalArgumentException("Le nombre d'enfants ne peut pas être négatif.");
        if (f.getSalaireActuel() != null && f.getSalaireActuel().signum() < 0) throw new IllegalArgumentException("Le salaire doit être positif.");
        if (f.getDateDepart() != null && f.getDateDepart().isBefore(f.getDateEmbauche())) throw new IllegalArgumentException("La date de départ doit suivre la date d'embauche.");
        if (id != null && id.equals(f.getSuperieurId())) throw new IllegalArgumentException("Un employé ne peut pas être son propre supérieur.");
        if (f.isCreerCompte() && (vide(f.getEmailPersonnel()) == null || f.getMotDePasse() == null || f.getMotDePasse().length() < 8)) throw new IllegalArgumentException("Un email et un mot de passe de 8 caractères sont nécessaires pour créer un compte.");
    }

    private void creerCompte(Personne p, Employe e, String motDePasse) { Long roleId = utilisateurRepository.findRoleIdByNom("EMPLOYE"); if (roleId == null) throw new IllegalStateException("Le rôle EMPLOYE est introuvable."); Utilisateur u = new Utilisateur(); u.setPersonne(p); u.setRoleId(roleId); u.setMotDePasseHash(passwordEncoder.encode(motDePasse)); u.setDepartement(e.getDepartement()); utilisateurRepository.save(u); }
    private String normaliser(String v) { return v == null || v.isBlank() ? null : v.trim().toLowerCase(Locale.ROOT); }
    private String vide(String v) { return v == null || v.isBlank() ? null : v.trim(); }
    private String nullToEmpty(String v) { return v == null ? "" : v; }
}
