package com.ciblorgasport.geolocationservice.entity;

import com.ciblorgasport.geolocationservice.entity.enums.TypeService;
import jakarta.persistence.*;

@Entity
@Table(name = "fan_zone_service")
public class FanZoneService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fan_zone_id", nullable = false)
    private FanZone fanZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_service", nullable = false)
    private TypeService typeService;

    public FanZoneService() {}

    public FanZoneService(FanZone fanZone, TypeService typeService) {
        this.fanZone = fanZone;
        this.typeService = typeService;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FanZone getFanZone() { return fanZone; }
    public void setFanZone(FanZone fanZone) { this.fanZone = fanZone; }

    public TypeService getTypeService() { return typeService; }
    public void setTypeService(TypeService typeService) { this.typeService = typeService; }
}
