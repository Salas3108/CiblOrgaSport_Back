package com.ciblorgasport.billetterie.service;

import com.ciblorgasport.billetterie.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    List<Ticket> findAll();
    Optional<Ticket> findById(Long id);
    Ticket create(Ticket ticket);
    Ticket update(Long id, Ticket ticket);
    void delete(Long id);
    double calculatePrice(String category);
}
