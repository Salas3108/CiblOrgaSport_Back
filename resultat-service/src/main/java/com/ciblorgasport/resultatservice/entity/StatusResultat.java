package com.ciblorgasport.resultatservice.entity;

public enum StatusResultat {
    SAISI,           // Résultat saisi mais non validé
    VALIDE,          // Résultat validé par un administrateur
    REJET,           // Résultat rejeté
    EN_CORRECTION    // Résultat en attente de correction
}
