package com.ticketbooking.mockito;

import com.ticketbooking.exception.ResourceNotFoundException;
import com.ticketbooking.model.User;
import com.ticketbooking.repository.UserRepository;
import com.ticketbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * MOCKITO CORE CONCEPTS DEMONSTRATION
 * 
 * This test class demonstrates:
 * 1. @Mock annotation for creating mock objects
 * 2. @InjectMocks for automatic dependency injection
 * 3. @ExtendWith(MockitoExtension.class) for Mockito integration
 * 4. MockitoAnnotations.openMocks(this) alternative approach
 * 5. when().thenReturn() for stubbing method calls
 * 6. when().thenThrow() for stubbing exceptions
 * 7. verify() for verifying method calls
 * 8. Argument matchers (any(), eq(), anyString(), etc.)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Mockito Core Concepts - UserService Tests")
class UserServiceMockitoTest {
    
    /**
     * @Mock - Creates a mock object
     * Mock objects are fake implementations that we can control
     */
    @Mock
    private UserRepository userRepository;
    
    /**
     * @InjectMocks - Creates instance and injects mocks into it
     * Automatically injects @Mock objects into the class being tested
     */
    @InjectMocks
    private UserService userService;
    
    /**
     * @Captor - Captures arguments passed to mocked methods
     * Useful for verifying the exact arguments used in method calls
     */
    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    private User testUser;
    
