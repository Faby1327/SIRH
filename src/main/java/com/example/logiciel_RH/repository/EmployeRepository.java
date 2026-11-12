package com.example.logiciel_RH.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.Employe;

public interface EmployeRepository extends JpaRepository<Employe, Long> {

    java.util.List<Employe> findAllByOrderByDateCreationDesc();

    @Query("select distinct e from Employe e join fetch e.personne left join e.departement d left join e.poste p "
         + "where e.statut = com.example.logiciel_RH.entity.Employe.Statut.ACTIF and e.id <> :employeId and "
         + "(d.idDepartement = :departementId or p.departement.idDepartement = :departementId or "
         + "exists (select c.id from Contrat c where c.employe = e and c.statut = com.example.logiciel_RH.entity.Contrat.Statut.EN_COURS and c.poste.departement.idDepartement = :departementId)) "
         + "order by e.personne.nom")
    java.util.List<Employe> remplaçantsDuDépartement(@Param("departementId") Long departementId, @Param("employeId") Long employeId);

    @Query("select e from Employe e join fetch e.personne p "
         + "left join fetch e.departement d "
         + "where (:q is null or :q = '' or "
         + "lower(e.matricule) like lower(concat('%', :q, '%')) or "
         + "lower(p.nom) like lower(concat('%', :q, '%')) or "
         + "lower(p.prenom) like lower(concat('%', :q, '%')) or "
         + "lower(coalesce(p.email, '')) like lower(concat('%', :q, '%'))) "
         + "and (:departementId is null or d.idDepartement = :departementId) "
         + "and (:statut is null or e.statut = :statut) "
         + "order by e.dateCreation desc")
    java.util.List<Employe> rechercher(@Param("q") String q,
                                       @Param("departementId") Long departementId,
                                       @Param("statut") Employe.Statut statut);

    Optional<Employe> findByPersonne_IdPersonne(Long idPersonne);

    Optional<Employe> findByMatricule(String matricule);

    @Query(value = "select nextval('employe_matricule_seq')", nativeQuery = true)
    Long prochainNumeroMatricule();

    @Query(value = "select coalesce(max(cast(substring(matricule from 5) as integer)), 0) + 1 "
                 + "from employe where matricule like 'EMP-%'", nativeQuery = true)
    Long prochainNumeroMatriculeDisponible();
}
