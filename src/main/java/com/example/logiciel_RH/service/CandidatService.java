package com.example.logiciel_RH.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.logiciel_RH.entity.Candidat;
import com.example.logiciel_RH.entity.Personne;
import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.CandidatRepository;
import com.example.logiciel_RH.repository.PersonneRepository;
import com.example.logiciel_RH.repository.UtilisateurRepository;

/**
 * Comptes candidats : création du compte de connexion et de son profil.
 */
@Service
public class CandidatService {

    private final UtilisateurRepository utilisateurRepository;
    private final CandidatRepository candidatRepository;
    private final PersonneRepository personneRepository;
    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    public CandidatService(UtilisateurRepository utilisateurRepository,
                           CandidatRepository candidatRepository,
                           PersonneRepository personneRepository,
                           UtilisateurService utilisateurService,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.candidatRepository = candidatRepository;
        this.personneRepository = personneRepository;
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un compte candidat (utilisateur + profil) dans une même transaction.
     *
     * @throws IllegalArgumentException si l'email est déjà utilisé ou si un
     *                                  champ obligatoire manque.
     */
    @Transactional
    public Candidat inscrire(String nom,
                             String prenom,
                             String email,
                             String telephone,
                             String ville,
                             String motDePasse) {
        String emailNormalise = utilisateurService.normaliserEmail(email);
        if (personneRepository.existsByEmailIgnoreCase(emailNormalise)) {
            throw new IllegalArgumentException("Un compte existe déjà avec cette adresse email.");
        }

        Long roleCandidat = utilisateurRepository.findRoleIdByNom("CANDIDAT");
        if (roleCandidat == null) {
            throw new IllegalStateException("Le rôle CANDIDAT n'existe pas en base.");
        }

        Personne personne = new Personne();
        personne.setNom(nom.trim());
        personne.setPrenom(prenom.trim());
        personne.setEmail(emailNormalise);
        personne.setTelephone(telephone == null ? null : telephone.trim());
        personne.setVille(ville == null || ville.isBlank() ? null : ville.trim());
        personne.setDateCreation(LocalDateTime.now());
        personne = personneRepository.save(personne);

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPersonne(personne);
        utilisateur.setMotDePasseHash(passwordEncoder.encode(motDePasse));
        utilisateur.setRoleId(roleCandidat);
        utilisateur = utilisateurRepository.save(utilisateur);

        Candidat candidat = new Candidat();
        candidat.setPersonne(personne);
        candidat.setDateCreation(LocalDateTime.now());
        return candidatRepository.save(candidat);
    }

    public Optional<Candidat> parUtilisateur(Utilisateur utilisateur) {
        if (utilisateur == null || utilisateur.getIdUtilisateur() == null) {
            return Optional.empty();
        }
        return candidatRepository.findByPersonne_Utilisateur_IdUtilisateur(utilisateur.getIdUtilisateur());
    }
}
