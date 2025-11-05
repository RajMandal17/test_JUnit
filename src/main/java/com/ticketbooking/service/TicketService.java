package com.ticketbooking.service;

import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.exception.TicketNotAvailableException;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.TicketType;
import com.ticketbooking.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Ticket operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    
    private final TicketRepository ticketRepository;
    
    /**
     * Create a new ticket
     */
    @Transactional
    public Ticket createTicket(Ticket ticket) {
        log.info("Creating ticket for event: {}", ticket.getEventName());
        
        if (ticket.getEventDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Get ticket by ID
     */
    public Ticket getTicketById(Long id) {
        log.info("Fetching ticket with id: {}", id);
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }
    
    /**
     * Get all tickets
     */
    public List<Ticket> getAllTickets() {
        log.info("Fetching all tickets");
        return ticketRepository.findAll();
    }
    
    /**
     * Get tickets by event name
     */
    public List<Ticket> getTicketsByEventName(String eventName) {
        log.info("Fetching tickets for event: {}", eventName);
        return ticketRepository.findByEventName(eventName);
    }
    
    /**
     * Get tickets by venue
     */
    public List<Ticket> getTicketsByVenue(String venue) {
        log.info("Fetching tickets for venue: {}", venue);
        return ticketRepository.findByVenue(venue);
    }
    
    /**
     * Get tickets by type
     */
    public List<Ticket> getTicketsByType(TicketType ticketType) {
        log.info("Fetching tickets of type: {}", ticketType);
        return ticketRepository.findByTicketType(ticketType);
    }
    
    /**
     * Get available tickets
     */
    public List<Ticket> getAvailableTickets() {
        log.info("Fetching available tickets");
        return ticketRepository.findByAvailableQuantityGreaterThan(0);
    }
    
    /**
     * Get upcoming events
     */
    public List<Ticket> getUpcomingEvents() {
        log.info("Fetching upcoming events");
        return ticketRepository.findUpcomingEvents(LocalDateTime.now());
    }
    
    /**
     * Get tickets by price range
     */
    public List<Ticket> getTicketsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching tickets with price between {} and {}", minPrice, maxPrice);
        return ticketRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Update ticket
     */
    @Transactional
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        log.info("Updating ticket with id: {}", id);
        
        Ticket ticket = getTicketById(id);
        
        ticket.setEventName(ticketDetails.getEventName());
        ticket.setVenue(ticketDetails.getVenue());
        ticket.setEventDate(ticketDetails.getEventDate());
        ticket.setPrice(ticketDetails.getPrice());
        ticket.setAvailableQuantity(ticketDetails.getAvailableQuantity());
        
        return ticketRepository.save(ticket);
    }
    
    /**
     * Reserve tickets (reduce available quantity)
     */
    @Transactional
    public void reserveTickets(Long ticketId, int quantity) {
        log.info("Reserving {} tickets for ticket id: {}", quantity, ticketId);
        
        Ticket ticket = getTicketById(ticketId);
        
        if (!ticket.hasAvailableTickets(quantity)) {
            throw new TicketNotAvailableException(
                String.format("Not enough tickets available. Requested: %d, Available: %d", 
                    quantity, ticket.getAvailableQuantity())
            );
        }
        
        ticket.reserveTickets(quantity);
        ticketRepository.save(ticket);
    }
    
    /**
     * Release tickets (increase available quantity)
     */
    @Transactional
    public void releaseTickets(Long ticketId, int quantity) {
        log.info("Releasing {} tickets for ticket id: {}", quantity, ticketId);
        
        Ticket ticket = getTicketById(ticketId);
        ticket.releaseTickets(quantity);
        ticketRepository.save(ticket);
    }
    
    /**
     * Delete ticket
     */
    @Transactional
    public void deleteTicket(Long id) {
        log.info("Deleting ticket with id: {}", id);
        
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }
    
    /**
     * Check if ticket is available
     */
    public boolean isTicketAvailable(Long ticketId, int quantity) {
        Ticket ticket = getTicketById(ticketId);
        return ticket.hasAvailableTickets(quantity);
    }
    
    /**
     * Calculate total price for tickets
     */
    public BigDecimal calculateTotalPrice(Long ticketId, int quantity) {
        Ticket ticket = getTicketById(ticketId);
        return ticket.calculateTotalPrice(quantity);
    }
}
