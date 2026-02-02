package com.ciblorgasport.participantsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Documents d'un athlète. Embeddable JPA pour stocker les noms de fichiers.
 */
@Embeddable
public class AthleteDocs {
    @Column(name = "certificat_medical")
    private String certificatMedical;

    @Column(name = "passport")
    private String passport;

    public AthleteDocs() {
    }

    public AthleteDocs(String certificatMedical, String passport) {
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
