package com.example.logiciel_RH.repository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.logiciel_RH.entity.Employe;
import com.example.logiciel_RH.entity.Contrat;
public interface EmployeBloc7Repository extends JpaRepository<Employe,Long>{
 Optional<Employe> findByPersonne_IdPersonne(Long personneId);
 @Query("select distinct e from Employe e join Contrat c on c.employe=e where c.statut=:statut and c.poste.departement.idDepartement=:dep") List<Employe> duDepartement(@Param("dep") Long dep,@Param("statut") Contrat.Statut statut);
 @Query("select distinct e from Employe e join Contrat c on c.employe=e where c.statut=:statut and c.poste.idPoste=:poste and c.poste.departement.idDepartement=:dep") List<Employe> duPoste(@Param("dep") Long dep,@Param("poste") Long poste,@Param("statut") Contrat.Statut statut);
 @Query("select count(e)>0 from Employe e join Contrat c on c.employe=e where e.id=:id and c.statut=:statut and c.poste.departement.idDepartement=:dep") boolean appartientAuDepartement(@Param("id") Long id,@Param("dep") Long dep,@Param("statut") Contrat.Statut statut);
}
