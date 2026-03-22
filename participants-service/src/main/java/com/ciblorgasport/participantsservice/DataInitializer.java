package com.ciblorgasport.participantsservice;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ciblorgasport.participantsservice.model.AthleteDocs;
import com.ciblorgasport.participantsservice.model.EpreuveAthleteAssignment;
import com.ciblorgasport.participantsservice.model.Sexe;
import com.ciblorgasport.participantsservice.repository.JpaAthleteRepository;
import com.ciblorgasport.participantsservice.repository.JpaEpreuveAthleteAssignmentRepository;

/**
 * Initialise les profils athlètes et les inscriptions aux épreuves.
 * Suppose que les athlètes ont déjà été créés via l'auth-service (athleteId == userId).
 * Idempotent : vérifie l'existence avant chaque opération.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    // Placeholder PDF minimal (1 octet) pour satisfaire la validation documentaire
    private static final byte[] PLACEHOLDER_PDF = new byte[]{0x25, 0x50, 0x44, 0x46}; // %PDF

    private final JpaAthleteRepository athleteRepository;
    private final JpaEpreuveAthleteAssignmentRepository assignmentRepository;

    public DataInitializer(JpaAthleteRepository athleteRepository,
                           JpaEpreuveAthleteAssignmentRepository assignmentRepository) {
        this.athleteRepository = athleteRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Données des 20 athlètes (id, nom, prenom, dateNaissance, pays, sexe)
        // Les IDs correspondent aux user IDs générés dans l'auth-service (athlete01 → ID=3, ...)
        // On utilise les usernames pour retrouver les athlètes existants
        List<AthleteData> athletes = List.of(
            new AthleteData("athlete01", "Ben Ali",   "Mohamed",      LocalDate.of(1998, 3, 12),  "Tunisie",  Sexe.MASCULIN),
            new AthleteData("athlete02", "Martin",    "Léa",          LocalDate.of(2000, 7, 25),  "France",   Sexe.FEMININ),
            new AthleteData("athlete03", "Ruiz",      "Carlos",       LocalDate.of(1997, 11, 8),  "Espagne",  Sexe.MASCULIN),
            new AthleteData("athlete04", "Greco",     "Sofia",        LocalDate.of(2001, 2, 14),  "Italie",   Sexe.FEMININ),
            new AthleteData("athlete05", "Amrani",    "Youssef",      LocalDate.of(1999, 5, 30),  "Maroc",    Sexe.MASCULIN),
            new AthleteData("athlete06", "Petrakis",  "Elena",        LocalDate.of(2002, 9, 3),   "Grèce",    Sexe.FEMININ),
            new AthleteData("athlete07", "Yilmaz",    "Mehmet",       LocalDate.of(1996, 1, 17),  "Turquie",  Sexe.MASCULIN),
            new AthleteData("athlete08", "Khalil",    "Amira",        LocalDate.of(2000, 4, 22),  "Algérie",  Sexe.FEMININ),
            new AthleteData("athlete09", "Santos",    "João",         LocalDate.of(1998, 8, 5),   "Portugal", Sexe.MASCULIN),
            new AthleteData("athlete10", "Kovac",     "Marta",        LocalDate.of(2003, 12, 19), "Croatie",  Sexe.FEMININ),
            new AthleteData("athlete11", "Dubois",    "Lucas",        LocalDate.of(1999, 6, 11),  "France",   Sexe.MASCULIN),
            new AthleteData("athlete12", "Benali",    "Fatima Zahra", LocalDate.of(2001, 10, 7),  "Maroc",    Sexe.FEMININ),
            new AthleteData("athlete13", "Rossi",     "Marco",        LocalDate.of(1997, 3, 28),  "Italie",   Sexe.MASCULIN),
            new AthleteData("athlete14", "Bouchard",  "Nadia",        LocalDate.of(2002, 7, 16),  "France",   Sexe.FEMININ),
            new AthleteData("athlete15", "Mansouri",  "Karim",        LocalDate.of(1998, 2, 9),   "Tunisie",  Sexe.MASCULIN),
            new AthleteData("athlete16", "Gomez",     "Ana",          LocalDate.of(2000, 11, 24), "Espagne",  Sexe.FEMININ),
            new AthleteData("athlete17", "Nikolaou",  "Stavros",      LocalDate.of(1996, 8, 13),  "Grèce",    Sexe.MASCULIN),
            new AthleteData("athlete18", "Mujic",     "Hana",         LocalDate.of(2001, 5, 2),   "Croatie",  Sexe.FEMININ),
            new AthleteData("athlete19", "Ferhat",    "Omar",         LocalDate.of(1999, 9, 20),  "Algérie",  Sexe.MASCULIN),
            new AthleteData("athlete20", "Fernandez", "Inês",         LocalDate.of(2003, 1, 15),  "Portugal", Sexe.FEMININ)
        );

        // Remplir les profils athlètes
        for (AthleteData data : athletes) {
            athleteRepository.findByUsername(data.username).ifPresent(athlete -> {
                if (athlete.getNom() == null || athlete.getNom().isBlank()) {
                    athlete.setNom(data.nom);
                    athlete.setPrenom(data.prenom);
                    athlete.setDateNaissance(data.dateNaissance);
                    athlete.setPays(data.pays);
                    athlete.setSexe(data.sexe);
                    athlete.setValide(true);
                    if (athlete.getDocs() == null) {
                        athlete.setDocs(new AthleteDocs(PLACEHOLDER_PDF, PLACEHOLDER_PDF));
                    } else {
                        if (athlete.getDocs().getCertificatMedical() == null || athlete.getDocs().getCertificatMedical().length == 0) {
                            athlete.getDocs().setCertificatMedical(PLACEHOLDER_PDF);
                        }
                        if (athlete.getDocs().getPassport() == null || athlete.getDocs().getPassport().length == 0) {
                            athlete.getDocs().setPassport(PLACEHOLDER_PDF);
                        }
                    }
                    athleteRepository.save(athlete);
                    log.debug("[DataInitializer] Profil mis à jour : {}", data.username);
                }
            });
        }

        // Inscriptions aux épreuves (épreuves 1–10 supposées créées par event-service)
        // Distribution : 2 athlètes par épreuve, couvrant les 20 athlètes
        // Les IDs des épreuves seront 1–10 si c'est la première initialisation de event-service
        assignIfAbsent(1L, athletes.get(0).username);   // 100m NL Hommes   → Mohamed, João
        assignIfAbsent(1L, athletes.get(8).username);
        assignIfAbsent(2L, athletes.get(1).username);   // 100m NL Femmes   → Léa, Marta
        assignIfAbsent(2L, athletes.get(9).username);
        assignIfAbsent(3L, athletes.get(2).username);   // 200m Dos Mixte   → Carlos, Sofia
        assignIfAbsent(3L, athletes.get(3).username);
        assignIfAbsent(4L, athletes.get(4).username);   // 4x100m Relais H  → Youssef, Mehmet, Lucas, Karim
        assignIfAbsent(4L, athletes.get(6).username);
        assignIfAbsent(4L, athletes.get(10).username);
        assignIfAbsent(4L, athletes.get(14).username);
        assignIfAbsent(5L, athletes.get(5).username);   // Water-Polo H     → Stavros, Omar, Marco, Carlos
        assignIfAbsent(5L, athletes.get(16).username);
        assignIfAbsent(5L, athletes.get(18).username);
        assignIfAbsent(5L, athletes.get(12).username);
        assignIfAbsent(6L, athletes.get(7).username);   // Water-Polo F     → Amira, Elena, Nadia, Ana
        assignIfAbsent(6L, athletes.get(5).username);
        assignIfAbsent(6L, athletes.get(13).username);
        assignIfAbsent(6L, athletes.get(15).username);
        assignIfAbsent(7L, athletes.get(11).username);  // Duo Nat. Art.    → Fatima Zahra, Hana
        assignIfAbsent(7L, athletes.get(17).username);
        assignIfAbsent(8L, athletes.get(2).username);   // Plongeon 10m H   → Carlos, Mehmet
        assignIfAbsent(8L, athletes.get(6).username);
        assignIfAbsent(9L, athletes.get(3).username);   // Plongeon 3m F    → Sofia, Inês
        assignIfAbsent(9L, athletes.get(19).username);
        assignIfAbsent(10L, athletes.get(0).username);  // 10km Eau Libre   → Mohamed, Léa, Karim, Nadia
        assignIfAbsent(10L, athletes.get(1).username);
        assignIfAbsent(10L, athletes.get(14).username);
        assignIfAbsent(10L, athletes.get(13).username);

        log.info("[DataInitializer] Profils athlètes et inscriptions aux épreuves initialisés.");
    }

    private void assignIfAbsent(Long epreuveId, String username) {
        athleteRepository.findByUsername(username).ifPresent(athlete -> {
            if (!assignmentRepository.existsByEpreuveIdAndAthleteId(epreuveId, athlete.getId())) {
                assignmentRepository.save(new EpreuveAthleteAssignment(epreuveId, athlete.getId()));
                log.debug("[DataInitializer] Inscrit {} à l'épreuve {}", username, epreuveId);
            }
        });
    }

    private record AthleteData(String username, String nom, String prenom,
                                LocalDate dateNaissance, String pays, Sexe sexe) {}
}
