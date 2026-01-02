package com.ciblorgasport.billetterie.service;

import com.ciblorgasport.billetterie.repository.TicketRepository;
import com.ciblorgasport.billetterie.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TicketServiceTest {

	@Mock
	private TicketRepository ticketRepository;

	@InjectMocks
	private TicketServiceImpl ticketService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldReturnPriceForCategory() {
		String category = "VIP";
		when(ticketRepository.findBasePriceByCategory(category)).thenReturn(100.0);

		double price = ticketService.calculatePrice(category);

		assertThat(price).isEqualTo(100.0);
	}
}
