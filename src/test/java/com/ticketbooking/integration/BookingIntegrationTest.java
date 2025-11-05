package com.ticketbooking.integration;

import com.ticketbooking.model.*;
import com.ticketbooking.repository.BookingRepository;
import com.ticketbooking.repository.TicketRepository;
import com.ticketbooking.repository.UserRepository;
import com.ticketbooking.service.BookingService;
import com.ticketbooking.service.TicketService;
import com.ticketbooking.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION TESTS
 * 
 * Demonstrates:
 * 1. Full Spring context integration tests
 * 2. Testing with real database (H2 in-memory)
 * 3. Testing service layer with repository layer
 * 4. @Transactional for test data isolation
 * 5. @SpringBootTest for complete application context
 * 6. End-to-end workflow testing
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - Complete Booking Flow")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    private User savedUser;
    private Ticket savedTicket;
    
    /**
     * Setup test data before each test
     */
    @BeforeEach
    void setUp() {
        // Create and save user
        User user = User.builder()
                .name("Integration Test User")
                .email("integration@test.com")
                .phoneNumber("+1234567890")
                .active(true)
                .build();
        savedUser = userService.createUser(user);
        
        // Create and save ticket
        Ticket ticket = Ticket.builder()
                .eventName("Integration Test Concert")
                .venue("Test Stadium")
                .eventDate(LocalDateTime.now().plusDays(30))
                .ticketType(TicketType.ECONOMY)
                .price(new BigDecimal("100.00"))
                .availableQuantity(100)
                .active(true)
                .build();
        savedTicket = ticketService.createTicket(ticket);
    }
    
    // ========== END-TO-END WORKFLOW TESTS ==========
    
    /**
     * Test complete booking workflow
     */
    @Test
    @Order(1)
    @DisplayName("Integration: Complete booking creation workflow")
    void testCompleteBookingWorkflow() {
        // 1. Verify user exists
        User user = userService.getUserById(savedUser.getId());
        assertNotNull(user);
        assertTrue(user.getActive());
        
        // 2. Verify ticket exists and is available
        Ticket ticket = ticketService.getTicketById(savedTicket.getId());
        assertNotNull(ticket);
        assertEquals(100, ticket.getAvailableQuantity());
        
        // 3. Create booking
        int requestedQuantity = 5;
        Booking booking = bookingService.createBooking(
            savedUser.getId(),
            savedTicket.getId(),
            requestedQuantity
        );
        
        // 4. Verify booking created
        assertNotNull(booking);
        assertNotNull(booking.getId());
        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertEquals(requestedQuantity, booking.getQuantity());
        assertEquals(new BigDecimal("500.00"), booking.getTotalAmount());
        
        // 5. Verify ticket quantity reduced
        Ticket updatedTicket = ticketService.getTicketById(savedTicket.getId());
        assertEquals(95, updatedTicket.getAvailableQuantity());
        
        // 6. Confirm booking
        Booking confirmedBooking = bookingService.confirmBooking(booking.getId());
        assertEquals(BookingStatus.CONFIRMED, confirmedBooking.getStatus());
        assertNotNull(confirmedBooking.getConfirmedAt());
        
        // 7. Verify booking persisted
        Booking fetchedBooking = bookingService.getBookingById(booking.getId());
        assertEquals(BookingStatus.CONFIRMED, fetchedBooking.getStatus());
    }
    
    /**
     * Test booking cancellation workflow
     */
    @Test
    @Order(2)
    @DisplayName("Integration: Booking cancellation workflow")
    void testBookingCancellationWorkflow() {
        // Create and confirm booking
        Booking booking = bookingService.createBooking(savedUser.getId(), savedTicket.getId(), 3);
        bookingService.confirmBooking(booking.getId());
        
        // Get current ticket quantity
        Ticket beforeCancel = ticketService.getTicketById(savedTicket.getId());
        int quantityBeforeCancel = beforeCancel.getAvailableQuantity();
        
        // Cancel booking
        Booking cancelledBooking = bookingService.cancelBooking(
            booking.getId(),
            "Changed plans"
        );
        
        // Verify cancellation
        assertEquals(BookingStatus.CANCELLED, cancelledBooking.getStatus());
        assertEquals("Changed plans", cancelledBooking.getCancellationReason());
        assertNotNull(cancelledBooking.getCancelledAt());
        
        // Verify tickets released back
        Ticket afterCancel = ticketService.getTicketById(savedTicket.getId());
        assertEquals(quantityBeforeCancel + 3, afterCancel.getAvailableQuantity());
    }
    
    /**
     * Test multiple bookings by same user
     */
    @Test
    @Order(3)
    @DisplayName("Integration: Multiple bookings by same user")
    void testMultipleBookingsByUser() {
        // Create first booking
        Booking booking1 = bookingService.createBooking(savedUser.getId(), savedTicket.getId(), 2);
        bookingService.confirmBooking(booking1.getId());
        
        // Create second booking
        Booking booking2 = bookingService.createBooking(savedUser.getId(), savedTicket.getId(), 3);
        
        // Get all bookings for user
        List<Booking> userBookings = bookingService.getBookingsByUserId(savedUser.getId());
        
        // Verify
        assertEquals(2, userBookings.size());
        
        // Verify confirmed bookings
        List<Booking> confirmedBookings = bookingService.getConfirmedBookingsByUser(savedUser.getId());
        assertEquals(1, confirmedBookings.size());
        assertEquals(booking1.getId(), confirmedBookings.get(0).getId());
    }
    
    /**
     * Test repository queries
     */
    @Test
    @Order(4)
    @DisplayName("Integration: Repository query methods")
    void testRepositoryQueries() {
        // Create multiple bookings with different statuses
        Booking booking1 = bookingService.createBooking(savedUser.getId(), savedTicket.getId(), 2);
        Booking booking2 = bookingService.createBooking(savedUser.getId(), savedTicket.getId(), 1);
        bookingService.confirmBooking(booking2.getId());
        
        // Test findByStatus
        List<Booking> pendingBookings = bookingRepository.findByStatus(BookingStatus.PENDING);
        assertTrue(pendingBookings.size() >= 1);
        
        List<Booking> confirmedBookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        assertTrue(confirmedBookings.size() >= 1);
        
        // Test countByUserIdAndStatus
        long pendingCount = bookingRepository.countByUserIdAndStatus(
            savedUser.getId(),
            BookingStatus.PENDING
        );
        assertTrue(pendingCount >= 1);
    }
    
    /**
     * Test data persistence across service layers
     */
    @Test
    @Order(5)
    @DisplayName("Integration: Data persistence across layers")
    void testDataPersistenceAcrossLayers() {
        // Create booking through service
        Booking serviceBooking = bookingService.createBooking(
            savedUser.getId(),
            savedTicket.getId(),
            4
        );
        
        // Fetch directly from repository
        Booking repoBooking = bookingRepository.findById(serviceBooking.getId())
                .orElseThrow();
        
        // Verify same data
        assertEquals(serviceBooking.getId(), repoBooking.getId());
        assertEquals(serviceBooking.getQuantity(), repoBooking.getQuantity());
        assertEquals(serviceBooking.getTotalAmount(), repoBooking.getTotalAmount());
        assertEquals(serviceBooking.getStatus(), repoBooking.getStatus());
    }
    
    /**
     * Test searching tickets by criteria
     */
    @Test
    @Order(6)
    @DisplayName("Integration: Search tickets by various criteria")
    void testTicketSearch() {
        // Create additional tickets
        Ticket ticket2 = Ticket.builder()
                .eventName("Rock Concert")
                .venue("Test Stadium")
                .eventDate(LocalDateTime.now().plusDays(15))
                .ticketType(TicketType.VIP)
                .price(new BigDecimal("300.00"))
                .availableQuantity(50)
                .active(true)
                .build();
        ticketService.createTicket(ticket2);
        
        // Search by venue
        List<Ticket> stadiumTickets = ticketService.getTicketsByVenue("Test Stadium");
        assertTrue(stadiumTickets.size() >= 2);
        
        // Search by ticket type
        List<Ticket> economyTickets = ticketService.getTicketsByType(TicketType.ECONOMY);
        assertTrue(economyTickets.size() >= 1);
        
        // Search by price range
        List<Ticket> affordableTickets = ticketService.getTicketsByPriceRange(
            new BigDecimal("50.00"),
            new BigDecimal("150.00")
        );
        assertTrue(affordableTickets.size() >= 1);
        
        // Search upcoming events
        List<Ticket> upcomingTickets = ticketService.getUpcomingEvents();
        assertTrue(upcomingTickets.size() >= 2);
    }
    
    /**
     * Test user operations
     */
    @Test
    @Order(7)
    @DisplayName("Integration: User CRUD operations")
    void testUserOperations() {
        // Create user
        User newUser = User.builder()
                .name("Another User")
                .email("another@test.com")
                .phoneNumber("+9876543210")
                .active(true)
                .build();
        User created = userService.createUser(newUser);
        assertNotNull(created.getId());
        
        // Update user
        created.setName("Updated Name");
        User updated = userService.updateUser(created.getId(), created);
        assertEquals("Updated Name", updated.getName());
        
        // Get user by email
        User foundByEmail = userService.getUserByEmail("another@test.com");
        assertEquals(created.getId(), foundByEmail.getId());
        
        // Deactivate user
        userService.deactivateUser(created.getId());
        User deactivated = userService.getUserById(created.getId());
        assertFalse(deactivated.getActive());
        
        // Activate user
        userService.activateUser(created.getId());
        User activated = userService.getUserById(created.getId());
        assertTrue(activated.getActive());
    }
}
