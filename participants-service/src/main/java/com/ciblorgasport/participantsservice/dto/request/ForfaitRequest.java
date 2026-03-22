package com.ciblorgasport.participantsservice.dto.request;

import java.util.Map;

public class ForfaitRequest {

    private Map<String, Object> detailsPerformance;

    public Map<String, Object> getDetailsPerformance() {
        return detailsPerformance;
    }

    public void setDetailsPerformance(Map<String, Object> detailsPerformance) {
        this.detailsPerformance = detailsPerformance;
    }
}
