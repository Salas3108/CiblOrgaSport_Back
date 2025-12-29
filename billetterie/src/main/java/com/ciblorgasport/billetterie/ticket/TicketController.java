package com.ciblorgasport.billetterie.ticket;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController("ticketControllerInternal")
@RequestMapping("/api/tickets")
@Validated
public class TicketController {
  private final TicketService service;

  public TicketController(TicketService service) {
    this.service = service;
  }

  // En tant que spectateur, consulter mes billets
  @GetMapping
  public ResponseEntity<List<Ticket>> myTickets(@RequestParam("spectatorId") @NotBlank String spectatorId) {
    return ResponseEntity.ok(service.listBySpectator(spectatorId));
  }

  // En tant que spectateur, stocker (créer) un billet
  @PostMapping
  public ResponseEntity<Ticket> create(@RequestBody @Valid CreateTicketRequest req) {
    Ticket t = service.create(req.getSpectatorId(), req.getEventId(), req.getSeat());
    return ResponseEntity.ok(t);
  }

  public static class CreateTicketRequest {
    @NotBlank
    private String spectatorId;
    @NotBlank
    private String eventId;
    private String seat;

    public CreateTicketRequest() { /* no-args for Jackson */ }

    public CreateTicketRequest(String spectatorId, String eventId, String seat) {
      this.spectatorId = spectatorId;
      this.eventId = eventId;
      this.seat = seat;
    }

    public String getSpectatorId() { return spectatorId; }
    public void setSpectatorId(String spectatorId) { this.spectatorId = spectatorId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }
  }
}
