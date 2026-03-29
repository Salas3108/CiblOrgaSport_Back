package com.ciblorgasport.billetterie.controller;

import com.ciblorgasport.billetterie.client.AuthServiceClient;
import com.ciblorgasport.billetterie.client.EventServiceClient;
import com.ciblorgasport.billetterie.dto.TicketResponse;
import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("ticketControllerApi")
@RequestMapping("/api/tickets")
public class TicketController {

	private final TicketService ticketService;
	private final AuthServiceClient authServiceClient;
	private final EventServiceClient eventServiceClient;

	public TicketController(TicketService ticketService, AuthServiceClient authServiceClient, EventServiceClient eventServiceClient) {
		this.ticketService = ticketService;
		this.authServiceClient = authServiceClient;
		this.eventServiceClient = eventServiceClient;
	}

	@GetMapping
	public ResponseEntity<List<TicketResponse>> findAll(@RequestParam(required = false) Long spectatorId) {
		List<Ticket> tickets;
		if (spectatorId != null) {
			tickets = ticketService.findBySpectatorId(spectatorId);
		} else {
			tickets = ticketService.findAll();
		}
		List<TicketResponse> responses = tickets.stream().map(this::toResponse).collect(Collectors.toList());
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TicketResponse> findById(@PathVariable Long id) {
		return ticketService.findById(id)
			.map(this::toResponse)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Ticket ticket) {
		try {
			Ticket saved = ticketService.create(ticket);
			return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<TicketResponse> update(@PathVariable Long id, @RequestBody Ticket ticket) {
		Ticket updated = ticketService.update(id, ticket);
		return ResponseEntity.ok(toResponse(updated));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		ticketService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/price")
	public ResponseEntity<Double> priceByCategory(@RequestParam String category) {
		return ResponseEntity.ok(ticketService.calculatePrice(category));
	}

	private TicketResponse toResponse(Ticket ticket) {
		TicketResponse r = new TicketResponse();
		r.setId(ticket.getId());
		r.setCategory(ticket.getCategory());
		r.setBasePrice(ticket.getBasePrice());
		r.setSpectatorId(ticket.getSpectatorId());
		r.setEpreuveId(ticket.getEpreuveId());
		Map spectator = authServiceClient.fetchSpectatorById(ticket.getSpectatorId());
		Map event = eventServiceClient.fetchEventById(ticket.getEpreuveId());
		r.setSpectator(spectator);
		r.setEvent(event);
		return r;
	}
}
