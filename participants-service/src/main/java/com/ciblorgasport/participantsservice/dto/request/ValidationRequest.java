package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requête Commissaire : valider/refuser un athlète.
 * - valide=true  => athlète validé
 * - valide=false => refus + motifRefus (ex: "passeport expiré")
 *
 * Le champ message permet de garder un historique (ex: "passport expiré").
 */
public class ValidationRequest {
    private boolean valide;
    private String message;
    private String motifRefus;

    public ValidationRequest() {
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMotifRefus() {
        return motifRefus;
    }

    public void setMotifRefus(String motifRefus) {
        this.motifRefus = motifRefus;
    }
}
