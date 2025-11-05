package com.ticketbooking.mockito;

import com.ticketbooking.config.BookingConfigProperties;
import com.ticketbooking.exception.InvalidBookingException;
import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.*;
import com.ticketbooking.repository.BookingRepository;
import com.ticketbooking.service.BookingService;
import com.ticketbooking.service.TicketService;
import com.ticketbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ADVANCED MOCKITO CONCEPTS - BookingService Tests
 * 
 * Demonstrates:
 * 1. Mocking multiple dependencies
 * 2. Spies vs Mocks
 * 3. Chaining method calls
 * 4. Mixing matchers correctly
 * 5. Multiple when() statements
 * 6. Answer interface for custom behavior
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Advanced Mockito Concepts - BookingService Tests")
class BookingServiceMockitoTest {
    
    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private TicketService ticketService;
    
    @Mock
    private BookingConfigProperties configProperties;
    
    @InjectMocks
    private BookingService bookingService;
    
    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;
    
    private User testUser;
    private Ticket testTicket;
    private Booking testBooking;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("+1234567890")
                .active(true)
                .bookings(new ArrayList<>())
                .build();
        
        testTicket = Ticket.builder()
                .id(1L)
                .eventName("Concert")
                .venue("Stadium")
                .eventDate(LocalDateTime.now().plusDays(10))
                .ticketType(TicketType.ECONOMY)
                .price(new BigDecimal("100.00"))
                .availableQuantity(50)
                .active(true)
                .build();
        
