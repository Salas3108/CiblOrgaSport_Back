package com.ciblorgasport.analyticsservice.enums;

public enum EventType {
    // Connexions
    USER_LOGIN,
    USER_LOGOUT,
    USER_REGISTER,

    // Données consultées
    EVENT_VIEW,
    COMPETITION_VIEW,
    EPREUVE_VIEW,
    RESULT_VIEW,
    ATHLETE_PROFILE_VIEW,

    // Notifications
    NOTIFICATION_SENT,
    NOTIFICATION_SUBSCRIBED,

    // Volontaires
    VOLUNTEER_VALIDATED,

    // Incidents
    INCIDENT_DECLARED,

    // Générique
    PAGE_VIEW
}