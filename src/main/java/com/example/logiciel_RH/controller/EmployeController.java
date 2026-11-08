package com.example.logiciel_RH.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.logiciel_RH.dto.EmployeForm;
import com.example.logiciel_RH.entity.Contrat;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.repository.ContratRepository;
import com.example.logiciel_RH.repository.DepartementRepository;
import com.example.logiciel_RH.repository.EmployeRepository;
import com.example.logiciel_RH.repository.PosteRepository;
import com.example.logiciel_RH.service.EmployeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/rh/employes")
public class EmployeController {
    private final EmployeRepository employeRepository;
    private final ContratRepository contratRepository;
    private final PosteRepository posteRepository;
    private final DepartementRepository departementRepository;
    private final EmployeService employeService;

    public EmployeController(EmployeRepository employeRepository,
                             ContratRepository contratRepository,
                             PosteRepository posteRepository,
                             DepartementRepository departementRepository,
                             EmployeService employeService) {
        this.employeRepository = employeRepository;
        this.contratRepository = contratRepository;
        this.posteRepository = posteRepository;
        this.departementRepository = departementRepository;
        this.employeService = employeService;
    }

    @GetMapping
    public String liste(@RequestParam(required = false) String q,
                        @RequestParam(required = false) Long departementId,
                        @RequestParam(required = false) String statut,
                        HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employes", employeService.rechercher(q, departementId, statut));
        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("q", q);
        model.addAttribute("departementId", departementId);
        model.addAttribute("statutFiltre", statut);
        return "rh/employes";
    }

