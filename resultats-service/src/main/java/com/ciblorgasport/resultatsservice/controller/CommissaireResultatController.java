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

import com.ciblorgasport.resultatsservice.client.EventServiceClient;
import com.ciblorgasport.resultatsservice.client.ParticipantsServiceClient;
import com.ciblorgasport.resultatsservice.client.dto.AthleteInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EquipeInfoDto;
import com.ciblorgasport.resultatsservice.client.dto.EpreuveContextDto;
import com.ciblorgasport.resultatsservice.dto.ResultatDto;
import com.ciblorgasport.resultatsservice.dto.ResultatMapper;
import com.ciblorgasport.resultatsservice.dto.request.BulkResultatRequest;
import com.ciblorgasport.resultatsservice.dto.request.ResultatRequest;
import com.ciblorgasport.resultatsservice.dto.response.EpreuveContexteResponse;
import com.ciblorgasport.resultatsservice.dto.response.PublicationResponseDTO;
import com.ciblorgasport.resultatsservice.model.Resultat;
import com.ciblorgasport.resultatsservice.service.PublicationService;
import com.ciblorgasport.resultatsservice.service.ResultatService;

@RestController
@RequestMapping({"/resultats/commissaire", "/api/resultats/commissaire"})
@PreAuthorize("hasRole('COMMISSAIRE') or hasRole('ADMIN')")
public class CommissaireResultatController {

    private final ResultatService resultatService;
    private final ResultatMapper resultatMapper;
    private final PublicationService publicationService;
    private final EventServiceClient eventServiceClient;
    private final ParticipantsServiceClient participantsServiceClient;

    public CommissaireResultatController(ResultatService resultatService,
                                          ResultatMapper resultatMapper,
                                          PublicationService publicationService,
                                          EventServiceClient eventServiceClient,
                                          ParticipantsServiceClient participantsServiceClient) {
        this.resultatService = resultatService;
        this.resultatMapper = resultatMapper;
        this.publicationService = publicationService;
        this.eventServiceClient = eventServiceClient;
        this.participantsServiceClient = participantsServiceClient;
    }

    // ── Endpoints existants ──────────────────────────────────────────────────

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
        EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(epreuveId);
        List<ResultatDto> list = resultatService.getClassementEpreuve(epreuveId, false).stream()
                .map(r -> toEnrichi(r, ctx))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/athletes/{athleteId}")
    public ResponseEntity<List<ResultatDto>> getResultatsAthlete(@PathVariable Long athleteId) {
        AthleteInfoDto athlete = participantsServiceClient.getAthlete(athleteId);
        List<ResultatDto> list = resultatService.getResultatsAthlete(athleteId, false).stream()
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
        List<ResultatDto> list = resultatService.getResultatsEquipe(equipeId, false).stream()
                .map(r -> {
                    EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(r.getEpreuveId());
                    return resultatMapper.toDtoEnrichi(r, null, equipe, ctx);
                })
                .toList();
        return ResponseEntity.ok(list);
    }

    // ── Nouveaux endpoints ───────────────────────────────────────────────────

    /**
     * Retourne les métadonnées de l'épreuve + la liste des participants.
     */
    @GetMapping("/epreuves/{id}/contexte")
    public ResponseEntity<EpreuveContexteResponse> getContexteEpreuve(@PathVariable Long id) {
        EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(id);
        if (ctx == null) {
            return ResponseEntity.notFound().build();
        }

        EpreuveContexteResponse response = new EpreuveContexteResponse();
        response.setEpreuveId(ctx.getId());
        response.setNom(ctx.getNom());
        response.setDiscipline(ctx.getDiscipline());
        response.setNiveauEpreuve(ctx.getNiveauEpreuve());
        response.setTypeEpreuve(ctx.getTypeEpreuve());

        if (ctx.getAthleteIds() != null) {
            List<AthleteInfoDto> athletes = ctx.getAthleteIds().stream()
                    .map(participantsServiceClient::getAthlete)
                    .filter(a -> a != null)
                    .toList();
            response.setAthletes(athletes);
        }

        if (ctx.getEquipeIds() != null) {
            List<EquipeInfoDto> equipes = ctx.getEquipeIds().stream()
                    .map(participantsServiceClient::getEquipe)
                    .filter(e -> e != null)
                    .toList();
            response.setEquipes(equipes);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Saisie en masse des performances + calcul automatique du classement.
     */
    @PostMapping("/epreuves/{id}/saisie")
    public ResponseEntity<List<ResultatDto>> saisirBulk(@PathVariable Long id,
                                                          @RequestBody BulkResultatRequest request) {
        EpreuveContextDto ctx = eventServiceClient.getEpreuveContext(id);
        if (ctx == null) {
            return ResponseEntity.badRequest().build();
        }
        List<Resultat> resultats = resultatService.saisirBulk(id, request, ctx);
        List<ResultatDto> dtos = resultats.stream()
                .map(r -> toEnrichi(r, ctx))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Valide tous les résultats EN_ATTENTE de l'épreuve.
     */
    @PostMapping("/epreuves/{id}/valider-tout")
    public ResponseEntity<List<ResultatDto>> validerTout(@PathVariable Long id) {
        List<ResultatDto> list = resultatService.validerTout(id).stream()
                .map(resultatMapper::toDto)
                .toList();
        return ResponseEntity.ok(list);
    }

    /**
     * Publie tous les résultats VALIDE de l'épreuve.
     */
    @PostMapping("/epreuves/{id}/publier-tout")
    public ResponseEntity<PublicationResponseDTO> publierTout(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.publierEpreuve(id));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private ResultatDto toEnrichi(Resultat r, EpreuveContextDto ctx) {
        AthleteInfoDto athlete = r.getAthleteId() != null
                ? participantsServiceClient.getAthlete(r.getAthleteId()) : null;
        EquipeInfoDto equipe = r.getEquipeId() != null
                ? participantsServiceClient.getEquipe(r.getEquipeId()) : null;
        return resultatMapper.toDtoEnrichi(r, athlete, equipe, ctx);
    }
}
