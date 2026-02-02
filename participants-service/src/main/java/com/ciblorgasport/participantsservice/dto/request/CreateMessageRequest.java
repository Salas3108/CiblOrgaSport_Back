package com.ciblorgasport.participantsservice.dto.request;

/**
 * Requête Commissaire : ajouter un message/annotation.
 */
public class CreateMessageRequest {
    private String contenu;

    public CreateMessageRequest() {
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
