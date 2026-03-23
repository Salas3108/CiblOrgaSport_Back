package com.ciblorgasport.resultatsservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

import com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.request.BulkResultatRequest;
import com.ciblorgasport.resultatsservice.dto.request.PerformanceEntryDto;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.model.Medaille;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.model.ResultatStatut;
import com.ciblorgasport.resultatsservice.model.TypePerformance;
import com.ciblorgasport.resultatsservice.repository.ResultatRepository;
import com.ciblorgasport.resultatsservice.service.calcul.ClassementService;

class ResultatServiceTest {

    private ResultatRepository resultatRepository;
    private ClassementService classementService;
    private ParticipantsServiceClient participantsClient;
    private ResultatService resultatService;
    private Map<Long, Resultat> fakeStore;

    @BeforeEach
    void setUp() {
        fakeStore = new HashMap<>();
        resultatRepository = mock(ResultatRepository.class);
        classementService = mock(ClassementService.class);
        participantsClient = mock(ParticipantsServiceClient.class);
        resultatService = new ResultatService(resultatRepository, classementService, participantsClient);

        // Par défaut : athlète non en forfait
        when(participantsClient.getStatutParticipation(any(Long.class), any(Long.class))).thenReturn(null);

        when(resultatRepository.save(any())).thenAnswer(inv -> {
            Resultat r = inv.getArgument(0);
            if (r.getId() == null) r.setId((long) (fakeStore.size() + 1));
            fakeStore.put(r.getId(), r);
            return r;
        });

        when(resultatRepository.findById(any(Long.class))).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return Optional.ofNullable(fakeStore.get(id));
        });

        when(resultatRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
    }

    // ── createOrUpdate ────────────────────────────────────────────────────────

    @Test
    void createOrUpdate_creates_new_resultat_with_defaults() {
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 5L)).thenReturn(Optional.empty());

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

        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 5L))
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

    // ── validateResultat / publishResultat ───────────────────────────────────

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

    // ── getClassementEpreuve ─────────────────────────────────────────────────

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

        when(resultatRepository.findByEpreuveId(10L))
                .thenReturn(new ArrayList<>(List.of(r1, r2, r3)));

        List<Resultat> sorted = resultatService.getClassementEpreuve(10L, false);

        assertEquals(2L, sorted.get(0).getId());
        assertEquals(1L, sorted.get(1).getId());
        assertEquals(3L, sorted.get(2).getId());
    }

    @Test
    void getResultatsAthlete_published_only_uses_filtered_repository() {
        when(resultatRepository.findByAthleteIdAndPublishedTrue(9L))
                .thenReturn(new ArrayList<>());

        resultatService.getResultatsAthlete(9L, true);

        verify(resultatRepository).findByAthleteIdAndPublishedTrue(9L);
    }

    // ── saisirBulk ───────────────────────────────────────────────────────────

    @Test
    void saisirBulk_persists_each_entry_and_triggers_classement() {
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 1L)).thenReturn(Optional.empty());
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 2L)).thenReturn(Optional.empty());
        when(resultatRepository.findByEpreuveId(10L)).thenReturn(new ArrayList<>());

        EpreuveContextDto ctx = new EpreuveContextDto();
        ctx.setId(10L);
        ctx.setDiscipline("NATATION");
        ctx.setNiveauEpreuve("FINALE");

        PerformanceEntryDto e1 = new PerformanceEntryDto();
        e1.setAthleteId(1L);
        e1.setValeurPrincipale("49.95");

        PerformanceEntryDto e2 = new PerformanceEntryDto();
        e2.setAthleteId(2L);
        e2.setValeurPrincipale("50.12");

        BulkResultatRequest request = new BulkResultatRequest();
        request.setPerformances(List.of(e1, e2));

        resultatService.saisirBulk(10L, request, ctx);

        verify(resultatRepository).findByEpreuveIdAndAthleteId(10L, 1L);
        verify(resultatRepository).findByEpreuveIdAndAthleteId(10L, 2L);
        verify(classementService).calculerClassementAvecContexte(10L, ctx);
        verify(resultatRepository).findByEpreuveId(10L);
    }

    @Test
    void saisirBulk_assigns_score_type_for_water_polo() {
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 1L)).thenReturn(Optional.empty());
        when(resultatRepository.findByEpreuveId(10L)).thenReturn(new ArrayList<>());

        EpreuveContextDto ctx = new EpreuveContextDto();
        ctx.setId(10L);
        ctx.setDiscipline("WATER_POLO");
        ctx.setNiveauEpreuve("FINALE");

        PerformanceEntryDto e1 = new PerformanceEntryDto();
        e1.setAthleteId(1L);
        e1.setValeurPrincipale("12-8");

        BulkResultatRequest request = new BulkResultatRequest();
        request.setPerformances(List.of(e1));

        resultatService.saisirBulk(10L, request, ctx);

        verify(resultatRepository).save(any());
    }

    // ── validerTout ──────────────────────────────────────────────────────────

    @Test
    void validerTout_changes_all_en_attente_to_valide() {
        Resultat r1 = new Resultat();
        r1.setId(1L);
        r1.setStatut(ResultatStatut.EN_ATTENTE);
        r1.setEpreuveId(10L);

        Resultat r2 = new Resultat();
        r2.setId(2L);
        r2.setStatut(ResultatStatut.VALIDE);
        r2.setEpreuveId(10L);

        when(resultatRepository.findByEpreuveId(10L))
                .thenReturn(new ArrayList<>(List.of(r1, r2)));

        resultatService.validerTout(10L);

        verify(resultatRepository).saveAll(List.of(r1));
        assertEquals(ResultatStatut.VALIDE, r1.getStatut());
    }

    @Test
    void validerTout_returns_empty_when_nothing_en_attente() {
        Resultat r1 = new Resultat();
        r1.setId(1L);
        r1.setStatut(ResultatStatut.VALIDE);
        r1.setEpreuveId(10L);

        when(resultatRepository.findByEpreuveId(10L))
                .thenReturn(new ArrayList<>(List.of(r1)));

        List<Resultat> result = resultatService.validerTout(10L);

        verify(resultatRepository).saveAll(List.of());
        assertEquals(0, result.size());
    }

    // ── modifier epreuve (forfait) ────────────────────────────────────────────

    @Test
    void createOrUpdate_rejects_athlete_en_forfait() {
        when(participantsClient.getStatutParticipation(10L, 5L)).thenReturn("FORFAIT");

        ResultatRequest request = new ResultatRequest();
        request.setEpreuveId(10L);
        request.setAthleteId(5L);
        request.setValeurPrincipale("47.21");

        assertThrows(IllegalStateException.class, () -> resultatService.createOrUpdate(request));
    }

    @Test
    void saisirBulk_creates_forfait_resultat_for_forfait_athlete() {
        when(participantsClient.getStatutParticipation(10L, 1L)).thenReturn("FORFAIT");
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 1L)).thenReturn(Optional.empty());
        when(resultatRepository.findByEpreuveId(10L)).thenReturn(new ArrayList<>());

        EpreuveContextDto ctx = new EpreuveContextDto();
        ctx.setId(10L);
        ctx.setDiscipline("NATATION");
        ctx.setNiveauEpreuve("FINALE");

        PerformanceEntryDto e1 = new PerformanceEntryDto();
        e1.setAthleteId(1L);
        e1.setValeurPrincipale("47.21");

        BulkResultatRequest request = new BulkResultatRequest();
        request.setPerformances(List.of(e1));

        resultatService.saisirBulk(10L, request, ctx);

        verify(resultatRepository).save(any());
    }

    @Test
    void saisirBulk_skips_existing_forfait_resultat_for_forfait_athlete() {
        when(participantsClient.getStatutParticipation(10L, 1L)).thenReturn("FORFAIT");

        Resultat existing = new Resultat();
        existing.setId(1L);
        existing.setAthleteId(1L);
        existing.setStatut(ResultatStatut.FORFAIT);
        when(resultatRepository.findByEpreuveIdAndAthleteId(10L, 1L)).thenReturn(Optional.of(existing));
        when(resultatRepository.findByEpreuveId(10L)).thenReturn(new ArrayList<>(List.of(existing)));

        EpreuveContextDto ctx = new EpreuveContextDto();
        ctx.setId(10L);
        ctx.setDiscipline("NATATION");
        ctx.setNiveauEpreuve("FINALE");

        PerformanceEntryDto e1 = new PerformanceEntryDto();
        e1.setAthleteId(1L);
        e1.setValeurPrincipale("47.21");

        BulkResultatRequest request = new BulkResultatRequest();
        request.setPerformances(List.of(e1));

        List<Resultat> resultats = resultatService.saisirBulk(10L, request, ctx);

        // Aucun nouveau save (l'existant est deja FORFAIT)
        verify(resultatRepository, org.mockito.Mockito.never()).save(any());
        assertEquals(1, resultats.size());
        assertEquals(ResultatStatut.FORFAIT, resultats.get(0).getStatut());
    }
}
