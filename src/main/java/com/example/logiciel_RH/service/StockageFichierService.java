package com.example.logiciel_RH.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stockage des fichiers téléversés (documents et photos des employés).
 *
 * Le nom d'origine n'est jamais utilisé pour écrire sur le disque : le fichier
 * est enregistré sous un nom aléatoire, ce qui évite les collisions et les
 * chemins du type ../../.
 */
@Service
public class StockageFichierService {

    public static final Set<String> EXTENSIONS_DOCUMENT =
            Set.of("pdf", "jpg", "jpeg", "png", "doc", "docx");
    public static final Set<String> EXTENSIONS_PHOTO = Set.of("jpg", "jpeg", "png");

    private final Path racine;

    public StockageFichierService(@Value("${sirh.upload-dir:uploads/documents}") String dossier) {
        this.racine = Paths.get(dossier).toAbsolutePath().normalize();
    }

    /** Enregistre le fichier dans le sous-dossier donné et retourne son chemin relatif. */
    public String enregistrer(MultipartFile fichier, String sousDossier, Set<String> extensionsAutorisees) {
        if (fichier == null || fichier.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier sélectionné.");
        }
        String extension = extension(fichier.getOriginalFilename());
        if (!extensionsAutorisees.contains(extension)) {
            throw new IllegalArgumentException(
                    "Format non accepté. Formats autorisés : " + String.join(", ", extensionsAutorisees) + ".");
        }
        String relatif = sousDossier + "/" + UUID.randomUUID() + "." + extension;
        Path cible = resoudre(relatif);
        try {
            Files.createDirectories(cible.getParent());
            try (InputStream in = fichier.getInputStream()) {
                Files.copy(in, cible, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Le fichier n'a pas pu être enregistré.", ex);
        }
        return relatif;
    }

    public Resource charger(String cheminRelatif) {
        Path chemin = resoudre(cheminRelatif);
        if (!Files.isRegularFile(chemin)) {
            throw new IllegalArgumentException("Fichier introuvable sur le serveur.");
        }
        return new PathResource(chemin);
    }

    public void supprimerSilencieusement(String cheminRelatif) {
        if (cheminRelatif == null || cheminRelatif.isBlank()) return;
        try {
            Files.deleteIfExists(resoudre(cheminRelatif));
        } catch (IOException | IllegalArgumentException ignored) {
            // Un fichier orphelin n'empêche pas de poursuivre.
        }
    }

    private Path resoudre(String cheminRelatif) {
        Path chemin = racine.resolve(cheminRelatif).normalize();
        if (!chemin.startsWith(racine)) {
            throw new IllegalArgumentException("Chemin de fichier invalide.");
        }
        return chemin;
    }

    private static String extension(String nom) {
        if (nom == null) return "";
        int point = nom.lastIndexOf('.');
        return point < 0 ? "" : nom.substring(point + 1).toLowerCase(Locale.ROOT);
    }
}
