package com.ciblorgasport.resultatsservice.dto.response;

public class PublicationResponseDTO {

    private Long epreuveId;
    private int nbResultatsPublies;
    private boolean success;

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }

    public int getNbResultatsPublies() { return nbResultatsPublies; }
    public void setNbResultatsPublies(int nbResultatsPublies) { this.nbResultatsPublies = nbResultatsPublies; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
