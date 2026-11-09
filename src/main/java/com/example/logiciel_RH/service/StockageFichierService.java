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

@Service
public class StockageFichierService {
    public static final Set<String> EXTENSIONS_DOCUMENT = Set.of("pdf", "jpg", "jpeg", "png", "doc", "docx");
    public static final Set<String> EXTENSIONS_PHOTO = Set.of("jpg", "jpeg", "png");
    private final Path racine;
    public StockageFichierService(@Value("${sirh.upload-dir:uploads/documents}") String dossier) { racine = Paths.get(dossier).toAbsolutePath().normalize(); }
    public String enregistrer(MultipartFile fichier, String sousDossier, Set<String> autorisees) {
        if (fichier == null || fichier.isEmpty()) throw new IllegalArgumentException("Aucun fichier sélectionné.");
        String extension = extension(fichier.getOriginalFilename());
        if (!autorisees.contains(extension)) throw new IllegalArgumentException("Format de fichier non accepté.");
        String relatif = sousDossier + "/" + UUID.randomUUID() + "." + extension; Path cible = resoudre(relatif);
        try { Files.createDirectories(cible.getParent()); try (InputStream in = fichier.getInputStream()) { Files.copy(in, cible, StandardCopyOption.REPLACE_EXISTING); } }
        catch (IOException ex) { throw new IllegalStateException("Le fichier n'a pas pu être enregistré.", ex); }
        return relatif;
    }
    public Resource charger(String cheminRelatif) { Path chemin = resoudre(cheminRelatif); if (!Files.isRegularFile(chemin)) throw new IllegalArgumentException("Fichier introuvable."); return new PathResource(chemin); }
    public void supprimerSilencieusement(String cheminRelatif) { if (cheminRelatif == null || cheminRelatif.isBlank()) return; try { Files.deleteIfExists(resoudre(cheminRelatif)); } catch (IOException | IllegalArgumentException ignored) {} }
    private Path resoudre(String relatif) { Path chemin = racine.resolve(relatif).normalize(); if (!chemin.startsWith(racine)) throw new IllegalArgumentException("Chemin de fichier invalide."); return chemin; }
    private String extension(String nom) { if (nom == null) return ""; int point = nom.lastIndexOf('.'); return point < 0 ? "" : nom.substring(point + 1).toLowerCase(Locale.ROOT); }
}
