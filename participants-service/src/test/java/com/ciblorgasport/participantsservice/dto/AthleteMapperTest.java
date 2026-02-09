package com.ciblorgasport.participantsservice.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.ciblorgasport.participantsservice.model.Athlete;
import com.ciblorgasport.participantsservice.model.AthleteDocs;

/**
 * Tests unitaires du mapper (sans Spring).
 */
class AthleteMapperTest {

    private final AthleteMapper mapper = new AthleteMapper();

    @Test
    void toDto_copies_all_fields_and_docs() {
        Athlete athlete = new Athlete(
                1L,
                "Titouche",
                "Salim",
                LocalDate.parse("2000-07-26"),
                "Algerie",
                true,
                new AthleteDocs("certificat.pdf", "passport.pdf"),
                "OK"
        );
        athlete.setMotifRefus("passport expiré");

        AthleteDto dto = mapper.toDto(athlete);

        assertEquals(1L, dto.getId());
        assertEquals("Titouche", dto.getNom());
        assertEquals("Salim", dto.getPrenom());
        assertEquals(LocalDate.parse("2000-07-26"), dto.getDateNaissance());
        assertEquals("Algerie", dto.getPays());
        assertEquals(true, dto.isValide());
        assertEquals("OK", dto.getObservation());
        assertEquals("passport expiré", dto.getMotifRefus());

        assertNotNull(dto.getDocs());
        assertEquals("certificat.pdf", dto.getDocs().getCertificatMedical());
        assertEquals("passport.pdf", dto.getDocs().getPassport());
    }

    @Test
    void toDto_when_docs_null_returns_docs_object_with_null_fields() {
        Athlete athlete = new Athlete(
                2L,
                "Dupont",
                "Marie",
                LocalDate.parse("2000-03-22"),
                "Belgique",
                false,
                null,
                ""
        );

        AthleteDto dto = mapper.toDto(athlete);

        assertNotNull(dto.getDocs());
        assertEquals(null, dto.getDocs().getCertificatMedical());
        assertEquals(null, dto.getDocs().getPassport());
    }
}
