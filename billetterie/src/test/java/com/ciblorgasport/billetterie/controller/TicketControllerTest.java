package com.ciblorgasport.billetterie.controller;

import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    private TicketService ticketService;
    
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        ticketController = new TicketController(ticketService);
    }

    @Test
    void findAll_ReturnsAllTickets() {
        // Arrange
        Ticket ticket1 = mock(Ticket.class);
        Ticket ticket2 = mock(Ticket.class);
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);
        
        when(ticketService.findAll()).thenReturn(tickets);

        // Act
        ResponseEntity<List<Ticket>> response = ticketController.findAll();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(ticketService, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ReturnsTicket() {
        // Arrange
        Ticket ticket = mock(Ticket.class);
        when(ticketService.findById(1L)).thenReturn(Optional.of(ticket));

        // Act
        ResponseEntity<Ticket> response = ticketController.findById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertSame(ticket, response.getBody());
        verify(ticketService, times(1)).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ReturnsNotFound() {
        // Arrange
        when(ticketService.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Ticket> response = ticketController.findById(1L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(ticketService, times(1)).findById(1L);
    }

    @Test
    void create_ValidTicket_ReturnsCreated() {
        // Arrange
        Ticket ticketToCreate = mock(Ticket.class);
        Ticket createdTicket = mock(Ticket.class);
        
        when(ticketService.create(ticketToCreate)).thenReturn(createdTicket);

        // Act
        ResponseEntity<Ticket> response = ticketController.create(ticketToCreate);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertSame(createdTicket, response.getBody());
        verify(ticketService, times(1)).create(ticketToCreate);
    }

    @Test
    void update_ValidTicket_ReturnsUpdated() {
        // Arrange
        Ticket ticketUpdate = mock(Ticket.class);
        Ticket updatedTicket = mock(Ticket.class);
        
        when(ticketService.update(1L, ticketUpdate)).thenReturn(updatedTicket);

        // Act
        ResponseEntity<Ticket> response = ticketController.update(1L, ticketUpdate);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertSame(updatedTicket, response.getBody());
        verify(ticketService, times(1)).update(1L, ticketUpdate);
    }

    @Test
    void delete_Always_ReturnsNoContent() {
        // Act
        ResponseEntity<Void> response = ticketController.delete(1L);

        // Assert
        assertEquals(204, response.getStatusCodeValue());
        verify(ticketService, times(1)).delete(1L);
    }

    @Test
    void priceByCategory_ValidCategory_ReturnsPrice() {
        // Arrange
        String category = "VIP";
        double expectedPrice = 150.0;
        
        when(ticketService.calculatePrice(category)).thenReturn(expectedPrice);

        // Act
        ResponseEntity<Double> response = ticketController.priceByCategory(category);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPrice, response.getBody());
        verify(ticketService, times(1)).calculatePrice(category);
    }

    @Test
    void priceByCategory_NullCategory_ReturnsPrice() {
        // Arrange
        double expectedPrice = 0.0;
        
        when(ticketService.calculatePrice(null)).thenReturn(expectedPrice);

        // Act
        ResponseEntity<Double> response = ticketController.priceByCategory(null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0.0, response.getBody());
    }
}