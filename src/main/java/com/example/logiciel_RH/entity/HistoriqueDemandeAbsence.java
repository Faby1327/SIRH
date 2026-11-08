package com.example.logiciel_RH.entity;
import java.time.LocalDateTime;
import jakarta.persistence.*;
@Entity @Table(name="historique_demande_absence")
public class HistoriqueDemandeAbsence {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="demande_id") private DemandeAbsence demande;
 @ManyToOne(optional=false) @JoinColumn(name="acteur_id") private Utilisateur acteur;
 @Column(nullable=false) private String action;
 @Column(columnDefinition="text") private String commentaire;
 @Column(nullable=false,name="date_action") private LocalDateTime dateAction=LocalDateTime.now();
 public HistoriqueDemandeAbsence(){} public HistoriqueDemandeAbsence(DemandeAbsence d,Utilisateur u,String a,String c){demande=d;acteur=u;action=a;commentaire=c;}
 public Long getId(){return id;} public String getAction(){return action;} public String getCommentaire(){return commentaire;} public LocalDateTime getDateAction(){return dateAction;} public Utilisateur getActeur(){return acteur;}
}
