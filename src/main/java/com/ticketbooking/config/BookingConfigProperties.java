package com.ticketbooking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for booking settings
 * Demonstrates how to test @ConfigurationProperties
 */
@Configuration
@ConfigurationProperties(prefix = "booking")
@Data
public class BookingConfigProperties {
    
    /**
     * Maximum number of tickets a user can book
     */
    private int maxTicketsPerUser = 10;
    
    /**
     * Cancellation fee amount
     */
    private double cancellationFee = 50.0;
    
    /**
     * Number of days in advance tickets can be booked
     */
    private int advanceBookingDays = 30;
}
