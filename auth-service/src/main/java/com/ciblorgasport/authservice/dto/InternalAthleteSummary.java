package com.ciblorgasport.authservice.dto;

/**
 * DTO interne pour la synchro des athletes.
 */
public class InternalAthleteSummary {
    private Long id;
    private String username;

    public InternalAthleteSummary() {
    }

    public InternalAthleteSummary(Long id, String username) {
        this.id = id;
        this.username = username;
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
