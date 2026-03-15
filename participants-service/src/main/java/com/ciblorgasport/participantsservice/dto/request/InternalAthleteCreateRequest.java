package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requete interne : creer un profil athlete minimal.
 */
public class InternalAthleteCreateRequest {
    private Long id;
    private String username;

    public InternalAthleteCreateRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
