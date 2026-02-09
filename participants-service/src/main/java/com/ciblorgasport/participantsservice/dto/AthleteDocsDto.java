package com.ciblorgasport.participantsservice.dto;

/**
 * DTO Docs (même structure que le mock front : docs.certificatMedical + docs.passport).
 */
public class AthleteDocsDto {
    private String certificatMedical;
    private String passport;

    public AthleteDocsDto() {
    }

    public AthleteDocsDto(String certificatMedical, String passport) {
        this.certificatMedical = certificatMedical;
        this.passport = passport;
    }

    public String getCertificatMedical() {
        return certificatMedical;
    }

    public void setCertificatMedical(String certificatMedical) {
        this.certificatMedical = certificatMedical;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }
}
