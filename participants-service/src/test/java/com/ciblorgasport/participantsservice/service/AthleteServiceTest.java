package com.ciblorgasport.participantsservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ciblorgasport.participantsservice.dto.AthleteMapper;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteDocsRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteInfoRequest;
import com.ciblorgasport.participantsservice.dto.request.UpdateAthleteObservationRequest;
import com.ciblorgasport.participantsservice.dto.request.ValidationRequest;
import com.ciblorgasport.participantsservice.dto.AthleteDocsDto;
import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.Message;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaMessageRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import org.mockito.ArgumentCaptor;

/**
 * Tests unitaires JUnit (sans Spring, sans MockMvc) — Mockito pour simuler JPA.
 */
class AthleteServiceTest {

    private JpaAthleteRepository athleteRepository;
    private JpaMessageRepository messageRepository;
    private AthleteService athleteService;
    private java.util.Map<Long, Athlete> fakeStore;

    @BeforeEach
    void setUp() {
        // in-test fake store to simulate repository behaviour
        fakeStore = new java.util.HashMap<>();
        java.util.List<Message> savedMessages = new java.util.ArrayList<>();

        athleteRepository = mock(JpaAthleteRepository.class);
        messageRepository = mock(JpaMessageRepository.class);

        when(athleteRepository.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return java.util.Optional.ofNullable(fakeStore.get(id));
        });

        when(athleteRepository.save(any(Athlete.class))).thenAnswer(inv -> {
            Athlete a = inv.getArgument(0);
            fakeStore.put(a.getId(), a);
            return a;
        });

        when(messageRepository.save(any(Message.class))).thenAnswer(inv -> {
            Message m = inv.getArgument(0);
            savedMessages.add(m);
            return m;
        });

        when(messageRepository.findAll()).thenAnswer(inv -> new java.util.ArrayList<>(savedMessages));

        AthleteMapper athleteMapper = new AthleteMapper();
        ParticipantsStore store = new ParticipantsStore();

        athleteService = new AthleteService(athleteRepository, messageRepository, athleteMapper, store);
    }

    @Test
    void athlete_updateInfo_updates_existing_athlete() {
        fakeStore.put(1L, new Athlete(1L, null, null, null, null, false, null, null));

        UpdateAthleteInfoRequest request = new UpdateAthleteInfoRequest();
        request.setNom("Dupont");
        request.setPrenom("Marie");
        request.setDateNaissance(LocalDate.parse("2000-03-22"));
        request.setPays("Belgique");

        Athlete athlete = athleteService.updateInfo(1L, request);

        assertEquals(1L, athlete.getId());
        assertEquals("Dupont", athlete.getNom());
        assertEquals("Marie", athlete.getPrenom());
        assertEquals(LocalDate.parse("2000-03-22"), athlete.getDateNaissance());
        assertEquals("Belgique", athlete.getPays());
        assertEquals(false, athlete.isValide());
    }

    @Test
    void athlete_updateDocs_sets_certificat_and_passport() {
        // Athlète existant
        fakeStore.put(2L, new Athlete(2L, "Test", null, null, null, false, null, null));

        UpdateAthleteDocsRequest docsRequest = new UpdateAthleteDocsRequest();
        docsRequest.setDocs(new AthleteDocsDto("certificat.pdf", "passport.pdf"));

        Athlete athlete = athleteService.updateDocs(2L, docsRequest);

        assertEquals("certificat.pdf", athlete.getDocs().getCertificatMedical());
        assertEquals("passport.pdf", athlete.getDocs().getPassport());
    }

    @Test
    void athlete_updateDocs_requires_docs_object() {
        fakeStore.put(3L, new Athlete(3L, "Test", null, null, null, false, null, null));
        UpdateAthleteDocsRequest docsRequest = new UpdateAthleteDocsRequest();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> athleteService.updateDocs(3L, docsRequest));
        assertNotNull(ex);
    }

    @Test
    void athlete_updateObservation_sets_observation() {
        fakeStore.put(4L, new Athlete(4L, "Test", null, null, null, false, null, null));
        UpdateAthleteObservationRequest request = new UpdateAthleteObservationRequest();
        request.setObservation("Certificat médical valide");

        Athlete athlete = athleteService.updateObservation(4L, request);

        assertEquals("Certificat médical valide", athlete.getObservation());
    }

    @Test
    void commissaire_validate_refusal_sets_motifRefus_and_creates_message_if_provided() {
        fakeStore.put(10L, new Athlete(10L, "A", null, null, null, false, null, null));

        ValidationRequest validation = new ValidationRequest();
        validation.setValide(false);
        validation.setMotifRefus("passport expiré");
        validation.setMessage("passport expiré");

        Athlete athlete = athleteService.validate(10L, validation);

        assertEquals(false, athlete.isValide());
        assertEquals("passport expiré", athlete.getMotifRefus());

        // On vérifie qu'un message a bien été sauvegardé pour l'athlète
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(1)).save(captor.capture());
        assertEquals(10L, captor.getValue().getAthleteId());
    }

    @Test
    void commissaire_validate_true_clears_motifRefus() {
        fakeStore.put(11L, new Athlete(11L, "A", null, null, null, false, null, null));

        ValidationRequest refuse = new ValidationRequest();
        refuse.setValide(false);
        refuse.setMotifRefus("doc manquant");
        athleteService.validate(11L, refuse);

        ValidationRequest ok = new ValidationRequest();
        ok.setValide(true);

        Athlete athlete = athleteService.validate(11L, ok);

        assertEquals(true, athlete.isValide());
        assertNull(athlete.getMotifRefus());
    }

    @Test
    void commissaire_createMessage_requires_non_blank_content() {
        fakeStore.put(12L, new Athlete(12L, "A", null, null, null, false, null, null));

        assertThrows(IllegalArgumentException.class, () -> athleteService.createMessage(12L, "   "));
    }

    @Test
    void commissaire_createMessage_requires_existing_athlete() {
        assertThrows(IllegalArgumentException.class, () -> athleteService.createMessage(999L, "hello"));
    }
}
