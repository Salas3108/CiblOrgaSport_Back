package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requete Admin : creer un profil athlete.
 */
public class CreateAthleteRequest {
    private String username;

    public CreateAthleteRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
