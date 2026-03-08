package com.ciblorgasport.volunteerservice.dto;

import com.ciblorgasport.volunteerservice.entity.Volunteer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class VolunteerMatchDTO {
    private Volunteer volunteer;
    private boolean isMatch;
    private List<String> missingRequirements;

    public VolunteerMatchDTO() {
        this.missingRequirements = new ArrayList<>();
    }

    @JsonCreator
    public VolunteerMatchDTO(
            @JsonProperty("volunteer") Volunteer volunteer,
            @JsonProperty("isMatch") boolean isMatch,
            @JsonProperty("missingRequirements") List<String> missingRequirements) {
        this.volunteer = volunteer;
        this.isMatch = isMatch;
        this.missingRequirements = missingRequirements != null ? missingRequirements : new ArrayList<>();
    }

    // Getters
    public Volunteer getVolunteer() { return volunteer; }
    
    @JsonProperty("isMatch")
    public boolean isMatch() { return isMatch; }
    
    public List<String> getMissingRequirements() { return missingRequirements; }

    // Setters
    public void setVolunteer(Volunteer volunteer) { this.volunteer = volunteer; }
    
    @JsonProperty("isMatch")
    public void setMatch(boolean match) { isMatch = match; }
    
    public void setMissingRequirements(List<String> missingRequirements) { 
        this.missingRequirements = missingRequirements; 
    }
    
    public void addMissingRequirement(String requirement) {
        this.missingRequirements.add(requirement);
    }
}