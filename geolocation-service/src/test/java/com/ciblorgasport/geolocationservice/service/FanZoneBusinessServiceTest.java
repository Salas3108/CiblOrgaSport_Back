package com.ciblorgasport.geolocationservice.service;

import com.ciblorgasport.geolocationservice.dto.FanZoneRequest;
import com.ciblorgasport.geolocationservice.dto.FanZoneResponse;
import com.ciblorgasport.geolocationservice.entity.FanZone;
import com.ciblorgasport.geolocationservice.entity.enums.TypeService;
import com.ciblorgasport.geolocationservice.exception.ResourceNotFoundException;
import com.ciblorgasport.geolocationservice.repository.FanZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FanZoneBusinessServiceTest {

    @Mock
    private FanZoneRepository fanZoneRepository;

    @InjectMocks
    private FanZoneBusinessService service;

    @Test
    void haversine_samePoint_returnsZero() {
        double distance = service.haversine(48.8566, 2.3522, 48.8566, 2.3522);
        assertThat(distance).isEqualTo(0.0);
    }

    @Test
    void haversine_knownDistance_isApproximatelyCorrect() {
        // Paris → Lyon ~ 392 km
        double distance = service.haversine(48.8566, 2.3522, 45.7640, 4.8357);
        assertThat(distance).isBetween(390_000.0, 395_000.0);
    }

    @Test
    void create_savesAndReturnsFanZone() {
        FanZoneRequest req = new FanZoneRequest();
        req.setNom("Zone A");
        req.setLatitude(48.8566);
        req.setLongitude(2.3522);
        req.setServices(List.of(TypeService.ECRAN_GEANT, TypeService.RESTAURATION));

        FanZone saved = new FanZone();
        saved.setId(1L);
        saved.setNom("Zone A");
        saved.setLatitude(48.8566);
        saved.setLongitude(2.3522);
        when(fanZoneRepository.save(any())).thenReturn(saved);

        FanZoneResponse response = service.create(req);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNom()).isEqualTo("Zone A");
        verify(fanZoneRepository).save(any(FanZone.class));
    }

    @Test
    void findNearby_filtersAndSortsByDistance() {
        FanZone close = fanZone(1L, "Proche", 48.8566, 2.3522);
        FanZone far = fanZone(2L, "Lointaine", 48.9000, 2.4000);
        when(fanZoneRepository.findAll()).thenReturn(List.of(far, close));

        // Rayon 500 m autour du Stade
        List<FanZoneResponse> result = service.findNearby(48.8566, 2.3522, 500);

        // Seule la fan zone proche est dans les 500m (distance = 0)
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Proche");
        assertThat(result.get(0).getDistance()).isEqualTo(0.0);
    }

    @Test
    void findNearby_allInRadius_sortedByDistanceAscending() {
        // Point de référence : 48.8566, 2.3522
        FanZone z1 = fanZone(1L, "Z1", 48.8570, 2.3525); // très proche
        FanZone z2 = fanZone(2L, "Z2", 48.8580, 2.3530); // un peu plus loin
        when(fanZoneRepository.findAll()).thenReturn(List.of(z2, z1));

        List<FanZoneResponse> result = service.findNearby(48.8566, 2.3522, 5000);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNom()).isEqualTo("Z1");
        assertThat(result.get(0).getDistance()).isLessThan(result.get(1).getDistance());
    }

    @Test
    void findNearby_noneInRadius_returnsEmptyList() {
        FanZone far = fanZone(1L, "Lointaine", 50.0, 5.0);
        when(fanZoneRepository.findAll()).thenReturn(List.of(far));

        List<FanZoneResponse> result = service.findNearby(48.8566, 2.3522, 100);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_existingFanZone_callsRepository() {
        when(fanZoneRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(fanZoneRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsException() {
        when(fanZoneRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private FanZone fanZone(Long id, String nom, double lat, double lng) {
        FanZone fz = new FanZone();
        fz.setId(id);
        fz.setNom(nom);
        fz.setLatitude(lat);
        fz.setLongitude(lng);
        return fz;
    }
}
