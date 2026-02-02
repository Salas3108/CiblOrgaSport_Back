package com.ciblorgasport.participantsservice.repository;

import java.util.List;

import com.ciblorgasport.participantsservice.model.Message;

public interface MessageRepository {
    List<Message> findAll();
    Message save(Message message);
}
