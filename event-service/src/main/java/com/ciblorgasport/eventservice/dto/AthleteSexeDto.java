package com.ciblorgasport.eventservice.dto;

/**
 * DTO interne reçu depuis participants-service pour les vérifications d'éligibilité de genre.
 */
public class AthleteSexeDto {
    private Long id;
    private String sexe;
    private boolean valide;

    public AthleteSexeDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }
}
