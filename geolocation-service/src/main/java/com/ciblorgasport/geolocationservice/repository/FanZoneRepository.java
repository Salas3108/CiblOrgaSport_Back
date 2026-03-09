package com.ciblorgasport.geolocationservice.repository;

import com.ciblorgasport.geolocationservice.entity.FanZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FanZoneRepository extends JpaRepository<FanZone, Long> {
}
