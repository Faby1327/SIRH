package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.logiciel_RH.entity.TypeDocument;

public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Long> {

    List<TypeDocument> findAllByOrderByLibelleAsc();
}
