package com.ciblorgasport.participantsservice.dto;

/**
 * DTO Docs avec URLs de téléchargement des PDFs.
 */
public class AthleteDocsDto {
    private String certificatMedicalUrl;
    private String passportUrl;

    public AthleteDocsDto() {
    }

    public AthleteDocsDto(String certificatMedicalUrl, String passportUrl) {
        this.certificatMedicalUrl = certificatMedicalUrl;
        this.passportUrl = passportUrl;
    }

    public String getCertificatMedicalUrl() {
        return certificatMedicalUrl;
    }

    public void setCertificatMedicalUrl(String certificatMedicalUrl) {
        this.certificatMedicalUrl = certificatMedicalUrl;
    }

    public String getPassportUrl() {
        return passportUrl;
    }

    public void setPassportUrl(String passportUrl) {
        this.passportUrl = passportUrl;
    }
}
