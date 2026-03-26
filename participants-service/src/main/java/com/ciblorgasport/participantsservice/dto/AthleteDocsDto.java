package com.ciblorgasport.participantsservice.dto;

/**
 * DTO Docs avec URLs de téléchargement des PDFs.
 */
public class AthleteDocsDto {
    private String certificatMedicalUrl;
    private String passportUrl;
    private String documentGenre;

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

    public String getDocumentGenre() {
        return documentGenre;
    }

    public void setDocumentGenre(String documentGenre) {
        this.documentGenre = documentGenre;
    }
}
