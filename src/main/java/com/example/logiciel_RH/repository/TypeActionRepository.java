package com.example.logiciel_RH.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.logiciel_RH.entity.TypeAction;

public interface TypeActionRepository extends JpaRepository<TypeAction, Long> {
    Optional<TypeAction> findByCode(String code);
}
