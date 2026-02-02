package com.ciblorgasport.participantsservice.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO global aligné avec le mock front :
 * {
 *   "athletes": [...],
 *   "messages": [...],
 *   "logs": [...]
 * }
 */
public class ParticipantsMockDbDto {
    private List<AthleteDto> athletes = new ArrayList<>();
    private List<MessageDto> messages = new ArrayList<>();
    private List<String> logs = new ArrayList<>();

    public ParticipantsMockDbDto() {
    }

    public List<AthleteDto> getAthletes() {
        return athletes;
    }

    public void setAthletes(List<AthleteDto> athletes) {
        this.athletes = athletes;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDto> messages) {
        this.messages = messages;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
