package com.ciblorgasport.eventservice;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ciblorgasport.eventservice.model.Competition;
import com.ciblorgasport.eventservice.model.Epreuve;
import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.model.enums.Discipline;
import com.ciblorgasport.eventservice.model.enums.GenreEpreuve;
import com.ciblorgasport.eventservice.model.enums.NiveauEpreuve;
import com.ciblorgasport.eventservice.model.enums.StatutEpreuve;
import com.ciblorgasport.eventservice.model.enums.TypeEpreuve;
import com.ciblorgasport.eventservice.repository.CompetitionRepository;
import com.ciblorgasport.eventservice.repository.EpreuveRepository;
import com.ciblorgasport.eventservice.repository.EventRepository;

/**
 * Initialise l'event, les compétitions et les épreuves de démonstration.
 * Idempotent : ne crée rien si des données existent déjà.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final EventRepository eventRepository;
    private final CompetitionRepository competitionRepository;
    private final EpreuveRepository epreuveRepository;

    public DataInitializer(EventRepository eventRepository,
                           CompetitionRepository competitionRepository,
                           EpreuveRepository epreuveRepository) {
        this.eventRepository = eventRepository;
        this.competitionRepository = competitionRepository;
        this.epreuveRepository = epreuveRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (eventRepository.count() > 0) {
            log.info("[DataInitializer] Données déjà présentes, initialisation ignorée.");
            return;
        }

        // --- Event ---
        Event event = new Event();
        event.setName("Jeux Aquatiques Méditerranéens 2026");
        event.setDateDebut(LocalDate.of(2026, 6, 15));
        event.setDateFin(LocalDate.of(2026, 6, 30));
        event.setDescription("Compétition internationale de sports aquatiques rassemblant les nations méditerranéennes.");
        event.setPaysHote("France");
        event = eventRepository.save(event);

        // --- Compétitions (5 disciplines) ---
        Competition natation       = createCompetition(event, Discipline.NATATION);
        Competition waterPolo      = createCompetition(event, Discipline.WATER_POLO);
        Competition natArtistique  = createCompetition(event, Discipline.NATATION_ARTISTIQUE);
        Competition plongeon       = createCompetition(event, Discipline.PLONGEON);
        Competition eauLibre       = createCompetition(event, Discipline.EAU_LIBRE);

        // --- 10 Épreuves ---
        createEpreuve("100m Nage Libre Hommes",        "Épreuve individuelle 100m nage libre masculin",
                natation, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.MASCULIN,  NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 16, 10, 0), 5);

        createEpreuve("100m Nage Libre Femmes",        "Épreuve individuelle 100m nage libre féminin",
                natation, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.FEMININ,   NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 16, 11, 0), 5);

        createEpreuve("200m Dos Mixte",                "Épreuve individuelle 200m dos toutes catégories",
                natation, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.MIXTE,     NiveauEpreuve.DEMI_FINALE,
                LocalDateTime.of(2026, 6, 17, 9, 30), 7);

        createEpreuve("4x100m Relais Nage Libre Hommes", "Relais 4×100m nage libre masculin",
                natation, TypeEpreuve.COLLECTIVE,  GenreEpreuve.MASCULIN,  NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 18, 14, 0), 10);

        createEpreuve("Water-Polo Hommes",             "Tournoi de water-polo masculin",
                waterPolo, TypeEpreuve.COLLECTIVE, GenreEpreuve.MASCULIN,  NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 19, 15, 0), 60);

        createEpreuve("Water-Polo Femmes",             "Tournoi de water-polo féminin",
                waterPolo, TypeEpreuve.COLLECTIVE, GenreEpreuve.FEMININ,   NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 20, 15, 0), 60);

        createEpreuve("Duo Natation Artistique Mixte", "Épreuve duo natation artistique mixte",
                natArtistique, TypeEpreuve.COLLECTIVE, GenreEpreuve.MIXTE, NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 21, 10, 0), 30);

        createEpreuve("Plongeon 10m Hommes",           "Épreuve de plongeon tour de 10m masculin",
                plongeon, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.MASCULIN, NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 22, 9, 0), 20);

        createEpreuve("Plongeon 3m Femmes",            "Épreuve de plongeon tremplin 3m féminin",
                plongeon, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.FEMININ,  NiveauEpreuve.DEMI_FINALE,
                LocalDateTime.of(2026, 6, 22, 11, 0), 20);

        createEpreuve("10km Eau Libre Mixte",          "Course en eau libre 10km toutes catégories",
                eauLibre, TypeEpreuve.INDIVIDUELLE, GenreEpreuve.MIXTE,    NiveauEpreuve.FINALE,
                LocalDateTime.of(2026, 6, 25, 8, 0), 120);

        log.info("[DataInitializer] Initialisé : 1 event, 5 compétitions, 10 épreuves.");
    }

    private Competition createCompetition(Event event, Discipline discipline) {
        Competition c = new Competition();
        c.setEvent(event);
        c.setDiscipline(discipline);
        return competitionRepository.save(c);
    }

    private void createEpreuve(String nom, String description, Competition competition,
                                TypeEpreuve type, GenreEpreuve genre, NiveauEpreuve niveau,
                                LocalDateTime dateHeure, int dureeMinutes) {
        Epreuve e = new Epreuve();
        e.setNom(nom);
        e.setDescription(description);
        e.setCompetition(competition);
        e.setTypeEpreuve(type);
        e.setGenreEpreuve(genre);
        e.setNiveauEpreuve(niveau);
        e.setDateHeure(dateHeure);
        e.setDureeMinutes(dureeMinutes);
        e.setStatut(StatutEpreuve.PLANIFIE);
        epreuveRepository.save(e);
    }
}
