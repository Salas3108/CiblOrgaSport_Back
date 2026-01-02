package com.ciblorgasport.billetterie.controller;

import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ticketControllerApi")
@RequestMapping("/api/tickets")
public class TicketController {

	private final TicketService ticketService;

	public TicketController(TicketService ticketService) {
		this.ticketService = ticketService;
	}

	@GetMapping
	public ResponseEntity<List<Ticket>> findAll() {
		return ResponseEntity.ok(ticketService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Ticket> findById(@PathVariable Long id) {
		return ticketService.findById(id)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
		return ResponseEntity.ok(ticketService.create(ticket));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Ticket> update(@PathVariable Long id, @RequestBody Ticket ticket) {
		return ResponseEntity.ok(ticketService.update(id, ticket));
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
}
