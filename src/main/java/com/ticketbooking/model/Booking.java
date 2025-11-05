package com.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking entity representing a ticket booking by a user
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @NotNull(message = "Ticket cannot be null")
    private Ticket ticket;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;
    
    @NotNull(message = "Total amount cannot be null")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(nullable = false)
    private LocalDateTime bookingDate;
    
    private LocalDateTime confirmedAt;
    
    private LocalDateTime cancelledAt;
    
    @Column(length = 500)
    private String cancellationReason;
    
    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
    }
    
    // Business method to confirm booking
    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }
    
    // Business method to cancel booking
    public void cancel(String reason) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }
    
    // Business method to check if booking can be cancelled
    public boolean isCancellable() {
        return this.status == BookingStatus.CONFIRMED || this.status == BookingStatus.PENDING;
    }
    
    // Business method to calculate refund amount
    public BigDecimal calculateRefund(BigDecimal cancellationFee) {
        if (!isCancellable()) {
            return BigDecimal.ZERO;
        }
        BigDecimal refund = totalAmount.subtract(cancellationFee);
        return refund.max(BigDecimal.ZERO);
    }
}
