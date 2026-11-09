package com.example.logiciel_RH.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.logiciel_RH.entity.JournalAudit;
import com.example.logiciel_RH.repository.JournalAuditRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class AuditConsultationService {
    public static final int LIMITE = 200;
    private final JournalAuditRepository repository;
    public AuditConsultationService(JournalAuditRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<JournalAudit> rechercher(String action, String entite, Long entiteId, String utilisateur,
                                         LocalDate du, LocalDate au, String texte) {
        Specification<JournalAudit> spec = (root, query, cb) -> {
            List<Predicate> filtres = new ArrayList<>();
            if (action != null && !action.isBlank()) filtres.add(cb.equal(root.get("action").get("code"), action));
            if (entite != null && !entite.isBlank()) filtres.add(cb.equal(root.get("typeEntite").get("code"), entite));
            if (entiteId != null) filtres.add(cb.equal(root.get("entiteId"), entiteId));
            if (utilisateur != null && !utilisateur.isBlank()) {
                var personne = root.join("utilisateur").join("personne");
                String motif = "%" + utilisateur.trim().toLowerCase() + "%";
                filtres.add(cb.or(cb.like(cb.lower(personne.get("nom")), motif), cb.like(cb.lower(personne.get("prenom")), motif), cb.like(cb.lower(personne.get("email")), motif)));
            }
            if (du != null) filtres.add(cb.greaterThanOrEqualTo(root.<LocalDateTime>get("dateAction"), du.atStartOfDay()));
            if (au != null) filtres.add(cb.lessThan(root.<LocalDateTime>get("dateAction"), au.plusDays(1).atStartOfDay()));
            if (texte != null && !texte.isBlank()) {
                String motif = "%" + texte.trim().toLowerCase() + "%";
                filtres.add(cb.or(cb.like(cb.lower(root.<String>get("anciennesValeurs")), motif), cb.like(cb.lower(root.<String>get("nouvellesValeurs")), motif)));
            }
            return cb.and(filtres.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, PageRequest.of(0, LIMITE, Sort.by(Sort.Direction.DESC, "dateAction", "id"))).getContent();
    }
}
