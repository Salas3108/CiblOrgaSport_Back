package com.ciblorgasport.participantsservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Documents d'un athlète. Stockés en binaire (BYTEA PostgreSQL).
 */
@Embeddable
public class AthleteDocs {
     @Column(name = "certificat_medical", columnDefinition = "bytea")
     private byte[] certificatMedical;

     @Column(name = "passport", columnDefinition = "bytea")
     private byte[] passport;

    @Column(name = "document_genre")
    private String documentGenre;

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

    public String getDocumentGenre() {
        return documentGenre;
    }

    public void setDocumentGenre(String documentGenre) {
        this.documentGenre = documentGenre;
    }
}
