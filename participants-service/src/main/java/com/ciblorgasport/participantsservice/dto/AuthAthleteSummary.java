package com.ciblorgasport.participantsservice.dto;

/**
 * DTO pour recevoir les athletes depuis auth-service.
 */
public class AuthAthleteSummary {
    private Long id;
    private String username;

    public AuthAthleteSummary() {
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
