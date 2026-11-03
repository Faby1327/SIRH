package com.example.logiciel_RH.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.logiciel_RH.entity.Poste;
import com.example.logiciel_RH.repository.PosteRepository;

@Service
public class PosteService {

    private final PosteRepository repository;

    public PosteService(PosteRepository repository) {
        this.repository = repository;
    }

    public List<Poste> findAll() {
        return repository.findAll();
    }

    public List<Poste> findByDepartement(Long idDepartement) {
        return repository.findByDepartement_IdDepartement(idDepartement);
    }

    public Poste getById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
