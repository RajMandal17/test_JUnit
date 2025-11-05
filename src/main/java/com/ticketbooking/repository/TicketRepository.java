package com.ticketbooking.repository;

import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Ticket entity
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * Find tickets by event name
     */
    List<Ticket> findByEventName(String eventName);
    
    /**
     * Find tickets by venue
     */
    List<Ticket> findByVenue(String venue);
    
    /**
     * Find tickets by ticket type
     */
    List<Ticket> findByTicketType(TicketType ticketType);
    
    /**
     * Find active tickets
     */
    List<Ticket> findByActiveTrue();
    
    /**
     * Find tickets with available quantity greater than zero
     */
    List<Ticket> findByAvailableQuantityGreaterThan(Integer quantity);
    
    /**
     * Find tickets by event date between two dates
     */
    List<Ticket> findByEventDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find tickets by price range
     */
    List<Ticket> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find available tickets for an event (active and has available quantity)
     */
    @Query("SELECT t FROM Ticket t WHERE t.eventName = :eventName AND t.active = true AND t.availableQuantity > 0")
    List<Ticket> findAvailableTicketsByEvent(@Param("eventName") String eventName);
    
    /**
     * Find upcoming events (event date after current time)
     */
    @Query("SELECT t FROM Ticket t WHERE t.eventDate > :currentDate AND t.active = true")
    List<Ticket> findUpcomingEvents(@Param("currentDate") LocalDateTime currentDate);
}
