package com.ciblorgasport.participantsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Basic;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;

/**
 * Documents d'un athlète. Stockés en binaire (BYTEA PostgreSQL).
 */
@Embeddable
public class AthleteDocs {
     @Lob
     @Basic(fetch = FetchType.LAZY)
     @Column(name = "certificat_medical", columnDefinition = "bytea")
     private byte[] certificatMedical;

     @Lob
     @Basic(fetch = FetchType.LAZY)
     @Column(name = "passport", columnDefinition = "bytea")
     private byte[] passport;

    public AthleteDocs() {
    }

    public AthleteDocs(byte[] certificatMedical, byte[] passport) {
        this.certificatMedical = certificatMedical;
        this.passport = passport;
    }

    public byte[] getCertificatMedical() {
        return certificatMedical;
    }

    public void setCertificatMedical(byte[] certificatMedical) {
        this.certificatMedical = certificatMedical;
    }

    public byte[] getPassport() {
        return passport;
    }

    public void setPassport(byte[] passport) {
        this.passport = passport;
    }
}
