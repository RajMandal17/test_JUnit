package com.ticketbooking.service;

import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.User;
import com.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for User operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    /**
     * Create a new user
     */
    @Transactional
    public User createUser(User user) {
        log.info("Creating user with email: {}", user.getEmail());
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + user.getEmail());
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }
    
    /**
     * Get all active users
     */
    public List<User> getActiveUsers() {
        log.info("Fetching all active users");
        return userRepository.findByActiveTrue();
    }
    
    /**
     * Update user
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        log.info("Updating user with id: {}", id);
        
        User user = getUserById(id);
        
        user.setName(userDetails.getName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        
        return userRepository.save(user);
    }
    
    /**
     * Deactivate user
     */
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deactivating user with id: {}", id);
        
        User user = getUserById(id);
        user.setActive(false);
        userRepository.save(user);
    }
    
    /**
     * Activate user
     */
    @Transactional
    public void activateUser(Long id) {
        log.info("Activating user with id: {}", id);
        
        User user = getUserById(id);
        user.setActive(true);
        userRepository.save(user);
    }
    
    /**
     * Delete user
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
