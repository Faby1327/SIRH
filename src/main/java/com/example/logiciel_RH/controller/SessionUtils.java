package com.example.logiciel_RH.controller;

import com.example.logiciel_RH.entity.Utilisateur;

import jakarta.servlet.http.HttpSession;

public final class SessionUtils {

    private SessionUtils() {
    }

    public static Utilisateur getUtilisateur(HttpSession session) {
        return (Utilisateur) session.getAttribute("utilisateurConnecte");
    }

    public static boolean hasRole(HttpSession session, String role) {
        Utilisateur utilisateur = getUtilisateur(session);
        return utilisateur != null && role.equals(utilisateur.getRole());
    }
}
