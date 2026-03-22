package com.ciblorgasport.resultatsservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.resultatsservice.client.EventServiceClient;
import com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient;
import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EquipeInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@RestController
@RequestMapping({"/resultats/public", "/api/resultats/public"})
public class PublicResultatController {

    private final ResultatService resultatService;
    private final ResultatMapper resultatMapper;
    private final EventServiceClient eventServiceClient;
    private final ParticipantsServiceClient participantsServiceClient;

    public PublicResultatController(ResultatService resultatService,
                                     ResultatMapper resultatMapper,
                                     EventServiceClient eventServiceClient,
                                     ParticipantsServiceClient participantsServiceClient) {
        this.resultatService = resultatService;
        this.resultatMapper = resultatMapper;
        this.eventServiceClient = eventServiceClient;
        this.participantsServiceClient = participantsServiceClient;
    }

    @GetMapping("/epreuves/{epreuveId}")
    public ResponseEntity<List<ResultatDto>> getClassementEpreuve(@PathVariable Long epreuveId) {
        EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(epreuveId);
        List<ResultatDto> list = resultatService.getClassementEpreuve(epreuveId, true).stream()
                .map(r -> toEnrichi(r, ctx))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<List<ResultatDto>> getResultatsAthlete(@PathVariable Long athleteId) {
        AthleteInfoDto athlete = participantsServiceClient.getAthlete(athleteId);
        List<ResultatDto> list = resultatService.getResultatsAthlete(athleteId, true).stream()
                .map(r -> {
                    EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(r.getEpreuveId());
                    return resultatMapper.toDtoEnrichi(r, athlete, null, ctx);
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/equipes/{equipeId}")
    public ResponseEntity<List<ResultatDto>> getResultatsEquipe(@PathVariable Long equipeId) {
        EquipeInfoDto equipe = participantsServiceClient.getEquipe(equipeId);
        List<ResultatDto> list = resultatService.getResultatsEquipe(equipeId, true).stream()
                .map(r -> {
                    EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(r.getEpreuveId());
                    return resultatMapper.toDtoEnrichi(r, null, equipe, ctx);
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    private ResultatDto toEnrichi(Resultat r, EpreuveContextDto ctx) {
        AthleteInfoDto athlete = r.getAthleteId() != null
                ? participantsServiceClient.getAthlete(r.getAthleteId()) : null;
        EquipeInfoDto equipe = r.getEquipeId() != null
                ? participantsServiceClient.getEquipe(r.getEquipeId()) : null;
        return resultatMapper.toDtoEnrichi(r, athlete, equipe, ctx);
    }
}
