package com.ciblorgasport.geolocationservice.repository;

import com.ciblorgasport.geolocationservice.entity.AthleteGeoConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AthleteGeoConfigRepository extends JpaRepository<AthleteGeoConfig, Long> {
}
