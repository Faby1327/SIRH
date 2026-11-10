package com.example.logiciel_RH.controller;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.logiciel_RH.entity.*;
import com.example.logiciel_RH.repository.EmployeBloc7Repository;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.service.Bloc7Service;
import jakarta.servlet.http.HttpSession;

@Controller
public class Bloc7Controller {

    private final Bloc7Service service;
    private final EmployeBloc7Repository employes;
    private final ContratRepository contrats;

    public Bloc7Controller(Bloc7Service service, EmployeBloc7Repository employes, ContratRepository contrats) {
        this.service = service;
        this.employes = employes;
        this.contrats = contrats;
    }

    @GetMapping("/mes-demandes")
    public String mesDemandes(@RequestParam(defaultValue = "conge") String onglet, HttpSession s, Model m) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "EMPLOYE")) {
            return "redirect:/login";

        }
        chargerListe(u, m);
        m.addAttribute("solde", service.solde(u));
        m.addAttribute("onglet", onglet);
        m.addAttribute("today", LocalDate.now());
        return "conges/absences/mesDemandes";
    }

    @GetMapping("/gestion-demandes")
    public String gestion(@RequestParam(defaultValue = "conge") String onglet, HttpSession s, Model m) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER", "RH")) {
            return "redirect:/login";

        }
        chargerListe(u, m);
        m.addAttribute("onglet", onglet);
        m.addAttribute("today", LocalDate.now());
        List<Employe> listeEmployes = "MANAGER".equals(u.getRole()) ? service.employesDepartement(u) : employes.findAll();
        m.addAttribute("employes", listeEmployes);
        Map<Long, Contrat> contratsActifs = new HashMap<>();
        for (Employe e : listeEmployes) {
            contrats.findByEmploye_IdAndStatut(e.getId(), Contrat.Statut.EN_COURS).stream().findFirst().ifPresent(c -> contratsActifs.put(e.getId(), c));

        }
        m.addAttribute("contratsActifs", contratsActifs);
        return "conges/absences/gestionDemandes";
    }

    private void chargerListe(Utilisateur u, Model m) {
        m.addAttribute("utilisateurConnecte", u);
        m.addAttribute("demandesConge", service.conges(u));
        m.addAttribute("demandesAbsence", service.absences(u));
        m.addAttribute("role", u.getRole());
    }

    @PostMapping("/mes-demandes/conge")
    public String demanderConge(@RequestParam LocalDate dateDebut, @RequestParam LocalDate dateFin, @RequestParam(required = false) String motif, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "EMPLOYE")) {
            return "redirect:/login";

        }
        return action(ra, "/mes-demandes?onglet=conge", () -> service.creerConge(u, dateDebut, dateFin, motif));
    }

    @PostMapping("/mes-demandes/absence")
    public String declarerAbsence(@RequestParam LocalDate dateDebut, @RequestParam LocalDate dateFin, @RequestParam String motif, @RequestParam(required = false) MultipartFile justificatif, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "EMPLOYE")) {
            return "redirect:/login";

        }
        try {
            String fichier = stocker(justificatif);
            service.creerAbsence(u, service.employe(u).getId(), dateDebut, dateFin, motif, fichier);
            ra.addFlashAttribute("message", "Absence déclarée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", message(e));
        }
        return "redirect:/mes-demandes?onglet=absence";
    }

    @PostMapping("/gestion-demandes/conge/{id}/decision")
    public String decisionConge(@PathVariable Long id, @RequestParam boolean accepter, @RequestParam(required = false) String commentaire, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER", "RH")) {
            return "redirect:/login";

        }
        return action(ra, "/detailsDemande?type=conge&id=" + id, () -> service.decisionConge(id, u, accepter, commentaire));
    }

    @PostMapping("/gestion-demandes/absence")
    public String declarerAbsenceManager(@RequestParam Long employeId, @RequestParam LocalDate dateDebut, @RequestParam LocalDate dateFin, @RequestParam String motif, @RequestParam(required = false) MultipartFile justificatif, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER")) {
            return "redirect:/login";

        }
        try {
            service.creerAbsence(u, employeId, dateDebut, dateFin, motif, stocker(justificatif));
            ra.addFlashAttribute("message", "Absence enregistrée et transmise à RH.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", message(e));
        }
        return "redirect:/gestion-demandes?onglet=absence";
    }

    @PostMapping("/gestion-demandes/absence/{id}/transmettre")
    public String transmettre(@PathVariable Long id, @RequestParam(required = false) String commentaire, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER")) {
            return "redirect:/login";

        }
        return action(ra, "/detailsDemande?type=absence&id=" + id, () -> service.commentaireAbsence(id, u, commentaire));
    }

    @PostMapping("/gestion-demandes/absence/{id}/verifier")
    public String verifier(@PathVariable Long id, @RequestParam boolean justifiee, @RequestParam(required = false) String commentaire, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "RH")) {
            return "redirect:/login";

        }
        return action(ra, "/detailsDemande?type=absence&id=" + id, () -> service.verifierAbsence(id, u, justifiee, commentaire));
    }

    @PostMapping("/detailsDemande/conge/{id}/remplacement")
    public String remplacement(@PathVariable Long id, @RequestParam(required = false) Long remplacantId, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER")) {
            return "redirect:/login";

        }
        return action(ra, "/detailsDemande?type=conge&id=" + id, () -> service.remplacer(id, u, remplacantId));
    }

    @PostMapping("/detailsDemande/absence/{id}/remplacement")
    public String remplacementAbsence(@PathVariable Long id, @RequestParam(required = false) Long remplacantId, HttpSession s, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "MANAGER")) {
            return "redirect:/login";

        }
        return action(ra, "/detailsDemande?type=absence&id=" + id, () -> service.remplacerAbsence(id, u, remplacantId));
    }

    @GetMapping("/detailsDemande")
    public String detail(@RequestParam String type, @RequestParam Long id, HttpSession s, Model m, RedirectAttributes ra) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (!role(u, "EMPLOYE", "MANAGER", "RH")) {
            return "redirect:/login";

        }
        try {
            m.addAttribute("role", u.getRole());
            if ("conge".equals(type)) {
                DemandeConge d = service.conge(id);
                if (!autorise(u, d.getEmploye())) {
                    throw new SecurityException("Accès refusé.");

                }
                m.addAttribute("conge", d);
                m.addAttribute("soldeEmploye", service.solde(d.getEmploye().getId()));
                m.addAttribute("historique", service.historique(id));
                m.addAttribute("remplacantsDisponibles", "MANAGER".equals(u.getRole()) ? service.remplacantsDisponibles(u, d) : List.of());
            } else if ("absence".equals(type)) {
                DemandeAbsence d = service.absence(id);
                if (!autorise(u, d.getEmploye())) {
                    throw new SecurityException("Accès refusé.");

                }
                m.addAttribute("absence", d);
                m.addAttribute("soldeEmploye", service.solde(d.getEmploye().getId()));
                m.addAttribute("historique", service.historiqueAbsence(id));
                m.addAttribute("remplacantsDisponibles", "MANAGER".equals(u.getRole()) ? service.remplacantsDisponibles(u, d) : List.of());
            } else {
                throw new IllegalArgumentException("Type de demande invalide.");

            }
            m.addAttribute("utilisateurConnecte", u);
            return "conges/absences/detailsDemande";
        } catch (Exception e) {
            ra.addFlashAttribute("error", message(e));
            return "redirect:" + ("EMPLOYE".equals(u.getRole()) ? "/mes-demandes" : "/gestion-demandes");
        }
    }

    @GetMapping("/bloc7-documents/{filename}")
    public ResponseEntity<Resource> document(@PathVariable String filename, HttpSession s) {
        Utilisateur u = SessionUtils.getUtilisateur(s);
        if (filename.contains("/") || filename.contains("\\") || !service.documentAccessible(u, "/bloc7-documents/" + filename)) {
            return ResponseEntity.notFound().build();

        }
        Path path = Paths.get("uploads", "bloc7", filename).toAbsolutePath().normalize();
        if (!Files.isRegularFile(path)) {
            return ResponseEntity.notFound().build();

        }
        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = null;
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType == null ? "application/octet-stream" : contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename.replaceAll("[\"\\r\\n]", "_") + "\"").body(new FileSystemResource(path));
    }

    private boolean autorise(Utilisateur u, Employe e) {
        if ("RH".equals(u.getRole())) {
            return true;

        }
        if ("EMPLOYE".equals(u.getRole())) {
            return e.getPersonne().getIdPersonne().equals(u.getPersonne().getIdPersonne());

        }
        return service.autoriseManager(u, e);
    }

    private static boolean role(Utilisateur u, String... roles) {
        return u != null && Arrays.asList(roles).contains(u.getRole());
    }

    private static String stocker(MultipartFile f) throws IOException {
        if (f == null || f.isEmpty()) {
            return null;

        }
        if (f.getSize() > 10_000_000) {
            throw new IllegalArgumentException("Le justificatif dépasse 10 Mo.");

        }
        String original = Objects.requireNonNull(f.getOriginalFilename());
        String extension = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        if (!Set.of("pdf", "png", "jpg", "jpeg").contains(extension)) {
            throw new IllegalArgumentException("Formats autorisés : PDF, PNG et JPEG.");

        }
        String nom = UUID.randomUUID() + "." + extension;
        Path dir = Paths.get("uploads", "bloc7").toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Files.copy(f.getInputStream(), dir.resolve(nom), StandardCopyOption.REPLACE_EXISTING);
        return "/bloc7-documents/" + nom;
    }

    private static String message(Exception e) {
        return e.getMessage() == null ? "Une erreur est survenue." : e.getMessage();
    }

    private String action(RedirectAttributes ra, String dest, Runnable r) {
        try {
            r.run();
            ra.addFlashAttribute("message", "Opération enregistrée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", message(e));
        }
        return "redirect:" + dest;
    }
}
