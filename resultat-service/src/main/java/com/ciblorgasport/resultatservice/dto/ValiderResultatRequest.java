package com.ciblorgasport.resultatservice.dto;

import com.ciblorgasport.resultatservice.entity.StatusResultat;
import jakarta.validation.constraints.*;

public class ValiderResultatRequest {
    
    @NotNull(message = "Le statut est requis")
    private StatusResultat status;
    
    @Size(max = 500, message = "La raison ne doit pas dépasser 500 caractères")
    private String raison;
    
    // Getters and Setters
    public StatusResultat getStatus() {
        return status;
    }
    
    public void setStatus(StatusResultat status) {
        this.status = status;
    }
    
    public String getRaison() {
        return raison;
    }
    
    public void setRaison(String raison) {
        this.raison = raison;
    }
    
    // Constructors
    public ValiderResultatRequest() {}
    
    public ValiderResultatRequest(StatusResultat status, String raison) {
        this.status = status;
        this.raison = raison;
    }
}