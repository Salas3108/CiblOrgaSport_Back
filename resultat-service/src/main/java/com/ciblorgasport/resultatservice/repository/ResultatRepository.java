package com.ciblorgasport.resultatservice.repository;

import com.ciblorgasport.resultatservice.entity.Resultat;
import com.ciblorgasport.resultatservice.entity.StatusResultat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    
    List<Resultat> findByEpreuveId(Long epreuveId);
    
    List<Resultat> findByEpreuveIdAndStatus(Long epreuveId, StatusResultat status);
    
    List<Resultat> findByAthleteId(Long athleteId);
    
    Optional<Resultat> findByEpreuveIdAndAthleteId(Long epreuveId, Long athleteId);
    
    List<Resultat> findBySaisieParId(Long saisieParId);
    
    List<Resultat> findByStatus(StatusResultat status);
    
    @Query("SELECT r FROM Resultat r WHERE r.epreuveId = :epreuveId ORDER BY r.classement ASC")
    List<Resultat> findByEpreuveIdOrderedByClassement(@Param("epreuveId") Long epreuveId);
    
    @Query("SELECT COUNT(r) FROM Resultat r WHERE r.epreuveId = :epreuveId AND r.status = :status")
    Long countByEpreuveIdAndStatus(@Param("epreuveId") Long epreuveId, @Param("status") StatusResultat status);
}
