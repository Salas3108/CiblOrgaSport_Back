package com.ciblorgasport.participantsservice.dto.request;

import com.ciblorgasport.participantsservice.dto.AthleteDocsDto;

/**
 * Requête Athlète : mettre à jour ses documents.
 */
public class UpdateAthleteDocsRequest {
    private AthleteDocsDto docs;

    public UpdateAthleteDocsRequest() {
    }

    public AthleteDocsDto getDocs() {
        return docs;
    }

    public void setDocs(AthleteDocsDto docs) {
        this.docs = docs;
    }
}