        testBooking = Booking.builder()
                .id(1L)
                .user(testUser)
                .ticket(testTicket)
                .quantity(2)
                .totalAmount(new BigDecimal("200.00"))
                .status(BookingStatus.PENDING)
                .bookingDate(LocalDateTime.now())
                .build();
    }
    
    // ========== MULTIPLE DEPENDENCY MOCKING ==========
    
    /**
     * Test with multiple mocked dependencies
     * Shows how InjectMocks handles multiple dependencies
     */
    @Test
    @DisplayName("Test createBooking with multiple mocked dependencies")
    void testCreateBooking_Success() {
        // Arrange: Setup all mock dependencies
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(ticketService.getTicketById(1L)).thenReturn(testTicket);
        when(ticketService.isTicketAvailable(1L, 2)).thenReturn(true);
        when(ticketService.calculateTotalPrice(1L, 2)).thenReturn(new BigDecimal("200.00"));
        when(configProperties.getMaxTicketsPerUser()).thenReturn(10);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        doNothing().when(ticketService).reserveTickets(anyLong(), anyInt());
        
        // Act
        Booking result = bookingService.createBooking(1L, 1L, 2);
        
        // Assert
        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());
        assertEquals(2, result.getQuantity());
        
        // Verify all interactions
        verify(userService).getUserById(1L);
        verify(ticketService).getTicketById(1L);
        verify(ticketService).isTicketAvailable(1L, 2);
        verify(ticketService).calculateTotalPrice(1L, 2);
        verify(ticketService).reserveTickets(1L, 2);
        verify(bookingRepository).save(any(Booking.class));
    }
    
    /**
     * Test exception scenario with multiple dependencies
     */
    @Test
    @DisplayName("Test createBooking when user is inactive")
    void testCreateBooking_WhenUserInactive_ThrowsException() {
        // Arrange
        testUser.setActive(false);
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // Act & Assert
        assertThrows(InvalidBookingException.class, () -> {
            bookingService.createBooking(1L, 1L, 2);
        });
        
        // Verify only userService was called
        verify(userService).getUserById(1L);
        verify(ticketService, never()).getTicketById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }
    
    // ========== ARGUMENT MATCHER MIXING ==========
    
    /**
     * IMPORTANT: When using matchers, ALL arguments must use matchers
     * Cannot mix exact values with matchers (will cause exception)
     */
    @Test
    @DisplayName("Test proper argument matcher usage")
    void testArgumentMatcherMixing() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(ticketService.getTicketById(1L)).thenReturn(testTicket);
        when(ticketService.isTicketAvailable(1L, 2)).thenReturn(false);
        
        // Act & Assert
        assertThrows(InvalidBookingException.class, () -> {
            bookingService.createBooking(1L, 1L, 2);
        });
        
        // CORRECT: All matchers
        verify(ticketService).isTicketAvailable(eq(1L), eq(2));
        
        // CORRECT: No matchers (exact values)
        verify(ticketService).isTicketAvailable(1L, 2);
        
        // INCORRECT (commented out): Would throw exception
        // verify(ticketService).isTicketAvailable(anyLong(), 2); // ❌ Don't do this!
    }
    
    // ========== SPY vs MOCK ==========
    
    /**
     * @Spy - Creates a spy on a real object
     * Spy calls real methods unless stubbed
     * Mock does nothing unless stubbed
     */
    @Test
    @DisplayName("Demonstrate difference between Spy and Mock")
    void testSpyVsMock() {
        // Create a spy on real BookingConfigProperties object
        BookingConfigProperties realConfig = new BookingConfigProperties();
        BookingConfigProperties spyConfig = spy(realConfig);
        
        // Spy calls real method
        int maxTickets = spyConfig.getMaxTicketsPerUser();
        assertEquals(10, maxTickets); // Real default value
        
        // Can stub specific methods on spy
        when(spyConfig.getMaxTicketsPerUser()).thenReturn(5);
        assertEquals(5, spyConfig.getMaxTicketsPerUser()); // Stubbed value
        
        // Mock returns default value (0 for primitives)
        BookingConfigProperties mockConfig = mock(BookingConfigProperties.class);
        assertEquals(0, mockConfig.getMaxTicketsPerUser()); // Mock returns 0 by default
        
        // Must stub mock to return value
        when(mockConfig.getMaxTicketsPerUser()).thenReturn(15);
        assertEquals(15, mockConfig.getMaxTicketsPerUser());
    }
    
    /**
     * Using @Spy annotation
     */
    @Test
    @DisplayName("Test with @Spy annotation for partial mocking")
    void testWithSpyAnnotation() {
        // Create a real Booking object
        Booking realBooking = Booking.builder()
                .id(1L)
                .user(testUser)
                .ticket(testTicket)
                .quantity(2)
                .totalAmount(new BigDecimal("200.00"))
                .status(BookingStatus.PENDING)
                .build();
        
        // Spy on it
        Booking spyBooking = spy(realBooking);
        
        // Real method is called
        assertTrue(spyBooking.isCancellable());
        
        // Stub specific method
        when(spyBooking.isCancellable()).thenReturn(false);
        assertFalse(spyBooking.isCancellable());
        
        // Other methods still work normally
        assertEquals(BookingStatus.PENDING, spyBooking.getStatus());
    }
    
    // ========== CHAINING AND COMPLEX STUBBING ==========
    
    /**
     * Stubbing methods that return other objects (chaining)
     */
    @Test
    @DisplayName("Test method chaining with mocks")
    void testMethodChaining() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        
        // Act
        Booking result = bookingService.getBookingById(1L);
        
        // Assert - Can call methods on returned object
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(testUser, result.getUser());
        assertEquals(testTicket, result.getTicket());
    }
    
    /**
     * Multiple return values for same method
     * thenReturn() can take multiple values
     */
    @Test
    @DisplayName("Test multiple return values")
    void testMultipleReturnValues() {
        // First call returns pending, second returns confirmed
        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(testBooking))
                .thenReturn(Optional.of(testBooking));
        
        // First call
        Booking first = bookingService.getBookingById(1L);
        assertEquals(BookingStatus.PENDING, first.getStatus());
        
        // Modify for second call
        testBooking.setStatus(BookingStatus.CONFIRMED);
        
        // Second call
        Booking second = bookingService.getBookingById(1L);
        assertEquals(BookingStatus.CONFIRMED, second.getStatus());
        
        verify(bookingRepository, times(2)).findById(1L);
    }
    
    // ========== CUSTOM ANSWERS ==========
    
    /**
     * Answer interface for custom behavior
     * Use when you need dynamic responses based on arguments
     */
    @Test
    @DisplayName("Test with Answer for dynamic behavior")
    void testWithAnswer() {
        // Arrange: Use Answer to return different values based on input
        when(ticketService.calculateTotalPrice(anyLong(), anyInt()))
                .thenAnswer(invocation -> {
                    Long ticketId = invocation.getArgument(0);
                    Integer quantity = invocation.getArgument(1);
                    
                    // Dynamic calculation based on arguments
                    BigDecimal basePrice = new BigDecimal("100.00");
                    return basePrice.multiply(BigDecimal.valueOf(quantity));
                });
        
        // Act
        BigDecimal price1 = ticketService.calculateTotalPrice(1L, 2);
        BigDecimal price2 = ticketService.calculateTotalPrice(1L, 5);
        
        // Assert
        assertEquals(new BigDecimal("200.00"), price1);
        assertEquals(new BigDecimal("500.00"), price2);
    }
    
    // ========== VERIFY WITH ARGUMENT CAPTOR ==========
    
    /**
     * Using ArgumentCaptor to verify complex objects
     */
    @Test
    @DisplayName("Test cancelBooking with ArgumentCaptor")
    void testCancelBooking_WithArgumentCaptor() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        doNothing().when(ticketService).releaseTickets(anyLong(), anyInt());
        
        // Act
        bookingService.cancelBooking(1L, "User requested cancellation");
        
        // Verify and capture
        verify(bookingRepository).save(bookingCaptor.capture());
        
        // Assert on captured booking
        Booking capturedBooking = bookingCaptor.getValue();
        assertEquals(BookingStatus.CANCELLED, capturedBooking.getStatus());
        assertEquals("User requested cancellation", capturedBooking.getCancellationReason());
        assertNotNull(capturedBooking.getCancelledAt());
    }
    
    // ========== VERIFYING ORDER OF CALLS ==========
    
    /**
     * InOrder - Verifies methods were called in specific order
     */
    @Test
    @DisplayName("Test method call order with InOrder")
    void testMethodCallOrder() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(testUser);
        when(ticketService.getTicketById(1L)).thenReturn(testTicket);
        when(ticketService.isTicketAvailable(1L, 2)).thenReturn(true);
        when(ticketService.calculateTotalPrice(1L, 2)).thenReturn(new BigDecimal("200.00"));
        when(configProperties.getMaxTicketsPerUser()).thenReturn(10);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        doNothing().when(ticketService).reserveTickets(anyLong(), anyInt());
        
        // Act
        bookingService.createBooking(1L, 1L, 2);
        
        // Verify order
        InOrder inOrder = inOrder(userService, ticketService, bookingRepository);
        inOrder.verify(userService).getUserById(1L);
        inOrder.verify(ticketService).getTicketById(1L);
        inOrder.verify(ticketService).isTicketAvailable(1L, 2);
        inOrder.verify(ticketService).reserveTickets(1L, 2);
        inOrder.verify(bookingRepository).save(any(Booking.class));
    }
    
    // ========== TIMEOUT VERIFICATION ==========
    
    /**
     * Verify with timeout - useful for async operations
     */
    @Test
    @DisplayName("Test with timeout verification")
    void testWithTimeout() {
        // Arrange
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking));
        
        // Act
        bookingService.getBookingById(1L);
        
        // Verify with timeout (method should be called within 100ms)
        verify(bookingRepository, timeout(100)).findById(1L);
    }
}
