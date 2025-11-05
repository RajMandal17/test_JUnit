package com.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ticket entity representing an available ticket for an event
 */
@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event name cannot be blank")
    @Column(nullable = false)
    private String eventName;
    
    @NotBlank(message = "Venue cannot be blank")
    @Column(nullable = false)
    private String venue;
    
    @NotNull(message = "Event date cannot be null")
    @Column(nullable = false)
    private LocalDateTime eventDate;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Ticket type cannot be null")
    @Column(nullable = false)
    private TicketType ticketType;
    
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @NotNull(message = "Available quantity cannot be null")
    @Min(value = 0, message = "Available quantity must be non-negative")
    @Column(nullable = false)
    private Integer availableQuantity;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Business method to check if tickets are available
    public boolean hasAvailableTickets(int requestedQuantity) {
        return active && availableQuantity >= requestedQuantity;
    }
    
    // Business method to calculate total price
    public BigDecimal calculateTotalPrice(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    // Business method to reserve tickets
    public void reserveTickets(int quantity) {
        if (!hasAvailableTickets(quantity)) {
            throw new IllegalStateException("Not enough tickets available");
        }
        this.availableQuantity -= quantity;
    }
    
    // Business method to release tickets (when booking is cancelled)
    public void releaseTickets(int quantity) {
        this.availableQuantity += quantity;
    }
}
