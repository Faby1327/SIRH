package com.example.logiciel_RH.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
@Entity @Table(name="solde_conge")
public class SoldeConge {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="employe_id") private Employe employe;
 @Column(name="date_acquisition",nullable=false) private LocalDate dateAcquisition;
 @Column(name="jours_restants",nullable=false) private int joursRestants;
 public Long getId(){return id;} public LocalDate getDateAcquisition(){return dateAcquisition;} public int getJoursRestants(){return joursRestants;} public void setJoursRestants(int v){joursRestants=v;}
}
