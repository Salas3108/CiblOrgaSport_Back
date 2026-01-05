package com.ciblorgasport.billetterie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

// Change the entity name to avoid collision with com.ciblorgasport.billetterie.ticket.Ticket
@Entity(name = "TicketModel")
public class Ticket {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String category;

	private Double basePrice;
	// External references to other microservices (no DB foreign keys)
	private Long spectatorId;
	private Long epreuveId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public Long getSpectatorId() {
		return spectatorId;
	}

	public void setSpectatorId(Long spectatorId) {
		this.spectatorId = spectatorId;
	}

	public Long getEpreuveId() {
		return epreuveId;
	}

	public void setEpreuveId(Long epreuveId) {
		this.epreuveId = epreuveId;
	}
}