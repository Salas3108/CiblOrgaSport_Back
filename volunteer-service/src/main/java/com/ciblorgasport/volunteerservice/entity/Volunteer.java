package com.ciblorgasport.volunteerservice.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "volunteers")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private Long authUserId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String languages;

    private String preferredTaskTypes;



    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String availabilitiesJson;

    // Constructeurs
    public Volunteer() {}

    // Getters et setters...
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Long getAuthUserId() { return authUserId; }
    public void setAuthUserId(Long authUserId) { this.authUserId = authUserId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getLanguages() { return languages; }
    public void setLanguages(String languages) { this.languages = languages; }

    public String getPreferredTaskTypes() { return preferredTaskTypes; }
    public void setPreferredTaskTypes(String preferredTaskTypes) { this.preferredTaskTypes = preferredTaskTypes; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getAvailabilitiesJson() { return availabilitiesJson; }
    public void setAvailabilitiesJson(String availabilitiesJson) { this.availabilitiesJson = availabilitiesJson; }

    // Méthodes utilitaires
    public Set<String> getLanguagesSet() {
        if (languages == null || languages.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(languages.split(",")));
    }

    public void setLanguagesFromSet(Set<String> langs) {
        this.languages = langs != null ? String.join(",", langs) : null;
    }

    public Set<String> getPreferredTaskTypesSet() {
        if (preferredTaskTypes == null || preferredTaskTypes.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(preferredTaskTypes.split(",")));
    }

    public void setPreferredTaskTypesFromSet(Set<String> types) {
        this.preferredTaskTypes = types != null ? String.join(",", types) : null;
    }
}