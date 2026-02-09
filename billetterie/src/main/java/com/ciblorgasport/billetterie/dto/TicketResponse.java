package com.ciblorgasport.billetterie.dto;

import java.util.Map;

public class TicketResponse {
    private Long id;
    private String category;
    private Double basePrice;
    private Long spectatorId;
    private Long epreuveId;
    private Map<String, Object> spectator;
    private Map<String, Object> event;

    public TicketResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Long getSpectatorId() { return spectatorId; }
    public void setSpectatorId(Long spectatorId) { this.spectatorId = spectatorId; }

    public Long getEpreuveId() { return epreuveId; }
    public void setEpreuveId(Long epreuveId) { this.epreuveId = epreuveId; }

    public Map<String, Object> getSpectator() { return spectator; }
    public void setSpectator(Map<String, Object> spectator) { this.spectator = spectator; }

    public Map<String, Object> getEvent() { return event; }
    public void setEvent(Map<String, Object> event) { this.event = event; }
}
