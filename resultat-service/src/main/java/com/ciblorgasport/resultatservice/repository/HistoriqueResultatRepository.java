package com.ciblorgasport.resultatservice.repository;

import com.ciblorgasport.resultatservice.entity.HistoriqueResultat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueResultatRepository extends JpaRepository<HistoriqueResultat, Long> {
    
    List<HistoriqueResultat> findByResultatId(Long resultatId);
    
    List<HistoriqueResultat> findByModifiePar(Long modifiePar);
}
