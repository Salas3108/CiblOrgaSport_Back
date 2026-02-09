package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requête Athlète : ajouter/mettre à jour une remarque (observation).
 */
public class UpdateAthleteObservationRequest {
    private String observation;

    public UpdateAthleteObservationRequest() {
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
