package com.ciblorgasport.resultatsservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@RestController
@RequestMapping({"/commissaire/resultats", "/api/commissaire/resultats"})
@PreAuthorize("hasRole('COMMISSAIRE') or hasRole('ADMIN')")
public class CommissaireResultatController {

    private final ResultatService resultatService;
    private final ResultatMapper resultatMapper;

    public CommissaireResultatController(ResultatService resultatService, ResultatMapper resultatMapper) {
        this.resultatService = resultatService;
        this.resultatMapper = resultatMapper;
    }

    @PostMapping
    public ResponseEntity<ResultatDto> createOrUpdate(@RequestBody ResultatRequest request) {
        return ResponseEntity.ok(resultatMapper.toDto(resultatService.createOrUpdate(request)));
    }

    @PostMapping("/{id}/validation")
    public ResponseEntity<ResultatDto> validate(@PathVariable Long id) {
        return ResponseEntity.ok(resultatMapper.toDto(resultatService.validateResultat(id)));
    }

    @PostMapping("/{id}/publier")
    public ResponseEntity<ResultatDto> publish(@PathVariable Long id) {
        return ResponseEntity.ok(resultatMapper.toDto(resultatService.publishResultat(id)));
    }

    @GetMapping("/epreuves/{epreuveId}")
    public ResponseEntity<List<ResultatDto>> getClassementEpreuve(@PathVariable Long epreuveId) {
        List<ResultatDto> list = resultatService.getClassementEpreuve(epreuveId, false).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<List<ResultatDto>> getResultatsAthlete(@PathVariable Long athleteId) {
        List<ResultatDto> list = resultatService.getResultatsAthlete(athleteId, false).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/equipes/{equipeId}")
    public ResponseEntity<List<ResultatDto>> getResultatsEquipe(@PathVariable Long equipeId) {
        List<ResultatDto> list = resultatService.getResultatsEquipe(equipeId, false).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }
}
