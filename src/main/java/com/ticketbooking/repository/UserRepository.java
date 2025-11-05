package com.ticketbooking.repository;

import com.ticketbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Find all active users
     */
    List<User> findByActiveTrue();
    
    /**
     * Find all inactive users
     */
    List<User> findByActiveFalse();
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users with bookings count greater than specified value
     */
    @Query("SELECT u FROM User u WHERE SIZE(u.bookings) > :count")
    List<User> findUsersWithBookingsGreaterThan(@Param("count") int count);
}
