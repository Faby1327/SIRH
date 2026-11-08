package com.example.logiciel_RH.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.logiciel_RH.entity.DocumentEmploye;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.service.EmployeService;
import com.example.logiciel_RH.service.StockageFichierService;

import jakarta.servlet.http.HttpSession;

/**
 * Téléchargement des documents et photos. Les fichiers ne sont jamais servis
 * en statique : chaque accès passe par le contrôle des droits de lecture.
 */
@Controller
@RequestMapping("/employes")
public class EmployeFichierController {

    private final EmployeRepository employeRepository;
    private final EmployeService employeService;
    private final StockageFichierService stockage;

    public EmployeFichierController(EmployeRepository employeRepository,
                                    EmployeService employeService,
                                    StockageFichierService stockage) {
        this.employeRepository = employeRepository;
        this.employeService = employeService;
        this.stockage = stockage;
    }

    @GetMapping("/{id}/documents/{documentId}")
    public ResponseEntity<Resource> telecharger(@PathVariable("id") Long id,
                                                @PathVariable("documentId") Long documentId,
                                                HttpSession session) {
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) return ResponseEntity.notFound().build();
        if (!employeService.peutConsulter(SessionUtils.getUtilisateur(session), employe)) {
            return ResponseEntity.status(403).build();
        }
        try {
            DocumentEmploye document = employeService.document(id, documentId);
            Resource ressource = stockage.charger(document.getCheminFichier());
            return reponse(ressource, document.getNomFichier(), false);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> photo(@PathVariable("id") Long id, HttpSession session) {
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null || employe.getPhoto() == null) return ResponseEntity.notFound().build();
        if (!employeService.peutConsulter(SessionUtils.getUtilisateur(session), employe)) {
            return ResponseEntity.status(403).build();
        }
        try {
            return reponse(stockage.charger(employe.getPhoto()), employe.getPhoto(), true);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<Resource> reponse(Resource ressource, String nom, boolean enLigne) {
        String minuscule = nom.toLowerCase();
        MediaType type = minuscule.endsWith(".pdf") ? MediaType.APPLICATION_PDF
                : minuscule.endsWith(".png") ? MediaType.IMAGE_PNG
                : (minuscule.endsWith(".jpg") || minuscule.endsWith(".jpeg")) ? MediaType.IMAGE_JPEG
                : MediaType.APPLICATION_OCTET_STREAM;
        ContentDisposition disposition = (enLigne ? ContentDisposition.inline() : ContentDisposition.attachment())
                .filename(enLigne ? "photo" : nom, java.nio.charset.StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(type)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(ressource);
    }
}
