package com.ticketbooking.exception;

/**
 * Exception thrown when a booking operation is invalid
 */
public class InvalidBookingException extends RuntimeException {
    
    public InvalidBookingException(String message) {
        super(message);
    }
    
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
