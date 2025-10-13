package com.ciblorgasport.service;

import com.ciblorgasport.dto.CompetitionResponse;
import com.ciblorgasport.dto.CreateCompetitionRequest;
import com.ciblorgasport.entity.Competition;
import com.ciblorgasport.repository.CompetitionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompetitionService {

    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public CompetitionResponse createCompetition(CreateCompetitionRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date cannot be before start date");
        }

        Competition competition = new Competition(
                request.getName(),
                request.getDiscipline(),
                request.getLocation(),
                request.getStartDate(),
                request.getEndDate()
        );

        Competition saved = competitionRepository.save(competition);
        return new CompetitionResponse(
                saved.getId(),
                saved.getName(),
                saved.getDiscipline(),
                saved.getLocation(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }
}
