package com.ciblorgasport.eventservice.model.enums;

public enum NiveauEpreuve {
    PHASE_PRELIMINAIRE,   // Couvre : SERIES, PRELIMINAIRES, PROGRAMME_TECHNIQUE
    PHASE_GROUPE,         // Couvre : POULE (water-polo)
    QUART_DE_FINALE,      // Water-polo uniquement
    DEMI_FINALE,          // Natation, Water-polo, Plongeon
    PETITE_FINALE,        // Match pour 3e place (water-polo)
    FINALE                // Toutes disciplines
}
