package com.ticketbooking.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SPRING CONFIGURATION AND PROPERTIES TESTING
 * 
 * Demonstrates:
 * 1. Testing @ConfigurationProperties
 * 2. Testing @Value injections
 * 3. Using different Spring profiles in tests
 * 4. @TestPropertySource for overriding properties
 * 5. Testing Environment bean
 * 6. Property validation
 */
@SpringBootTest
@DisplayName("Configuration and Properties Tests")
class ConfigurationPropertiesTest {
    
    @Autowired
    private BookingConfigProperties configProperties;
    
    @Autowired
    private Environment environment;
    
    @Value("${booking.max-tickets-per-user}")
    private int maxTicketsFromValue;
    
    @Value("${booking.cancellation-fee}")
    private double cancellationFeeFromValue;
    
    // ========== TESTING @ConfigurationProperties ==========
    
    /**
     * Test default properties from application.properties
     */
    @Test
    @DisplayName("Test ConfigurationProperties loads default values")
    void testDefaultConfigurationProperties() {
        // Assert default values from application.properties
        assertNotNull(configProperties);
        assertEquals(10, configProperties.getMaxTicketsPerUser());
        assertEquals(50.0, configProperties.getCancellationFee());
        assertEquals(30, configProperties.getAdvanceBookingDays());
    }
    
    // ========== TESTING @Value INJECTION ==========
    
    /**
     * Test @Value annotation injects properties correctly
     */
    @Test
    @DisplayName("Test @Value annotation injects properties")
    void testValueAnnotation() {
        assertEquals(10, maxTicketsFromValue);
        assertEquals(50.0, cancellationFeeFromValue);
    }
    
    // ========== TESTING ENVIRONMENT BEAN ==========
    
    /**
     * Test Environment bean for accessing properties
     */
    @Test
    @DisplayName("Test Environment bean access to properties")
    void testEnvironmentProperties() {
        String maxTickets = environment.getProperty("booking.max-tickets-per-user");
        String cancellationFee = environment.getProperty("booking.cancellation-fee");
        String advanceBookingDays = environment.getProperty("booking.advance-booking-days");
        
        assertNotNull(maxTickets);
        assertEquals("10", maxTickets);
        assertEquals("50.0", cancellationFee);
        assertEquals("30", advanceBookingDays);
    }
    
    /**
     * Test Environment with default values
     */
    @Test
    @DisplayName("Test Environment.getProperty() with default value")
    void testEnvironmentWithDefault() {
        String nonExistent = environment.getProperty("non.existent.property", "default-value");
        assertEquals("default-value", nonExistent);
        
        String existing = environment.getProperty("booking.max-tickets-per-user", "5");
        assertEquals("10", existing); // Returns actual value, not default
    }
}

/**
 * TEST WITH ACTIVE PROFILE
 * Demonstrates using @ActiveProfiles to load test-specific configuration
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Test Profile Configuration Tests")
class TestProfileConfigurationTest {
    
    @Autowired
    private BookingConfigProperties configProperties;
    
    @Autowired
    private Environment environment;
    
    /**
     * Test that test profile properties override default properties
     */
    @Test
    @DisplayName("Test profile properties override default values")
    void testProfileProperties() {
        // Test profile has different values in application-test.properties
        assertEquals(5, configProperties.getMaxTicketsPerUser()); // Override: 5 instead of 10
        assertEquals(25.0, configProperties.getCancellationFee()); // Override: 25 instead of 50
        assertEquals(15, configProperties.getAdvanceBookingDays()); // Override: 15 instead of 30
    }
    
    /**
     * Verify active profiles
     */
    @Test
    @DisplayName("Verify active profile is 'test'")
    void testActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        assertEquals(1, activeProfiles.length);
        assertEquals("test", activeProfiles[0]);
    }
}

/**
 * TEST WITH @TestPropertySource
 * Demonstrates overriding properties specifically for one test class
 */
@SpringBootTest
@TestPropertySource(properties = {
    "booking.max-tickets-per-user=3",
    "booking.cancellation-fee=10.0",
    "booking.advance-booking-days=7"
})
@DisplayName("Test Property Source Override Tests")
class TestPropertySourceTest {
    
    @Autowired
    private BookingConfigProperties configProperties;
    
    @Value("${booking.max-tickets-per-user}")
    private int maxTickets;
    
    /**
     * Test @TestPropertySource overrides properties
     */
    @Test
    @DisplayName("Test @TestPropertySource overrides configuration properties")
    void testPropertySourceOverride() {
        // Values from @TestPropertySource annotation
        assertEquals(3, configProperties.getMaxTicketsPerUser());
        assertEquals(10.0, configProperties.getCancellationFee());
        assertEquals(7, configProperties.getAdvanceBookingDays());
        
        assertEquals(3, maxTickets);
    }
}

/**
 * TEST WITH EXTERNAL PROPERTIES FILE
 * Demonstrates loading properties from custom file
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test-custom.properties")
@DisplayName("Custom Properties File Tests")
class CustomPropertiesFileTest {
    
    @Value("${custom.property:default}")
    private String customProperty;
    
    @Autowired
    private Environment environment;
    
    /**
     * Test loading from custom properties file
     * Note: You would need to create test-custom.properties for this to work
     */
    @Test
    @DisplayName("Test custom properties file loading")
    void testCustomPropertiesFile() {
        // This will use default value if file doesn't exist
        assertNotNull(customProperty);
    }
}

/**
 * TEST MISSING PROPERTIES HANDLING
 */
@SpringBootTest
@DisplayName("Missing Properties Handling Tests")
class MissingPropertiesTest {
    
    @Value("${non.existent.property:fallback-value}")
    private String propertyWithDefault;
    
    @Autowired
    private Environment environment;
    
    /**
     * Test @Value with default value for missing property
     */
    @Test
    @DisplayName("Test @Value with default for missing property")
    void testMissingPropertyWithDefault() {
        assertEquals("fallback-value", propertyWithDefault);
    }
    
    /**
     * Test Environment.getProperty() for missing property
     */
    @Test
    @DisplayName("Test Environment returns null for missing property")
    void testMissingPropertyInEnvironment() {
        String missing = environment.getProperty("completely.missing.property");
        assertNull(missing);
    }
}
