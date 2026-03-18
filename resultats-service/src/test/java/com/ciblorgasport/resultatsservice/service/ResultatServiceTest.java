package com.ciblorgasport.resultatsservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.model.TypePerformance;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;

class ResultatServiceTest {

    private ResultatRepository resultatRepository;
    private ResultatService resultatService;
    private Map<Long, Resultat> fakeStore;

    @BeforeEach
    void setUp() {
        fakeStore = new HashMap<>();
        resultatRepository = mock(ResultatRepository.class);
        resultatService = new ResultatService(resultatRepository);

        when(resultatRepository.save(any(Resultat.class))).thenAnswer(inv -> {
            Resultat r = inv.getArgument(0);
            if (r.getId() == null) {
                r.setId((long) (fakeStore.size() + 1));
            }
            fakeStore.put(r.getId(), r);
            return r;
        });

        when(resultatRepository.findById(any(Long.class))).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return Optional.ofNullable(fakeStore.get(id));
        });
    }

    @Test
    void createOrUpdate_creates_new_resultat_with_defaults() {
        when(resultatRepository.findByEpreuveIdAndAthleteId(eq(10L), eq(5L))).thenReturn(Optional.empty());

        ResultatRequest request = new ResultatRequest();
        request.setEpreuveId(10L);
        request.setAthleteId(5L);
        request.setClassement(1);
        request.setMedaille(Medaille.OR);
        request.setQualification(true);
        request.setValeurPrincipale("47.21");
        request.setUnite("secondes");
        request.setTypePerformance(TypePerformance.TEMPS);
        request.setDetailsPerformance(Map.of("vent", "1.3"));

        Resultat created = resultatService.createOrUpdate(request);

        assertNotNull(created.getId());
        assertEquals(10L, created.getEpreuveId());
        assertEquals(5L, created.getAthleteId());
        assertEquals(ResultatStatut.EN_ATTENTE, created.getStatut());
        assertEquals(false, created.isPublished());
        assertEquals("47.21", created.getValeurPrincipale());
    }

    @Test
    void createOrUpdate_updates_existing_resultat_without_overwriting_missing_fields() {
        Resultat existing = new Resultat();
        existing.setId(1L);
        existing.setEpreuveId(10L);
        existing.setAthleteId(5L);
        existing.setValeurPrincipale("48.00");
        existing.setUnite("secondes");
        fakeStore.put(1L, existing);

        when(resultatRepository.findByEpreuveIdAndAthleteId(eq(10L), eq(5L)))
                .thenReturn(Optional.of(existing));

        ResultatRequest request = new ResultatRequest();
        request.setEpreuveId(10L);
        request.setAthleteId(5L);
        request.setClassement(2);

        Resultat updated = resultatService.createOrUpdate(request);

        assertEquals(2, updated.getClassement());
        assertEquals("48.00", updated.getValeurPrincipale());
        assertEquals("secondes", updated.getUnite());
    }

    @Test
    void createOrUpdate_requires_participant() {
        ResultatRequest request = new ResultatRequest();
        request.setEpreuveId(10L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                resultatService.createOrUpdate(request));
        assertEquals("athleteId ou equipeId est obligatoire", ex.getMessage());
    }

    @Test
    void createOrUpdate_rejects_both_athlete_and_equipe() {
        ResultatRequest request = new ResultatRequest();
        request.setEpreuveId(10L);
        request.setAthleteId(5L);
        request.setEquipeId(3L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                resultatService.createOrUpdate(request));
        assertEquals("athleteId et equipeId sont mutuellement exclusifs", ex.getMessage());
    }

    @Test
    void validateResultat_sets_status_valide() {
        Resultat existing = new Resultat();
        existing.setId(1L);
        existing.setStatut(ResultatStatut.EN_ATTENTE);
        fakeStore.put(1L, existing);

        Resultat validated = resultatService.validateResultat(1L);

        assertEquals(ResultatStatut.VALIDE, validated.getStatut());
    }

    @Test
    void publishResultat_requires_valide() {
        Resultat existing = new Resultat();
        existing.setId(1L);
        existing.setStatut(ResultatStatut.EN_ATTENTE);
        fakeStore.put(1L, existing);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                resultatService.publishResultat(1L));
        assertEquals("resultat doit etre VALIDE avant publication", ex.getMessage());
    }

    @Test
    void publishResultat_sets_published_true() {
        Resultat existing = new Resultat();
        existing.setId(1L);
        existing.setStatut(ResultatStatut.VALIDE);
        fakeStore.put(1L, existing);

        Resultat published = resultatService.publishResultat(1L);

        assertEquals(true, published.isPublished());
    }

    @Test
    void getClassementEpreuve_sorts_by_classement() {
        Resultat r1 = new Resultat();
        r1.setId(1L);
        r1.setClassement(2);
        Resultat r2 = new Resultat();
        r2.setId(2L);
        r2.setClassement(1);
        Resultat r3 = new Resultat();
        r3.setId(3L);
        r3.setClassement(null);

        when(resultatRepository.findByEpreuveId(eq(10L)))
                .thenReturn(new ArrayList<>(List.of(r1, r2, r3)));

        List<Resultat> sorted = resultatService.getClassementEpreuve(10L, false);

        assertEquals(2, sorted.get(0).getId());
        assertEquals(1, sorted.get(1).getId());
        assertEquals(3, sorted.get(2).getId());
    }

    @Test
    void getResultatsAthlete_published_only_uses_filtered_repository() {
        when(resultatRepository.findByAthleteIdAndPublishedTrue(eq(9L)))
                .thenReturn(new ArrayList<>());

        resultatService.getResultatsAthlete(9L, true);

        verify(resultatRepository).findByAthleteIdAndPublishedTrue(9L);
    }
}
