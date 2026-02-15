package com.ciblorgasport.resultatsservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@RestController
@RequestMapping({"/public/resultats", "/api/public/resultats"})
public class PublicResultatController {

    private final ResultatService resultatService;
    private final ResultatMapper resultatMapper;

    public PublicResultatController(ResultatService resultatService, ResultatMapper resultatMapper) {
        this.resultatService = resultatService;
        this.resultatMapper = resultatMapper;
    }

    @GetMapping("/epreuves/{epreuveId}")
    public ResponseEntity<List<ResultatDto>> getClassementEpreuve(@PathVariable Long epreuveId) {
        List<ResultatDto> list = resultatService.getClassementEpreuve(epreuveId, true).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<List<ResultatDto>> getResultatsAthlete(@PathVariable Long athleteId) {
        List<ResultatDto> list = resultatService.getResultatsAthlete(athleteId, true).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/equipes/{equipeId}")
    public ResponseEntity<List<ResultatDto>> getResultatsEquipe(@PathVariable Long equipeId) {
        List<ResultatDto> list = resultatService.getResultatsEquipe(equipeId, true).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }
}
