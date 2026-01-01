package com.ciblorgasport.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Epreuve> epreuves = new ArrayList<>();

    public Competition() {}

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public List<Epreuve> getEpreuves() { return epreuves; }
    @JsonProperty("eventId")
    public UUID getEventId() {
        return event != null ? event.getId() : null;
    }
}
