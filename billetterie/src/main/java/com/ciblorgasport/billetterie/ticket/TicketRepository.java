package com.ciblorgasport.billetterie.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
  List<Ticket> findBySpectatorId(String spectatorId);
}
