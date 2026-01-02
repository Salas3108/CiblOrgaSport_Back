package com.ciblorgasport.billetterie.service.impl;

import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.repository.TicketRepository;
import com.ciblorgasport.billetterie.service.TicketService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

	private final TicketRepository ticketRepository;

	public TicketServiceImpl(TicketRepository ticketRepository) {
		this.ticketRepository = ticketRepository;
	}

	@Override
	public List<Ticket> findAll() {
		return ticketRepository.findAll();
	}

	@Override
	public Optional<Ticket> findById(Long id) {
		return ticketRepository.findById(id);
	}

	@Override
	public Ticket create(Ticket ticket) {
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
		Double base = ticketRepository.findBasePriceByCategory(category);
		return base != null ? base : 0.0;
	}
}