    /**
     * Setup method - runs before each test
     * Initializes test data
     */
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phoneNumber("+1234567890")
                .registeredAt(LocalDateTime.now())
                .active(true)
                .bookings(new ArrayList<>())
                .build();
    }
    
    // ========== BASIC STUBBING WITH when().thenReturn() ==========
    
    /**
     * when().thenReturn() - Stubs method to return specific value
     * When the mocked method is called, it returns the specified value
     */
    @Test
    @DisplayName("Test getUserById with when().thenReturn() stubbing")
    void testGetUserById_WhenUserExists() {
        // ARRANGE: Set up mock behavior
        // When userRepository.findById(1L) is called, return Optional of testUser
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // ACT: Execute the method under test
        User result = userService.getUserById(1L);
        
        // ASSERT: Verify the results
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        
        // VERIFY: Check that repository method was called exactly once
        verify(userRepository, times(1)).findById(1L);
    }
    
    /**
     * Testing with when().thenReturn() for multiple values
     */
    @Test
    @DisplayName("Test getAllUsers returns list of users")
    void testGetAllUsers() {
        // Arrange: Create list of users
        List<User> users = List.of(testUser, 
            User.builder().id(2L).name("Jane Doe").email("jane@example.com")
                .phoneNumber("+9876543210").active(true).build()
        );
        
        when(userRepository.findAll()).thenReturn(users);
        
        // Act
        List<User> result = userService.getAllUsers();
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Jane Doe", result.get(1).getName());
        
        // Verify
        verify(userRepository).findAll();
    }
    
    // ========== STUBBING EXCEPTIONS with when().thenThrow() ==========
    
    /**
     * when().thenThrow() - Stubs method to throw exception
     */
    @Test
    @DisplayName("Test getUserById throws ResourceNotFoundException when user not found")
    void testGetUserById_WhenUserNotFound_ThrowsException() {
        // Arrange: Stub repository to return empty Optional
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert: Verify exception is thrown
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        
        // Verify repository was called
        verify(userRepository).findById(999L);
    }
    
    /**
     * Alternative way to stub exceptions directly
     */
    @Test
    @DisplayName("Test getUserByEmail when repository throws exception")
    void testGetUserByEmail_WhenRepositoryThrowsException() {
        // Arrange: Stub to throw exception directly
        when(userRepository.findByEmail("test@example.com"))
            .thenThrow(new RuntimeException("Database connection error"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.getUserByEmail("test@example.com");
        });
    }
    
    // ========== ARGUMENT MATCHERS ==========
    
    /**
     * any() - Matches any argument of specified type
     * anyString(), anyInt(), anyLong() - Type-specific matchers
     */
    @Test
    @DisplayName("Test createUser with argument matchers")
    void testCreateUser_WithArgumentMatchers() {
        // Arrange: Use any(User.class) matcher
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User newUser = User.builder()
                .name("New User")
                .email("new@example.com")
                .phoneNumber("+1111111111")
                .build();
        
        User result = userService.createUser(newUser);
        
        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName()); // Returns testUser
        
        // Verify with matchers
        verify(userRepository).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }
    
    /**
     * eq() - Matches exact value
     * Useful when mixing matchers with exact values
     */
    @Test
    @DisplayName("Test with eq() matcher for exact matching")
    void testGetUserByEmail_WithExactMatcher() {
        // Arrange: Use eq() for exact matching
        when(userRepository.findByEmail(eq("john@example.com")))
            .thenReturn(Optional.of(testUser));
        
        // Act
        User result = userService.getUserByEmail("john@example.com");
        
        // Assert
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        
        // Verify with exact value
        verify(userRepository).findByEmail("john@example.com");
    }
    
    // ========== VOID METHOD STUBBING ==========
    
    /**
     * doNothing() - Stubs void methods
     * By default, void methods do nothing, but doNothing() makes intent explicit
     */
    @Test
    @DisplayName("Test deleteUser with doNothing() for void method")
    void testDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));
        
        // Act
        userService.deleteUser(1L);
        
        // Verify
        verify(userRepository).findById(1L);
        verify(userRepository).delete(testUser);
    }
    
    /**
     * doThrow() - Stubs void methods to throw exception
     */
    @Test
    @DisplayName("Test with doThrow() for void method exception")
    void testDeleteUser_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doThrow(new RuntimeException("Delete failed")).when(userRepository).delete(any(User.class));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.deleteUser(1L);
        });
    }
    
    // ========== VERIFICATION METHODS ==========
    
    /**
     * verify() - Verifies method was called
     * times(n) - Exactly n times
     * never() - Never called
     * atLeastOnce() - At least once
     * atLeast(n) - At least n times
     * atMost(n) - At most n times
     */
    @Test
    @DisplayName("Test verification with times(), never(), atLeastOnce()")
    void testVerificationMethods() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        
        // Act
        userService.getUserById(1L);
        userService.getUserById(1L);
        
        // Verify with different verification modes
        verify(userRepository, times(2)).findById(1L);
        verify(userRepository, atLeastOnce()).findById(1L);
        verify(userRepository, atLeast(1)).findById(1L);
        verify(userRepository, atMost(5)).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
    }
    
    /**
     * verifyNoMoreInteractions() - Ensures no other methods were called
     */
    @Test
    @DisplayName("Test verifyNoMoreInteractions()")
    void testVerifyNoMoreInteractions() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        userService.getUserById(1L);
        
        // Verify
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }
    
    // ========== ARGUMENT CAPTORS ==========
    
    /**
     * ArgumentCaptor - Captures arguments passed to methods
     * Useful for verifying complex objects
     */
    @Test
    @DisplayName("Test with ArgumentCaptor to capture method arguments")
    void testCreateUser_WithArgumentCaptor() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        
        // Act
        User newUser = User.builder()
                .name("Captured User")
                .email("captured@example.com")
                .phoneNumber("+9999999999")
                .build();
        
        userService.createUser(newUser);
        
        // Verify and capture
        verify(userRepository).save(userCaptor.capture());
        
        // Assert on captured value
        User capturedUser = userCaptor.getValue();
        assertEquals("Captured User", capturedUser.getName());
        assertEquals("captured@example.com", capturedUser.getEmail());
    }
    
    // ========== CREATING MOCKS WITHOUT ANNOTATIONS ==========
    
    /**
     * Mockito.mock() - Creates mock without annotations
     * Alternative to @Mock annotation
     */
    @Test
    @DisplayName("Test creating mock with Mockito.mock()")
    void testWithManualMock() {
        // Create mock manually
        UserRepository manualMock = mock(UserRepository.class);
        
        // Stub behavior
        when(manualMock.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Use mock
        Optional<User> result = manualMock.findById(1L);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        
        // Verify
        verify(manualMock).findById(1L);
    }
}
