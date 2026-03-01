package com.ciblorgasport.eventservice.dto;

import org.springframework.stereotype.Component;
import com.ciblorgasport.eventservice.model.Event;

@Component
public class EventMapper {

    public EventDTO toDto(Event event) {
        if (event == null) return null;
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDateDebut(event.getDateDebut());
        dto.setDateFin(event.getDateFin());
        dto.setDescription(event.getDescription());
        dto.setPaysHote(event.getPaysHote());
        return dto;
    }

    public Event toEntity(EventDTO dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDateDebut(dto.getDateDebut());
        event.setDateFin(dto.getDateFin());
        event.setDescription(dto.getDescription());
        event.setPaysHote(dto.getPaysHote());
        return event;
    }

    public void updateEntityFromDto(Event event, EventDTO dto) {
        if (event == null || dto == null) return;
        event.setName(dto.getName());
        event.setDateDebut(dto.getDateDebut());
        event.setDateFin(dto.getDateFin());
        event.setDescription(dto.getDescription());
        event.setPaysHote(dto.getPaysHote());
    }
}
