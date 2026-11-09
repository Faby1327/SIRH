package com.example.logiciel_RH.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_action")
public class TypeAction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String code;
    @Column(nullable = false) private String libelle;
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getCode() { return code; } public void setCode(String code) { this.code = code; }
    public String getLibelle() { return libelle; } public void setLibelle(String libelle) { this.libelle = libelle; }
}
