package com.ciblorgasport.billetterie.service;

import java.util.Optional;
import java.util.List;
import com.ciblorgasport.billetterie.model.Ticket;

public interface TicketService {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
    Ticket create(Ticket ticket);
    Ticket update(Long id, Ticket ticket);
    void delete(Long id);
    double calculatePrice(String category);
}
