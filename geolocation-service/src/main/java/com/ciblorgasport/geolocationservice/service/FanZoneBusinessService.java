package com.ciblorgasport.geolocationservice.service;

import com.ciblorgasport.geolocationservice.dto.FanZoneRequest;
import com.ciblorgasport.geolocationservice.dto.FanZoneResponse;
import com.ciblorgasport.geolocationservice.entity.FanZone;
import com.ciblorgasport.geolocationservice.entity.FanZoneService;
import com.ciblorgasport.geolocationservice.entity.enums.TypeService;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.repository.FanZoneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FanZoneBusinessService {

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private final FanZoneRepository fanZoneRepository;

    public FanZoneBusinessService(FanZoneRepository fanZoneRepository) {
        this.fanZoneRepository = fanZoneRepository;
    }

    @Transactional
    public FanZoneResponse create(FanZoneRequest request) {
        FanZone fanZone = new FanZone();
        fanZone.setNom(request.getNom());
        fanZone.setDescription(request.getDescription());
        fanZone.setLatitude(request.getLatitude());
        fanZone.setLongitude(request.getLongitude());
        fanZone.setCapaciteMax(request.getCapaciteMax());
        fanZone.setAdresse(request.getAdresse());

        if (request.getServices() != null) {
            for (TypeService type : request.getServices()) {
                FanZoneService service = new FanZoneService(fanZone, type);
                fanZone.getServices().add(service);
            }
        }

        FanZone saved = fanZoneRepository.save(fanZone);
        return toResponse(saved, null);
    }

    @Transactional(readOnly = true)
    public List<FanZoneResponse> findAll() {
        return fanZoneRepository.findAll()
                .stream()
                .map(fz -> toResponse(fz, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FanZoneResponse> findNearby(double lat, double lng, double rayonMetres) {
        return fanZoneRepository.findAll()
                .stream()
                .map(fz -> {
                    double dist = haversine(lat, lng, fz.getLatitude(), fz.getLongitude());
                    return toResponse(fz, dist);
                })
                .filter(fzr -> fzr.getDistance() <= rayonMetres)
                .sorted(Comparator.comparingDouble(FanZoneResponse::getDistance))
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long fanzoneId) {
        if (!fanZoneRepository.existsById(fanzoneId)) {
            throw new ResourceNotFoundException("Fan zone introuvable id=" + fanzoneId);
        }
        fanZoneRepository.deleteById(fanzoneId);
    }

    double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_METERS * c;
    }

    private FanZoneResponse toResponse(FanZone fz, Double distance) {
        FanZoneResponse response = new FanZoneResponse();
        response.setId(fz.getId());
        response.setNom(fz.getNom());
        response.setDescription(fz.getDescription());
        response.setLatitude(fz.getLatitude());
        response.setLongitude(fz.getLongitude());
        response.setCapaciteMax(fz.getCapaciteMax());
        response.setAdresse(fz.getAdresse());
        response.setServices(fz.getServices().stream()
                .map(FanZoneService::getTypeService)
                .collect(Collectors.toList()));
        response.setDistance(distance);
        return response;
    }
}
