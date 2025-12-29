package com.ciblorgasport.billetterie.ticket;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
  private final TicketRepository repo;

  public TicketService(TicketRepository repo) {
    this.repo = repo;
  }

  public List<Ticket> listBySpectator(String spectatorId) {
    return repo.findBySpectatorId(spectatorId);
  }

  @Transactional
  public Ticket create(String spectatorId, String eventId, String seat) {
    Ticket t = new Ticket();
    t.setSpectatorId(spectatorId);
    t.setEventId(eventId);
    t.setSeat(seat);
    t.setIssuedAt(LocalDateTime.now());
    t.setQrCode("QR-" + UUID.randomUUID());
    return repo.save(t);
  }
}
