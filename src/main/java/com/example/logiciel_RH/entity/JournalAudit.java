package com.example.logiciel_RH.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "journal_audit")
public class JournalAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "utilisateur_id", nullable = false) private Utilisateur utilisateur;
    @ManyToOne(optional = false) @JoinColumn(name = "action_id", nullable = false) private TypeAction action;
    @Column(name = "entite_id", nullable = false) private Long entiteId;
    @ManyToOne(optional = false) @JoinColumn(name = "type_entite_id", nullable = false) private TypeEntite typeEntite;
    @Column(name = "date_action", nullable = false) private LocalDateTime dateAction;
    @Column(name = "anciennes_valeurs") private String anciennesValeurs;
    @Column(name = "nouvelles_valeurs") private String nouvellesValeurs;
    @PrePersist void avantInsertion() { if (dateAction == null) dateAction = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Utilisateur getUtilisateur() { return utilisateur; } public void setUtilisateur(Utilisateur v) { utilisateur = v; }
    public TypeAction getAction() { return action; } public void setAction(TypeAction v) { action = v; }
    public Long getEntiteId() { return entiteId; } public void setEntiteId(Long v) { entiteId = v; }
    public TypeEntite getTypeEntite() { return typeEntite; } public void setTypeEntite(TypeEntite v) { typeEntite = v; }
    public LocalDateTime getDateAction() { return dateAction; } public void setDateAction(LocalDateTime v) { dateAction = v; }
    public String getAnciennesValeurs() { return anciennesValeurs; } public void setAnciennesValeurs(String v) { anciennesValeurs = v; }
    public String getNouvellesValeurs() { return nouvellesValeurs; } public void setNouvellesValeurs(String v) { nouvellesValeurs = v; }
}
