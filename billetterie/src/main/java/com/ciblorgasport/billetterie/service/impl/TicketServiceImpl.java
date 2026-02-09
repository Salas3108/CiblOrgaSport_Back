package com.ciblorgasport.billetterie.service.impl;

import com.ciblorgasport.billetterie.client.AuthServiceClient;
import com.ciblorgasport.billetterie.client.EventServiceClient;
import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.repository.TicketRepository;
import com.ciblorgasport.billetterie.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired TicketRepository ticketRepository;
    @Autowired AuthServiceClient authServiceClient;
    @Autowired EventServiceClient eventServiceClient;

    @Override
    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> findBySpectatorId(Long spectatorId) {
        return ticketRepository.findBySpectatorId(spectatorId);
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public Ticket create(Ticket ticket) {
        // validate external references
        if (ticket.getSpectatorId() != null) {
            System.out.println("Comparing spectatorId with userId in auth-service");
            boolean ok = authServiceClient.existsById(ticket.getSpectatorId());
            if (!ok) throw new IllegalArgumentException("Spectator/User not found: " + ticket.getSpectatorId());
        }
        if (ticket.getEpreuveId() != null) {
            boolean ok = eventServiceClient.existsById(ticket.getEpreuveId());
            if (!ok) throw new IllegalArgumentException("Epreuve/Event not found: " + ticket.getEpreuveId());
        }
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket update(Long id, Ticket ticket) {
        ticket.setId(id);
        return ticketRepository.save(ticket);
    }

    @Override
    public void delete(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public double calculatePrice(String category) {
        Double basePrice = ticketRepository.findBasePriceByCategory(category);
        return basePrice != null ? basePrice : 0.0;
    }
}
