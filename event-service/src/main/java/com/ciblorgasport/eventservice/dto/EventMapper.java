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
        dto.setDateDebut(event.getDateDebut());
        dto.setDateFin(event.getDateFin());
        Lieu lieu = event.getLieuPrincipal();
        dto.setLieuPrincipalId(lieu != null ? lieu.getId() : null);
        return dto;
    }

    public Event toEntity(EventDTO dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDateDebut(dto.getDateDebut());
        event.setDateFin(dto.getDateFin());
        if (dto.getLieuPrincipalId() != null) {
            Optional<Lieu> opt = lieuRepository.findById(dto.getLieuPrincipalId());
            opt.ifPresent(event::setLieuPrincipal);
        } else {
            event.setLieuPrincipal(null);
        }
        return event;
    }

    public void updateEntityFromDto(Event event, EventDTO dto) {
        if (event == null || dto == null) return;
        event.setName(dto.getName());
        event.setDateDebut(dto.getDateDebut());
        event.setDateFin(dto.getDateFin());
        if (dto.getLieuPrincipalId() != null) {
            Optional<Lieu> opt = lieuRepository.findById(dto.getLieuPrincipalId());
            opt.ifPresent(event::setLieuPrincipal);
        } else {
            event.setLieuPrincipal(null);
        }
    }
}
