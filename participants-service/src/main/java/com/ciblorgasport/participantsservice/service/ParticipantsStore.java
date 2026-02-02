package com.ciblorgasport.participantsservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Stockage en mémoire du mock (messages + logs).
 *
 * On sépare ça des "athletes" qui sont dans un repository dédié.
 */
@Component
public class ParticipantsStore {

    private final List<String> logs = new ArrayList<>();

    public List<String> getLogs() {
        return logs;
    }

    public void addLog(String log) {
        logs.add(log);
    }

    public void clear() {
        logs.clear();
    }
}
