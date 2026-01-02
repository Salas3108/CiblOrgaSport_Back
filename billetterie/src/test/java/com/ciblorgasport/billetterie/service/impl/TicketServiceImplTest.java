package com.ciblorgasport.billetterie.service.impl;

import com.ciblorgasport.billetterie.model.Ticket;
import com.ciblorgasport.billetterie.repository.TicketRepository;
import com.ciblorgasport.billetterie.service.impl.TicketServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        testTicket = new Ticket();
        testTicket.setId(1L);
        testTicket.setCategory("VIP");
        testTicket.setBasePrice(100.0);
    }

    @Test
    void findAll_ShouldReturnAllTickets() {
        // Given
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setCategory("Standard");
        ticket2.setBasePrice(50.0);
        
        List<Ticket> expectedTickets = Arrays.asList(testTicket, ticket2);
        when(ticketRepository.findAll()).thenReturn(expectedTickets);

        // When
        List<Ticket> result = ticketService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(expectedTickets);
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyListWhenNoTickets() {
        // Given
        when(ticketRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Ticket> result = ticketService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnTicketWhenExists() {
        // Given
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(testTicket));

        // When
        Optional<Ticket> result = ticketService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testTicket);
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCategory()).isEqualTo("VIP");
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmptyWhenNotExists() {
        // Given
        when(ticketRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When
        Optional<Ticket> result = ticketService.findById(999L);

        // Then
        assertThat(result).isEmpty();
        verify(ticketRepository, times(1)).findById(999L);
    }

    @Test
    void create_ShouldSaveAndReturnTicket() {
        // Given
        Ticket newTicket = new Ticket();
        newTicket.setCategory("Premium");
        newTicket.setBasePrice(75.0);
        
        Ticket savedTicket = new Ticket();
        savedTicket.setId(3L);
        savedTicket.setCategory("Premium");
        savedTicket.setBasePrice(75.0);
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // When
        Ticket result = ticketService.create(newTicket);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getCategory()).isEqualTo("Premium");
        assertThat(result.getBasePrice()).isEqualTo(75.0);
        verify(ticketRepository, times(1)).save(newTicket);
    }

    @Test
    void update_ShouldSetIdAndSaveTicket() {
        // Given
        Ticket updateTicket = new Ticket();
        updateTicket.setCategory("Economy");
        updateTicket.setBasePrice(25.0);
        
        Ticket savedTicket = new Ticket();
        savedTicket.setId(1L);
        savedTicket.setCategory("Economy");
        savedTicket.setBasePrice(25.0);
        
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);

        // When
        Ticket result = ticketService.update(1L, updateTicket);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCategory()).isEqualTo("Economy");
        assertThat(result.getBasePrice()).isEqualTo(25.0);
        
        // Verify that the ID was set on the input ticket
        assertThat(updateTicket.getId()).isEqualTo(1L);
        verify(ticketRepository, times(1)).save(updateTicket);
    }

    @Test
    void delete_ShouldCallRepositoryDeleteById() {
        // Given
        Long ticketId = 1L;

        // When
        ticketService.delete(ticketId);

        // Then
        verify(ticketRepository, times(1)).deleteById(ticketId);
    }

    @Test
    void calculatePrice_ShouldReturnBasePriceWhenCategoryExists() {
        // Given
        String category = "VIP";
        Double expectedPrice = 100.0;
        when(ticketRepository.findBasePriceByCategory(category)).thenReturn(expectedPrice);

        // When
        double result = ticketService.calculatePrice(category);

        // Then
        assertThat(result).isEqualTo(100.0);
        verify(ticketRepository, times(1)).findBasePriceByCategory(category);
    }

    @Test
    void calculatePrice_ShouldReturnZeroWhenCategoryNotExists() {
        // Given
        String category = "NonExistent";
        when(ticketRepository.findBasePriceByCategory(category)).thenReturn(null);

        // When
        double result = ticketService.calculatePrice(category);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(ticketRepository, times(1)).findBasePriceByCategory(category);
    }

    @Test
    void calculatePrice_ShouldHandleEmptyCategory() {
        // Given
        String category = "";
        when(ticketRepository.findBasePriceByCategory(category)).thenReturn(null);

        // When
        double result = ticketService.calculatePrice(category);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(ticketRepository, times(1)).findBasePriceByCategory(category);
    }

    @Test
    void calculatePrice_ShouldHandleNullCategory() {
        // Given
        when(ticketRepository.findBasePriceByCategory(null)).thenReturn(null);

        // When
        double result = ticketService.calculatePrice(null);

        // Then
        assertThat(result).isEqualTo(0.0);
        verify(ticketRepository, times(1)).findBasePriceByCategory(null);
    }
}
