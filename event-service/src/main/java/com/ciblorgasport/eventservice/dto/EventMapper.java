package com.ciblorgasport.eventservice.dto;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import com.ciblorgasport.eventservice.model.Event;
import com.ciblorgasport.eventservice.model.Lieu;
import com.ciblorgasport.eventservice.repository.LieuRepository;

import java.util.Optional;

@Component
public class EventMapper {

    @Autowired
    private LieuRepository lieuRepository;

    public EventDTO toDto(Event event) {
        if (event == null) return null;
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        Lieu lieu = event.getLieuPrincipal();
        dto.setLieuId(lieu != null ? lieu.getId() : null);
        return dto;
    }

    public Event toEntity(EventDTO dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        if (dto.getLieuId() != null) {
            Optional<Lieu> opt = lieuRepository.findById(dto.getLieuId());
            opt.ifPresent(event::setLieuPrincipal);
        } else {
            event.setLieuPrincipal(null);
        }
        return event;
    }

    public void updateEntityFromDto(Event event, EventDTO dto) {
        if (event == null || dto == null) return;
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        if (dto.getLieuId() != null) {
            Optional<Lieu> opt = lieuRepository.findById(dto.getLieuId());
            opt.ifPresent(event::setLieuPrincipal);
        } else {
            event.setLieuPrincipal(null);
        }
    }
}
