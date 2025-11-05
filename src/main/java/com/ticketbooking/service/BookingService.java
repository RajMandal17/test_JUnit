package com.ticketbooking.service;

import com.ticketbooking.config.BookingConfigProperties;
import com.ticketbooking.exception.InvalidBookingException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.Booking;
import com.ticketbooking.model.BookingStatus;
import com.ticketbooking.model.Ticket;
import com.ticketbooking.model.User;
import com.ticketbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Booking operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final TicketService ticketService;
    private final BookingConfigProperties configProperties;
    
    /**
     * Create a new booking
     */
    @Transactional
    public Booking createBooking(Long userId, Long ticketId, int quantity) {
        log.info("Creating booking - User: {}, Ticket: {}, Quantity: {}", userId, ticketId, quantity);
        
        // Validate user
        User user = userService.getUserById(userId);
        if (!user.getActive()) {
            throw new InvalidBookingException("User account is not active");
        }
        
        // Validate ticket
        Ticket ticket = ticketService.getTicketById(ticketId);
        if (!ticket.getActive()) {
            throw new InvalidBookingException("Ticket is not active");
        }
        
        // Check if user can book more tickets
        if (!user.canBookTickets(quantity, configProperties.getMaxTicketsPerUser())) {
            throw new InvalidBookingException(
                String.format("User cannot book more than %d tickets", configProperties.getMaxTicketsPerUser())
            );
        }
        
        // Check ticket availability
        if (!ticketService.isTicketAvailable(ticketId, quantity)) {
            throw new InvalidBookingException("Requested tickets are not available");
        }
        
        // Calculate total amount
        BigDecimal totalAmount = ticketService.calculateTotalPrice(ticketId, quantity);
        
        // Reserve tickets
        ticketService.reserveTickets(ticketId, quantity);
        
        // Create booking
        Booking booking = Booking.builder()
                .user(user)
                .ticket(ticket)
                .quantity(quantity)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING)
                .build();
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Get booking by ID
     */
    public Booking getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
    }
    
    /**
     * Get all bookings
     */
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }
    
    /**
     * Get bookings by user ID
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId);
    }
    
    /**
     * Get bookings by status
     */
    public List<Booking> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status);
    }
    
    /**
     * Confirm booking
     */
    @Transactional
    public Booking confirmBooking(Long bookingId) {
        log.info("Confirming booking with id: {}", bookingId);
        
        Booking booking = getBookingById(bookingId);
        booking.confirm();
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Cancel booking
     */
    @Transactional
    public Booking cancelBooking(Long bookingId, String reason) {
        log.info("Cancelling booking with id: {}", bookingId);
        
        Booking booking = getBookingById(bookingId);
        
        if (!booking.isCancellable()) {
            throw new InvalidBookingException("Booking cannot be cancelled");
        }
        
        // Release tickets back to inventory
        ticketService.releaseTickets(booking.getTicket().getId(), booking.getQuantity());
        
        // Cancel booking
        booking.cancel(reason);
        
        return bookingRepository.save(booking);
    }
    
    /**
     * Calculate refund amount for booking
     */
    public BigDecimal calculateRefund(Long bookingId) {
        log.info("Calculating refund for booking: {}", bookingId);
        
        Booking booking = getBookingById(bookingId);
        BigDecimal cancellationFee = BigDecimal.valueOf(configProperties.getCancellationFee());
        
        return booking.calculateRefund(cancellationFee);
    }
    
    /**
     * Get confirmed bookings for a user
     */
    public List<Booking> getConfirmedBookingsByUser(Long userId) {
        log.info("Fetching confirmed bookings for user: {}", userId);
        return bookingRepository.findConfirmedBookingsByUser(userId);
    }
    
    /**
     * Expire pending bookings older than specified hours
     */
    @Transactional
    public void expirePendingBookings(int hours) {
        log.info("Expiring pending bookings older than {} hours", hours);
        
        LocalDateTime expiryDate = LocalDateTime.now().minusHours(hours);
        List<Booking> expiredBookings = bookingRepository.findExpiredPendingBookings(expiryDate);
        
        for (Booking booking : expiredBookings) {
            booking.setStatus(BookingStatus.EXPIRED);
            ticketService.releaseTickets(booking.getTicket().getId(), booking.getQuantity());
            bookingRepository.save(booking);
        }
        
        log.info("Expired {} pending bookings", expiredBookings.size());
    }
    
    /**
     * Get total booking count for user
     */
    public long getUserBookingCount(Long userId, BookingStatus status) {
        return bookingRepository.countByUserIdAndStatus(userId, status);
    }
}
