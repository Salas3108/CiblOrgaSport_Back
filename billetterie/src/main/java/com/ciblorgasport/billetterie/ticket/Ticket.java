package com.ciblorgasport.billetterie.ticket;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String spectatorId; // e.g., user UUID

  @Column(nullable = false)
  private String eventId;

  @Column(nullable = false, unique = true)
  private String qrCode; // unique code to present at entry

  @Column(nullable = false)
  private LocalDateTime issuedAt;

  private String seat; // optional

  @Column(name = "base_price")
  private Double basePrice;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getSpectatorId() { return spectatorId; }
  public void setSpectatorId(String spectatorId) { this.spectatorId = spectatorId; }

  public String getEventId() { return eventId; }
  public void setEventId(String eventId) { this.eventId = eventId; }

  public String getQrCode() { return qrCode; }
  public void setQrCode(String qrCode) { this.qrCode = qrCode; }

  public LocalDateTime getIssuedAt() { return issuedAt; }
  public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

  public String getSeat() { return seat; }
  public void setSeat(String seat) { this.seat = seat; }

  public Double getBasePrice() { return basePrice; }
  public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
}
