package com.example.logiciel_RH.repository;

import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.example.logiciel_RH.entity.SoldeConge;

public interface SoldeCongeRepository extends JpaRepository<SoldeConge, Long> {

    @Query("select coalesce(sum(s.joursRestants),0) from SoldeConge s where s.employe.id=:id")
    long totalRestant(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SoldeConge s where s.employe.id=:id and s.joursRestants>0 order by s.dateAcquisition asc,s.id asc")
    List<SoldeConge> lotsFifo(@Param("id") Long id);
}
