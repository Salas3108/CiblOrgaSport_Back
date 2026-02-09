package com.ciblorgasport.participantsservice.dto;

import org.springframework.stereotype.Component;

import com.ciblorgasport.participantsservice.model.Message;

@Component
public class MessageMapper {
    public MessageDto toDto(Message message) {
        if (message == null) return null;
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setAthleteId(message.getAthleteId());
        dto.setContenu(message.getContenu());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}
