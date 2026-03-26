package com.ciblorgasport.resultatsservice.config;

import com.ciblorgasport.resultatsservice.model.Discipline;
import com.ciblorgasport.resultatsservice.service.calcul.strategies.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration du Pattern Stratégie.
 * Expose une Map<Discipline, ClassementStrategy> injectée dans ClassementService.
 */
@Configuration
public class StrategyConfig {

    @Bean
    public Map<Discipline, ClassementStrategy> strategies(
            NatationClassementStrategy natation,
            PlongeonClassementStrategy plongeon,
            WaterPoloClassementStrategy waterPolo,
            NatationArtistiqueClassementStrategy natationArtistique,
            EauLibreClassementStrategy eauLibre) {

        return Map.of(
                Discipline.NATATION, natation,
                Discipline.PLONGEON, plongeon,
                Discipline.WATER_POLO, waterPolo,
                Discipline.NATATION_ARTISTIQUE, natationArtistique,
                Discipline.EAU_LIBRE, eauLibre
        );
    }
}
