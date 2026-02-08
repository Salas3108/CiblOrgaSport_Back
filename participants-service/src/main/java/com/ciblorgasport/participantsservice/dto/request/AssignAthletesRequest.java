package com.ciblorgasport.participantsservice.dto.request;

import java.util.List;

/**
 * Requete Commissaire : assigner des athletes a une equipe.
 */
public class AssignAthletesRequest {
    private List<Long> athleteIds;

    public AssignAthletesRequest() {
    }

    public List<Long> getAthleteIds() {
        return athleteIds;
    }

    public void setAthleteIds(List<Long> athleteIds) {
        this.athleteIds = athleteIds;
    }
}