    @GetMapping("/nouveau")
    public String nouveau(HttpSession session, Model model) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        EmployeForm form = new EmployeForm();
        form.setDateEmbauche(LocalDate.now());
        form.setStatut("ACTIF");
        preparerFormulaire(model, session, form, null);
        return "rh/ajouterEmploye";
    }

    @PostMapping("/nouveau")
    public String creer(@ModelAttribute EmployeForm form,
                        @RequestParam(name = "photoFichier", required = false) MultipartFile photo,
                        HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Employe employe = employeService.creer(form, photo);
            redirectAttributes.addFlashAttribute("message",
                    "Dossier créé. Matricule : " + employe.getMatricule() + ".");
            return "redirect:/rh/employes/" + employe.getId();
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            preparerFormulaire(model, session, form, null);
            return "rh/ajouterEmploye";
        }
    }

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable("id") Long id, HttpSession session, Model model,
                           RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        model.addAttribute("employe", employe);
        preparerFormulaire(model, session, employeService.versFormulaire(employe), id);
        return "rh/modifierEmploye";
    }

    @PostMapping("/{id}/modifier")
    public String enregistrerModification(@PathVariable("id") Long id, @ModelAttribute EmployeForm form,
                                          @RequestParam(name = "photoFichier", required = false) MultipartFile photo,
                                          HttpSession session, Model model,
                                          RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            employeService.modifier(id, form, photo);
            redirectAttributes.addFlashAttribute("message", "Dossier mis à jour.");
            return "redirect:/rh/employes/" + id;
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employe", employeRepository.findById(id).orElse(null));
            preparerFormulaire(model, session, form, id);
            return "rh/modifierEmploye";
        }
    }

    @GetMapping("/{id}/documents")
    public String documents(@PathVariable("id") Long id, HttpSession session, Model model,
                            RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        model.addAttribute("documents", employeService.documents(id));
        model.addAttribute("typesDocument", employeService.typesDocument());
        return "rh/documentsEmploye";
    }

    @PostMapping("/{id}/documents")
    public String ajouterDocument(@PathVariable("id") Long id, @RequestParam Long typeDocumentId,
                                  @RequestParam(required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateExpiration,
                                  @RequestParam("fichier") MultipartFile fichier,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            employeService.ajouterDocument(id, typeDocumentId, dateExpiration, fichier,
                    SessionUtils.getUtilisateur(session));
            redirectAttributes.addFlashAttribute("message", "Document ajouté au dossier.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id + "/documents";
    }

    @PostMapping("/{id}/documents/{documentId}/supprimer")
    public String supprimerDocument(@PathVariable("id") Long id, @PathVariable("documentId") Long documentId,
                                    HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            employeService.supprimerDocument(id, documentId);
            redirectAttributes.addFlashAttribute("message", "Document supprimé.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id + "/documents";
    }

    /** Fichier trop volumineux : refusé par Spring avant d'atteindre les méthodes ci-dessus. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fichierTropGros(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "Fichier trop volumineux (10 Mo maximum).");
        return "redirect:/rh/employes";
    }

    private void preparerFormulaire(Model model, HttpSession session, EmployeForm form, Long exclureId) {
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("form", form);
        model.addAttribute("departements", departementRepository.findAll());
        model.addAttribute("postes", posteRepository.findAll());
        model.addAttribute("superieurs", employeRepository.findAllByOrderByDateCreationDesc().stream()
                .filter(e -> exclureId == null || !exclureId.equals(e.getId())).toList());
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, HttpSession session, Model model,
                         RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        Employe employe = employeRepository.findById(id).orElse(null);
        if (employe == null) {
            redirectAttributes.addFlashAttribute("error", "Employé introuvable.");
            return "redirect:/rh/employes";
        }
        model.addAttribute("utilisateurConnecte", SessionUtils.getUtilisateur(session));
        model.addAttribute("employe", employe);
        model.addAttribute("contrats", contratRepository.findByEmploye_IdOrderByDateDebutDesc(id));
        model.addAttribute("postes", posteRepository.findAll());
        model.addAttribute("typesContrat", Contrat.Type.values());
        model.addAttribute("nombreDocuments", employeService.documents(id).size());
        return "rh/detailEmploye";
    }

    @PostMapping("/{id}/statut")
    @Transactional
    public String modifierStatut(@PathVariable("id") Long id, @RequestParam String statut,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            Employe.Statut nouveauStatut = Employe.Statut.valueOf(statut);
            employe.setStatut(nouveauStatut);
            employe.setDateFin(nouveauStatut == Employe.Statut.SORTI ? LocalDate.now() : null);
            employeRepository.save(employe);
            redirectAttributes.addFlashAttribute("message", "Statut de l'employé mis à jour.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + id;
    }

    @PostMapping("/{id}/contrats")
    @Transactional
    public String creerContrat(@PathVariable("id") Long id, @RequestParam Long posteId,
                                @RequestParam String typeContrat, @RequestParam String dateDebut,
                                @RequestParam(required = false) String dateFin,
                                @RequestParam BigDecimal salaireMensuel,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Employe employe = employeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Employé introuvable."));
            Poste poste = posteRepository.findById(posteId)
                    .orElseThrow(() -> new IllegalArgumentException("Poste introuvable."));
            LocalDate debut = LocalDate.parse(dateDebut);
            LocalDate fin = dateFin == null || dateFin.isBlank() ? null : LocalDate.parse(dateFin);
            if (salaireMensuel == null || salaireMensuel.signum() < 0)
                throw new IllegalArgumentException("Le salaire doit être positif.");
            if (fin != null && fin.isBefore(debut))
                throw new IllegalArgumentException("La date de fin doit être postérieure à la date de début.");

            for (Contrat precedent : contratRepository.findByEmploye_IdAndStatut(id, Contrat.Statut.EN_COURS)) {
                precedent.setStatut(Contrat.Statut.TERMINE);
                precedent.setDateFin(debut.minusDays(1));
                contratRepository.save(precedent);
            }
            Contrat contrat = new Contrat();
            contrat.setEmploye(employe);
            contrat.setPoste(poste);
            contrat.setTypeContrat(Contrat.Type.valueOf(typeContrat));
            contrat.setDateDebut(debut);
            contrat.setDateFin(fin);
            contrat.setSalaireMensuel(salaireMensuel);
            contrat.setDevise("Ar");
            contrat.setStatut(Contrat.Statut.EN_COURS);
            contratRepository.save(contrat);
            employe.appliquerContrat(contrat);
            employe.setStatut(Employe.Statut.ACTIF);
            employe.setDateFin(null);
            employeRepository.save(employe);
            redirectAttributes.addFlashAttribute("message",
                    "Nouveau contrat créé. Le contrat précédent est terminé.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", "Contrat non créé : " + ex.getMessage());
        }
        return "redirect:/rh/employes/" + id;
    }

    @PostMapping("/{employeId}/contrats/{contratId}/terminer")
    @Transactional
    public String terminerContrat(@PathVariable("employeId") Long employeId, @PathVariable("contratId") Long contratId,
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (!SessionUtils.hasRole(session, "RH")) return "redirect:/login";
        try {
            Contrat contrat = contratRepository.findById(contratId)
                    .orElseThrow(() -> new IllegalArgumentException("Contrat introuvable."));
            if (contrat.getEmploye() == null || !employeId.equals(contrat.getEmploye().getId()))
                throw new IllegalArgumentException("Contrat non associé à cet employé.");
            contrat.setStatut(Contrat.Statut.TERMINE);
            contrat.setDateFin(LocalDate.now());
            contratRepository.save(contrat);
            redirectAttributes.addFlashAttribute("message", "Contrat terminé.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/rh/employes/" + employeId;
    }
}
