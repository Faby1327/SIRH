package com.example.logiciel_RH.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.entity.DocumentEmploye;
import com.example.logiciel_RH.repository.DocumentEmployeRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.TypeDocumentRepository;
import com.example.logiciel_RH.service.StockageFichierService;

import jakarta.servlet.http.HttpSession;

@Controller
public class EmployeDocumentController {
    private final EmployeRepository employeRepository;
    private final DocumentEmployeRepository documentRepository;
    private final TypeDocumentRepository typeRepository;
    private final StockageFichierService stockage;

    public EmployeDocumentController(EmployeRepository employeRepository, DocumentEmployeRepository documentRepository,
                                     TypeDocumentRepository typeRepository, StockageFichierService stockage) {
        this.employeRepository = employeRepository; this.documentRepository = documentRepository; this.typeRepository = typeRepository; this.stockage = stockage;
    }

    @GetMapping("/rh/employes/{id}/documents")
    @Transactional(readOnly = true)
    public String documents(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        var employe = employeRepository.findById(id).orElse(null);
        if (employe == null) { redirectAttributes.addFlashAttribute("error", "Employé introuvable."); return "redirect:/rh/employes"; }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session)); model.addAttribute("employe", employe);
        model.addAttribute("documents", documentRepository.findByEmploye_IdOrderByDateAjoutDesc(id)); model.addAttribute("typesDocument", typeRepository.findAllByOrderByLibelleAsc());
        return "rh/documents-employe";
    }

    @PostMapping("/rh/employes/{id}/documents")
    public String ajouter(@PathVariable Long id, @RequestParam Long typeDocumentId, @RequestParam(required = false) String dateExpiration,
                          @RequestParam MultipartFile fichier, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            var employe = employeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            var type = typeRepository.findById(typeDocumentId).orElseThrow(() -> new IllegalArgumentException("Type de document introuvable."));
            DocumentEmploye document = new DocumentEmploye(); document.setEmploye(employe); document.setTypeDocument(type); document.setNomFichier(fichier.getOriginalFilename());
            document.setCheminFichier(stockage.enregistrer(fichier, "documents/" + id, StockageFichierService.EXTENSIONS_DOCUMENT));
            document.setDateAjout(LocalDateTime.now()); document.setDateExpiration(dateExpiration == null || dateExpiration.isBlank() ? null : LocalDate.parse(dateExpiration)); document.setUtilisateur(SessionUtils.getUtilisateur(session));
            documentRepository.save(document); redirectAttributes.addFlashAttribute("message", "Document ajouté au dossier.");
        } catch (RuntimeException ex) { redirectAttributes.addFlashAttribute("error", "Document non ajouté : " + ex.getMessage()); }
        return "redirect:/rh/employes/" + id + "/documents";
    }

    @PostMapping("/rh/employes/{id}/documents/{documentId}/supprimer")
    public String supprimer(@PathVariable Long id, @PathVariable Long documentId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try { DocumentEmploye d = documentRepository.findById(documentId).orElseThrow(() -> new IllegalArgumentException("Document introuvable.")); if (!id.equals(d.getEmploye().getId())) throw new IllegalArgumentException("Document non associé à cet employé."); stockage.supprimerSilencieusement(d.getCheminFichier()); documentRepository.delete(d); redirectAttributes.addFlashAttribute("message", "Document supprimé."); }
        catch (RuntimeException ex) { redirectAttributes.addFlashAttribute("error", ex.getMessage()); }
        return "redirect:/rh/employes/" + id + "/documents";
    }

    @GetMapping("/employes/{id}/documents/{documentId}")
    public ResponseEntity<Resource> telecharger(@PathVariable Long id, @PathVariable Long documentId, HttpSession session) {
        if (!peutAcceder(id, session)) return ResponseEntity.status(403).build();
        try { DocumentEmploye d = documentRepository.findById(documentId).orElseThrow(); if (!id.equals(d.getEmploye().getId())) return ResponseEntity.status(403).build(); Resource resource = stockage.charger(d.getCheminFichier()); return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(d.getNomFichier()).build().toString()).body(resource); }
        catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
    }

    @PostMapping("/rh/employes/{id}/photo")
    public String photo(@PathVariable Long id, @RequestParam MultipartFile photo, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try { var employe = employeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Employé introuvable.")); stockage.supprimerSilencieusement(employe.getPhoto()); employe.setPhoto(stockage.enregistrer(photo, "photos", StockageFichierService.EXTENSIONS_PHOTO)); employeRepository.save(employe); redirectAttributes.addFlashAttribute("message", "Photo mise à jour."); }
        catch (RuntimeException ex) { redirectAttributes.addFlashAttribute("error", "Photo non enregistrée : " + ex.getMessage()); }
        return "redirect:/rh/employes/" + id;
    }

    @GetMapping("/employes/{id}/photo")
    public ResponseEntity<Resource> afficherPhoto(@PathVariable Long id, HttpSession session) {
        if (!peutAcceder(id, session)) return ResponseEntity.status(403).build();
        try { var e = employeRepository.findById(id).orElseThrow(); if (e.getPhoto() == null) return ResponseEntity.notFound().build(); return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(stockage.charger(e.getPhoto())); }
        catch (RuntimeException ex) { return ResponseEntity.notFound().build(); }
    }

    private boolean peutAcceder(Long employeId, HttpSession session) {
        if (SessionUtils.hasRole(session, "RH")) return true;
        var utilisateur = SessionUtils.getUtilisateur(session);
        if (!SessionUtils.hasRole(session, "EMPLOYE") || utilisateur == null || utilisateur.getPersonne() == null) return false;
        var personne = utilisateur.getPersonne();
        return employeRepository.findById(employeId)
                .map(e -> e.getPersonne() != null && personne.getIdPersonne().equals(e.getPersonne().getIdPersonne()))
                .orElse(false);
    }
}
