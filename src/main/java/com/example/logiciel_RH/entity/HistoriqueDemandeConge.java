package com.example.logiciel_RH.entity;
import java.time.LocalDateTime;
import jakarta.persistence.*;
@Entity @Table(name="historique_demande_conge")
public class HistoriqueDemandeConge {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="demande_id") private DemandeConge demande;
 @ManyToOne(optional=false) @JoinColumn(name="validateur_id") private Utilisateur validateur;
 @Column(nullable=false) private String etape;
 @Column(nullable=false) private String decision;
 @Column(columnDefinition="text") private String commentaire;
 @Column(nullable=false,name="date_action") private LocalDateTime dateAction=LocalDateTime.now();
 public HistoriqueDemandeConge(){} public HistoriqueDemandeConge(DemandeConge d,Utilisateur u,String e,String dec,String c){demande=d;validateur=u;etape=e;decision=dec;commentaire=c;}
 public Long getId(){return id;} public DemandeConge getDemande(){return demande;} public Utilisateur getValidateur(){return validateur;} public String getEtape(){return etape;} public String getDecision(){return decision;} public String getCommentaire(){return commentaire;} public LocalDateTime getDateAction(){return dateAction;}
}
