package com.ciblorgasport.billetterie.service.impl;

import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    
    @InjectMocks
    private TicketServiceImpl ticketService;

    @Test
    void findAll_ReturnsAllTickets() {
        // Arrange
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        
        List<Ticket> tickets = Arrays.asList(ticket1, ticket2);
        when(ticketRepository.findAll()).thenReturn(tickets);

        // Act
        List<Ticket> result = ticketService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ReturnsTicket() {
        // Arrange
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        // Act
        Optional<Ticket> result = ticketService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void findById_WhenNotExists_ReturnsEmpty() {
        // Arrange
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Ticket> result = ticketService.findById(1L);

        // Assert
        assertFalse(result.isPresent());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void create_SavesAndReturnsTicket() {
        // Arrange
        Ticket ticketToSave = new Ticket();
        ticketToSave.setCategory("Standard");
        ticketToSave.setBasePrice(50.0);
        
        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setCategory("Standard");
        savedTicket.setBasePrice(50.0);
        
        when(ticketRepository.save(ticketToSave)).thenReturn(savedTicket);

        // Act
        Ticket result = ticketService.create(ticketToSave);

        // Assert
        assertNotNull(result.getId());
        assertEquals("Standard", result.getCategory());
        verify(ticketRepository, times(1)).save(ticketToSave);
    }

    @Test
    void update_SetsIdAndSaves() {
        // Arrange
        Ticket ticketUpdate = new Ticket();
        ticketUpdate.setCategory("Updated");
        ticketUpdate.setBasePrice(75.0);
        
        Ticket updatedTicket = new Ticket();
        updatedTicket.setId(1L);
        updatedTicket.setCategory("Updated");
        updatedTicket.setBasePrice(75.0);
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(updatedTicket);

        // Act
        Ticket result = ticketService.update(1L, ticketUpdate);

        // Assert
        assertEquals(1L, result.getId());
        assertEquals("Updated", result.getCategory());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void delete_CallsRepositoryDelete() {
        // Act
        ticketService.delete(1L);

        // Assert
        verify(ticketRepository, times(1)).deleteById(1L);
    }

    @Test
    void calculatePrice_WhenCategoryExists_ReturnsPrice() {
        // Arrange
        String category = "VIP";
        Double basePrice = 150.0;
        
        when(ticketRepository.findBasePriceByCategory(category)).thenReturn(basePrice);

        // Act
        double result = ticketService.calculatePrice(category);

        // Assert
        assertEquals(150.0, result);
        verify(ticketRepository, times(1)).findBasePriceByCategory(category);
    }

    @Test
    void calculatePrice_WhenCategoryNotExists_ReturnsZero() {
        // Arrange
        String category = "Unknown";
        
        when(ticketRepository.findBasePriceByCategory(category)).thenReturn(null);

        // Act
        double result = ticketService.calculatePrice(category);

        // Assert
        assertEquals(0.0, result);
        verify(ticketRepository, times(1)).findBasePriceByCategory(category);
    }

    @Test
    void calculatePrice_WhenNullCategory_ReturnsZero() {
        // Act
        double result = ticketService.calculatePrice(null);

        // Assert
        assertEquals(0.0, result);

    }
}