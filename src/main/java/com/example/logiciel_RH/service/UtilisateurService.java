package com.example.logiciel_RH.service;

import java.util.Locale;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.logiciel_RH.entity.Utilisateur;
import com.example.logiciel_RH.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authentifie un utilisateur.
     *
     * Les nouveaux mots de passe sont hachés en BCrypt. Les comptes de
     * démonstration créés avant la migration contiennent encore une valeur en
     * clair : on accepte ce format le temps de la bascule.
     */
    public Utilisateur login(String email, String motDePasse) {
        if (email == null || motDePasse == null) {
            return null;
        }

        Optional<Utilisateur> optional = repository.findByPersonne_EmailIgnoreCase(email.trim());

        if (optional.isEmpty()) {
            return null;
        }

        Utilisateur utilisateur = optional.get();
        String stocke = utilisateur.getMotDePasseHash();

        if (stocke == null) {
            return null;
        }

        boolean valide = stocke.startsWith("$2")
                ? passwordEncoder.matches(motDePasse, stocke)
                : stocke.equals(motDePasse);

        return valide ? utilisateur : null;
    }

    public String normaliserEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
