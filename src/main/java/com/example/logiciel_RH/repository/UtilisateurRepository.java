package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Utilisateur;

public interface UtilisateurRepository
        extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByPersonne_Email(String email);

    Optional<Utilisateur> findByPersonne_EmailIgnoreCase(String email);

    boolean existsByPersonne_EmailIgnoreCase(String email);

    /** Identifiant du rôle, pour créer un compte candidat. */
    @Query(value = "select id from role where nom = :nom", nativeQuery = true)
    Long findRoleIdByNom(@Param("nom") String nom);

    /** Premier compte actif d'un rôle donné (ex. contact RH affiché sur les offres). */
    @Query(value = "select p.email from utilisateur u "
            + "join personne p on p.id = u.personne_id "
            + "join role r on r.id = u.role_id "
            + "where r.nom = :role and u.actif = true "
            + "order by u.id limit 1", nativeQuery = true)
    String findEmailByRole(@Param("role") String role);
}
