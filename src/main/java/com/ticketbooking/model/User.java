package com.ticketbooking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing a customer in the ticket booking system
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name cannot be blank")
    @Column(nullable = false)
    private String name;
    
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    @Column(unique = true, nullable = false)
    private String email;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Phone number should be valid")
    @Column(nullable = false)
    private String phoneNumber;
    
    @Column(nullable = false)
    private LocalDateTime registeredAt;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        registeredAt = LocalDateTime.now();
    }
    
    // Business method to check if user can book more tickets
    public boolean canBookTickets(int requestedTickets, int maxAllowed) {
        if (!active) {
            return false;
        }
        long activeBookings = bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .count();
        return (activeBookings + requestedTickets) <= maxAllowed;
    }
}
