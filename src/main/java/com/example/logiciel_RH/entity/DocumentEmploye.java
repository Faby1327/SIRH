package com.example.logiciel_RH.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name = "document_employe")
public class DocumentEmploye {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "employe_id", nullable = false) private Employe employe;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "type_document_id", nullable = false) private TypeDocument typeDocument;
    @Column(name = "nom_fichier", nullable = false) private String nomFichier;
    @Column(name = "chemin_fichier", nullable = false) private String cheminFichier;
    @Column(name = "date_ajout", nullable = false) private LocalDateTime dateAjout;
    @Column(name = "date_expiration") private LocalDate dateExpiration;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "utilisateur_id") private Utilisateur utilisateur;
    public Long getId(){return id;} public void setId(Long v){id=v;} public Employe getEmploye(){return employe;} public void setEmploye(Employe v){employe=v;}
    public TypeDocument getTypeDocument(){return typeDocument;} public void setTypeDocument(TypeDocument v){typeDocument=v;} public String getNomFichier(){return nomFichier;} public void setNomFichier(String v){nomFichier=v;}
    public String getCheminFichier(){return cheminFichier;} public void setCheminFichier(String v){cheminFichier=v;} public LocalDateTime getDateAjout(){return dateAjout;} public void setDateAjout(LocalDateTime v){dateAjout=v;}
    public LocalDate getDateExpiration(){return dateExpiration;} public void setDateExpiration(LocalDate v){dateExpiration=v;} public Utilisateur getUtilisateur(){return utilisateur;} public void setUtilisateur(Utilisateur v){utilisateur=v;}
    public boolean isExpire(){return dateExpiration != null && dateExpiration.isBefore(LocalDate.now());}
}
