package com.ticketbooking.basic;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BASIC JUNIT CONCEPTS DEMONSTRATION
 * 
 * This test class demonstrates:
 * 1. Basic @Test annotation
 * 2. Test lifecycle annotations (@BeforeEach, @AfterEach, @BeforeAll, @AfterAll)
 * 3. @DisplayName for readable test names
 * 4. @Disabled to skip tests
 * 5. Basic assertions (assertEquals, assertTrue, assertFalse, assertNull, etc.)
 * 6. Exception testing with assertThrows
 */
@DisplayName("Basic JUnit 5 Concepts Demo")
class BasicJUnitConceptsTest {
    
    // Static variable to demonstrate @BeforeAll and @AfterAll
    private static String sharedResource;
    
    // Instance variable to demonstrate @BeforeEach and @AfterEach
    private Calculator calculator;
    
    /**
     * @BeforeAll - Runs once before all tests in the class
     * Must be static
     * Used for expensive setup operations (database connections, loading configuration, etc.)
     */
    @BeforeAll
    static void initAll() {
        System.out.println("@BeforeAll - Executed once before all tests");
        sharedResource = "Shared Resource Initialized";
    }
    
    /**
     * @BeforeEach - Runs before each test method
     * Used to set up fresh test data/objects for each test
     * Ensures test isolation
     */
    @BeforeEach
    void init() {
        System.out.println("  @BeforeEach - Executed before each test");
        calculator = new Calculator();
    }
    
    /**
     * @AfterEach - Runs after each test method
     * Used for cleanup after each test
     */
    @AfterEach
    void tearDown() {
        System.out.println("  @AfterEach - Executed after each test");
        calculator = null;
    }
    
    /**
     * @AfterAll - Runs once after all tests in the class
     * Must be static
     * Used for cleanup of expensive resources
     */
    @AfterAll
    static void tearDownAll() {
        System.out.println("@AfterAll - Executed once after all tests");
        sharedResource = null;
    }
    
    // ========== BASIC @Test ANNOTATION ==========
    
    /**
     * Simple test using @Test annotation
     */
    @Test
    void simpleTest() {
        System.out.println("    Running: simpleTest()");
        assertTrue(true, "This test always passes");
    }
    
    /**
     * @DisplayName - Provides a custom display name for the test
     * Makes test reports more readable
     */
    @Test
    @DisplayName("Addition of two positive numbers should return correct sum")
    void testAddition() {
        System.out.println("    Running: testAddition()");
        int result = calculator.add(2, 3);
        assertEquals(5, result, "2 + 3 should equal 5");
    }
    
    // ========== ASSERTION EXAMPLES ==========
    
    /**
     * assertEquals - Checks if two values are equal
     */
    @Test
    @DisplayName("Demonstrate assertEquals assertion")
    void testAssertEquals() {
        System.out.println("    Running: testAssertEquals()");
        
        // Basic equality
        assertEquals(10, calculator.add(4, 6));
        
        // With custom message
        assertEquals(15, calculator.multiply(3, 5), "3 * 5 should equal 15");
        
        // String equality
        assertEquals("Hello", "Hello");
        
        // Object equality
        String expected = "Test";
        String actual = "Test";
        assertEquals(expected, actual);
    }
    
    /**
     * assertNotEquals - Checks if two values are not equal
     */
    @Test
    @DisplayName("Demonstrate assertNotEquals assertion")
    void testAssertNotEquals() {
        System.out.println("    Running: testAssertNotEquals()");
        
        assertNotEquals(10, calculator.add(4, 5));
        assertNotEquals("Hello", "World");
    }
    
    /**
     * assertTrue and assertFalse
     */
    @Test
    @DisplayName("Demonstrate assertTrue and assertFalse")
    void testBooleanAssertions() {
        System.out.println("    Running: testBooleanAssertions()");
        
        // assertTrue
        assertTrue(calculator.isPositive(5));
        assertTrue(10 > 5, "10 should be greater than 5");
        
        // assertFalse
        assertFalse(calculator.isPositive(-5));
        assertFalse(5 > 10, "5 should not be greater than 10");
    }
    
    /**
     * assertNull and assertNotNull
     */
    @Test
    @DisplayName("Demonstrate assertNull and assertNotNull")
    void testNullAssertions() {
        System.out.println("    Running: testNullAssertions()");
        
        String nullString = null;
        String notNullString = "Hello";
        
        // assertNull
        assertNull(nullString, "String should be null");
        
        // assertNotNull
        assertNotNull(notNullString, "String should not be null");
        assertNotNull(calculator, "Calculator should be initialized");
    }
    
    /**
     * assertThrows - Tests that code throws expected exception
     */
    @Test
    @DisplayName("Demonstrate exception testing with assertThrows")
    void testExceptionThrowing() {
        System.out.println("    Running: testExceptionThrowing()");
        
        // Test that ArithmeticException is thrown
        Exception exception = assertThrows(
            ArithmeticException.class,
            () -> calculator.divide(10, 0),
            "Division by zero should throw ArithmeticException"
        );
        
        // Can also verify exception message
        assertEquals("Cannot divide by zero", exception.getMessage());
    }
    
    /**
     * assertAll - Groups multiple assertions together
     * All assertions are executed even if some fail
     */
    @Test
    @DisplayName("Demonstrate assertAll for grouped assertions")
    void testGroupedAssertions() {
        System.out.println("    Running: testGroupedAssertions()");
        
        assertAll("calculator operations",
            () -> assertEquals(5, calculator.add(2, 3)),
            () -> assertEquals(6, calculator.multiply(2, 3)),
            () -> assertEquals(2, calculator.subtract(5, 3)),
            () -> assertTrue(calculator.isPositive(5))
        );
    }
    
    // ========== @Disabled ANNOTATION ==========
    
    /**
     * @Disabled - Skips test execution
     * Useful for temporarily disabling flaky or work-in-progress tests
     */
    @Test
    @Disabled("This test is disabled - will not run")
    void testDisabled() {
        System.out.println("    This should not print!");
        fail("This test should be skipped");
    }
    
    /**
     * @Disabled with reason on class level would disable all tests
     * Here we demonstrate it on a single test
     */
    @Test
    @Disabled("Feature not yet implemented")
    @DisplayName("Future feature test - currently disabled")
    void testFutureFeature() {
        fail("Not implemented yet");
    }
    
    // ========== HELPER CLASS FOR TESTING ==========
    
    /**
     * Simple Calculator class for demonstration
     */
    static class Calculator {
        
        public int add(int a, int b) {
            return a + b;
        }
        
        public int subtract(int a, int b) {
            return a - b;
        }
        
        public int multiply(int a, int b) {
            return a * b;
        }
        
        public int divide(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("Cannot divide by zero");
            }
            return a / b;
        }
        
        public boolean isPositive(int number) {
            return number > 0;
        }
    }
}
