package com.ciblorgasport.analyticsservice.enums;

public enum EventType {
    // Auth
    USER_LOGIN,
    USER_LOGOUT,
    USER_REGISTER,
    // Events / Compétitions / Épreuves
    EVENT_VIEW,
    COMPETITION_VIEW,
    EPREUVE_VIEW,
    // Participants / Équipes
    ATHLETE_PROFILE_VIEW,
    ATHLETE_VALIDATION,
    EQUIPE_VIEW,
    // Résultats
    RESULT_VIEW,
    RESULT_SUBMIT,
    RESULT_PUBLISHED,
    // Incidents
    INCIDENT_VIEW,
    INCIDENT_DECLARED,
    // Générique
    PAGE_VIEW
}