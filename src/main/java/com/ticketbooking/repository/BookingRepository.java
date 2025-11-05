package com.ticketbooking.repository;

import com.ticketbooking.model.Booking;
import com.ticketbooking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Booking entity
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find bookings by user ID
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Find bookings by ticket ID
     */
    List<Booking> findByTicketId(Long ticketId);
    
    /**
     * Find bookings by status
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Find bookings by user ID and status
     */
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Find bookings created between two dates
     */
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count bookings by user ID and status
     */
    long countByUserIdAndStatus(Long userId, BookingStatus status);
    
    /**
     * Find all confirmed bookings for a user
     */
    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.status = 'CONFIRMED'")
    List<Booking> findConfirmedBookingsByUser(@Param("userId") Long userId);
    
    /**
     * Find bookings that need to expire (pending for more than specified hours)
     */
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.bookingDate < :expiryDate")
    List<Booking> findExpiredPendingBookings(@Param("expiryDate") LocalDateTime expiryDate);
}
