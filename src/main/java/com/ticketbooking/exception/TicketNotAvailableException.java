package com.ticketbooking.exception;

/**
 * Exception thrown when tickets are not available
 */
public class TicketNotAvailableException extends RuntimeException {
    
    public TicketNotAvailableException(String message) {
        super(message);
    }
}
