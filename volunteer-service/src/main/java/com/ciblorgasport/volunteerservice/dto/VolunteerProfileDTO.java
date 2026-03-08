package com.ciblorgasport.volunteerservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

public class VolunteerProfileDTO {
    // Champs pour la réponse (avec ID)
    private UUID id;
    private Long authUserId;
    
    // Champs du profil
    @NotBlank(message = "Le prénom est obligatoire")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;
    
    @Email(message = "Email invalide")
    private String email;
    
    private String phoneNumber;
    private Set<String> languages;
    private Set<String> preferredTaskTypes;
    private Set<AvailabilityDTO> availabilities;
    private boolean profileComplete;

    // Constructeur pour la création (sans id, utilisé par le frontend)
    @JsonCreator
    public VolunteerProfileDTO(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("phoneNumber") String phoneNumber,
            @JsonProperty("languages") Set<String> languages,
            @JsonProperty("preferredTaskTypes") Set<String> preferredTaskTypes,
            @JsonProperty("availabilities") Set<AvailabilityDTO> availabilities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.languages = languages;
        this.preferredTaskTypes = preferredTaskTypes;
        this.availabilities = availabilities;
        this.profileComplete = true;
    }

    // Constructeur complet pour la réponse (avec id)
    public VolunteerProfileDTO(UUID id, Long authUserId, String email, String firstName, String lastName,
                               String phoneNumber, Set<String> languages, Set<String> preferredTaskTypes,
                               Set<AvailabilityDTO> availabilities, boolean profileComplete) {
        this.id = id;
        this.authUserId = authUserId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.languages = languages;
        this.preferredTaskTypes = preferredTaskTypes;
        this.availabilities = availabilities;
        this.profileComplete = profileComplete;
    }

    // Getters
    public UUID getId() { return id; }
    public Long getAuthUserId() { return authUserId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Set<String> getLanguages() { return languages; }
    public Set<String> getPreferredTaskTypes() { return preferredTaskTypes; }
    public Set<AvailabilityDTO> getAvailabilities() { return availabilities; }
    public boolean isProfileComplete() { return profileComplete; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setAuthUserId(Long authUserId) { this.authUserId = authUserId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLanguages(Set<String> languages) { this.languages = languages; }
    public void setPreferredTaskTypes(Set<String> preferredTaskTypes) { this.preferredTaskTypes = preferredTaskTypes; }
    public void setAvailabilities(Set<AvailabilityDTO> availabilities) { this.availabilities = availabilities; }
    public void setProfileComplete(boolean profileComplete) { this.profileComplete = profileComplete; }
}