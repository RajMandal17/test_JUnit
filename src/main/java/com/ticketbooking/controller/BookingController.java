package com.ticketbooking.controller;

import com.ticketbooking.model.Booking;
import com.ticketbooking.model.BookingStatus;
import com.ticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Booking operations
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    
    /**
     * Create a new booking
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long userId,
            @RequestParam Long ticketId,
            @RequestParam int quantity) {
        Booking booking = bookingService.createBooking(userId, ticketId, quantity);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }
    
    /**
     * Get booking by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Booking booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
    
    /**
     * Get all bookings
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Confirm booking
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id) {
        Booking booking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(booking);
    }
    
    /**
     * Cancel booking
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        Booking booking = bookingService.cancelBooking(id, reason);
        return ResponseEntity.ok(booking);
    }
    
    /**
     * Calculate refund for booking
     */
    @GetMapping("/{id}/refund")
    public ResponseEntity<Map<String, BigDecimal>> calculateRefund(@PathVariable Long id) {
        BigDecimal refund = bookingService.calculateRefund(id);
        return ResponseEntity.ok(Map.of("refundAmount", refund));
    }
    
    /**
     * Get confirmed bookings for a user
     */
    @GetMapping("/user/{userId}/confirmed")
    public ResponseEntity<List<Booking>> getConfirmedBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getConfirmedBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }
}
