package com.ciblorgasport.participantsservice.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ciblorgasport.participantsservice.model.Message;

/**
 * Adapter pour exposer JpaMessageRepository via l'interface MessageRepository.
 */
@Repository
public class JpaMessageAdapter implements MessageRepository {

    private final JpaMessageRepository delegate;

    public JpaMessageAdapter(JpaMessageRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Message> findAll() {
        return delegate.findAll();
    }

    @Override
    public Message save(Message message) {
        return delegate.save(message);
    }
}
