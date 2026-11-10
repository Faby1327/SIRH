package com.example.logiciel_RH.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.logiciel_RH.entity.DemandeConge;
public interface DemandeCongeRepository extends JpaRepository<DemandeConge,Long>{
 List<DemandeConge> findAllByOrderByDateCreationDesc();
 List<DemandeConge> findByEmploye_Personne_Utilisateur_IdUtilisateurOrderByDateCreationDesc(Long id);
 List<DemandeConge> findByEmploye_IdAndStatutInAndDateDebutLessThanEqualAndDateFinGreaterThanEqual(Long employeId,java.util.Collection<String> statuts,java.time.LocalDate fin,java.time.LocalDate debut);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select d from DemandeConge d where d.id=:id") java.util.Optional<DemandeConge> findLockedById(@Param("id") Long id);
 @Query("select d from DemandeConge d where exists (select c.id from Contrat c where c.employe=d.employe and c.statut=:statut and c.poste.departement.idDepartement=:dep) order by d.dateCreation desc") List<DemandeConge> pourDepartement(@Param("dep") Long dep,@Param("statut") com.example.logiciel_RH.entity.Contrat.Statut statut);
}
