package com.ciblorgasport.resultatsservice.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ciblorgasport.resultatsservice.client.EventServiceClient;
import com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient;
import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.security.JwtUtils;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@RestController
@RequestMapping({"/resultats", "/api/resultats"})
public class MeResultatController {

    private final ResultatService resultatService;
    private final ResultatMapper resultatMapper;
    private final EventServiceClient eventServiceClient;
    private final ParticipantsServiceClient participantsServiceClient;
    private final JwtUtils jwtUtils;

    public MeResultatController(ResultatService resultatService,
                                ResultatMapper resultatMapper,
                                EventServiceClient eventServiceClient,
                                ParticipantsServiceClient participantsServiceClient,
                                JwtUtils jwtUtils) {
        this.resultatService = resultatService;
        this.resultatMapper = resultatMapper;
        this.eventServiceClient = eventServiceClient;
        this.participantsServiceClient = participantsServiceClient;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/me")
    public ResponseEntity<List<ResultatDto>> getMyResultats(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = header.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = jwtUtils.getUserIdFromJwtToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AthleteInfoDto athlete = participantsServiceClient.getAthlete(userId);

        List<ResultatDto> list = resultatService.getResultatsAthlete(userId, true).stream()
                .map(r -> {
                    EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(r.getEpreuveId());
                    return resultatMapper.toDtoEnrichi(r, athlete, null, ctx);
                })
                .toList();

        return ResponseEntity.ok(list);
    }
}
