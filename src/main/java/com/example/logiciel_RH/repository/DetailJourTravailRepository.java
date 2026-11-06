package com.example.logiciel_RH.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.logiciel_RH.entity.DetailJourTravail;

public interface DetailJourTravailRepository extends JpaRepository<DetailJourTravail, Long> {
    List<DetailJourTravail> findByJourTravail_IdOrderByHeureDebutAsc(Long jourTravailId);

    @Query("select coalesce(sum(d.nombreMinutes), 0) "
         + "from DetailJourTravail d "
         + "where d.participationMission.id = :participationId "
         + "and d.typeDetail = :typeDetail")
    Integer totalMinutesMission(@Param("participationId") Long participationId,
                                @Param("typeDetail") DetailJourTravail.TypeDetail typeDetail);
}
