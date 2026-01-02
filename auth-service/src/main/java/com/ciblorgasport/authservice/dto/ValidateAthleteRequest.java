package com.ciblorgasport.authservice.dto;

public class ValidateAthleteRequest {
    private String username;
    private boolean validated;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public boolean isValidated() { return validated; }
    public void setValidated(boolean validated) { this.validated = validated; }
}
