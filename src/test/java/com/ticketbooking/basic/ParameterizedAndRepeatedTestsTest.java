package com.ticketbooking.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PARAMETERIZED AND REPEATED TESTS DEMONSTRATION
 * 
 * This class demonstrates:
 * 1. @ParameterizedTest with different sources
 * 2. @ValueSource for simple values
 * 3. @CsvSource for multiple parameters
 * 4. @MethodSource for complex objects
 * 5. @EnumSource for enum values
 * 6. @RepeatedTest for running tests multiple times
 */
@DisplayName("Parameterized and Repeated Tests Demo")
class ParameterizedAndRepeatedTestsTest {
    
    // ========== PARAMETERIZED TESTS ==========
    
    /**
     * @ValueSource - Provides array of values for single parameter
     * Useful for testing multiple inputs with same logic
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 8, 13, 21})
    @DisplayName("Test multiple positive numbers using @ValueSource")
    void testPositiveNumbers(int number) {
        assertTrue(number > 0, () -> number + " should be positive");
    }
    
    /**
     * @ValueSource with strings
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "\t", "\n"})
    @DisplayName("Test blank strings using @ValueSource")
    void testBlankStrings(String input) {
        assertTrue(input == null || input.trim().isEmpty(),
            () -> "'" + input + "' should be blank");
    }
    
    /**
     * @CsvSource - Provides CSV format data for multiple parameters
     * Format: "param1, param2, expectedResult"
     */
    @ParameterizedTest
    @CsvSource({
        "2, 3, 5",
        "10, 20, 30",
        "-5, 5, 0",
        "100, 200, 300"
    })
    @DisplayName("Test addition with multiple inputs using @CsvSource")
    void testAdditionWithCsvSource(int a, int b, int expectedSum) {
        int result = a + b;
        assertEquals(expectedSum, result,
            () -> String.format("%d + %d should equal %d", a, b, expectedSum));
    }
    
    /**
     * @CsvSource with strings and different types
     */
    @ParameterizedTest
    @CsvSource({
        "apple, 5, true",
        "banana, 6, true",
        "cat, 3, true",
        "'', 0, false"
    })
    @DisplayName("Test string length validation using @CsvSource")
    void testStringLength(String input, int expectedLength, boolean isValid) {
        if (isValid) {
            assertEquals(expectedLength, input.length());
        } else {
            assertEquals(expectedLength, input.length());
        }
    }
    
    /**
     * @MethodSource - Provides complex objects from a method
     * Method must be static and return Stream, Iterable, or array
     */
    @ParameterizedTest
    @MethodSource("ticketPriceProvider")
    @DisplayName("Test ticket price calculation using @MethodSource")
    void testTicketPriceCalculation(String ticketType, int quantity, double expectedPrice) {
        double price = calculateTicketPrice(ticketType, quantity);
        assertEquals(expectedPrice, price, 0.01,
            () -> String.format("%d %s tickets should cost $%.2f", quantity, ticketType, expectedPrice));
    }
    
    /**
     * Data provider method for @MethodSource
     * Returns Stream of arguments
     */
    static Stream<Arguments> ticketPriceProvider() {
        return Stream.of(
            Arguments.of("ECONOMY", 1, 50.0),
            Arguments.of("ECONOMY", 2, 100.0),
            Arguments.of("BUSINESS", 1, 150.0),
            Arguments.of("VIP", 1, 300.0),
            Arguments.of("VIP", 3, 900.0)
        );
    }
    
    /**
     * @EnumSource - Provides enum values as test parameters
     */
    @ParameterizedTest
    @EnumSource(TicketType.class)
    @DisplayName("Test all ticket types using @EnumSource")
    void testAllTicketTypes(TicketType ticketType) {
        assertNotNull(ticketType);
        assertTrue(ticketType.getBasePrice() > 0,
            () -> ticketType + " should have positive base price");
    }
    
    /**
     * @EnumSource with mode INCLUDE - only specific enum values
     */
    @ParameterizedTest
    @EnumSource(value = TicketType.class, names = {"BUSINESS", "VIP"})
    @DisplayName("Test premium ticket types only")
    void testPremiumTicketTypes(TicketType ticketType) {
        assertTrue(ticketType.getBasePrice() >= 150.0,
            () -> ticketType + " should be premium priced");
    }
    
    /**
     * @EnumSource with mode EXCLUDE
     */
    @ParameterizedTest
    @EnumSource(value = TicketType.class, names = {"ECONOMY"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Test all non-economy ticket types")
    void testNonEconomyTicketTypes(TicketType ticketType) {
        assertTrue(ticketType.getBasePrice() > 50.0,
            () -> ticketType + " should cost more than economy");
    }
    
    /**
     * @NullSource and @EmptySource - Special sources for null and empty values
     */
    @ParameterizedTest
    @NullSource
    @DisplayName("Test null input handling")
    void testNullInput(String input) {
        assertNull(input);
    }
    
    @ParameterizedTest
    @EmptySource
    @DisplayName("Test empty string handling")
    void testEmptyString(String input) {
        assertTrue(input.isEmpty());
    }
    
    /**
     * @NullAndEmptySource - Combines @NullSource and @EmptySource
     */
    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Test null and empty inputs")
    void testNullAndEmptyInputs(String input) {
        assertTrue(input == null || input.isEmpty(),
            "Input should be null or empty");
    }
    
    // ========== REPEATED TESTS ==========
    
    /**
     * @RepeatedTest - Runs test multiple times
     * Useful for testing random operations or checking for flakiness
     */
    @RepeatedTest(5)
    @DisplayName("Repeated test - runs 5 times")
    void testRepeated() {
        int randomNumber = (int) (Math.random() * 100);
        assertTrue(randomNumber >= 0 && randomNumber < 100,
            "Random number should be in valid range");
    }
    
    /**
     * @RepeatedTest with custom display name
     * {currentRepetition} and {totalRepetitions} are placeholders
     */
    @RepeatedTest(value = 3, name = "Repetition {currentRepetition} of {totalRepetitions}")
    @DisplayName("Repeated test with custom name")
    void testRepeatedWithCustomName() {
        assertTrue(true);
    }
    
    /**
     * @RepeatedTest with RepetitionInfo parameter
     * Provides information about current repetition
     */
    @RepeatedTest(5)
    @DisplayName("Repeated test with repetition info")
    void testWithRepetitionInfo(RepetitionInfo repetitionInfo) {
        int current = repetitionInfo.getCurrentRepetition();
        int total = repetitionInfo.getTotalRepetitions();
        
        System.out.println("Repetition " + current + " of " + total);
        
        assertTrue(current >= 1 && current <= total);
    }
    
    // ========== HELPER METHODS AND CLASSES ==========
    
    /**
     * Helper method to calculate ticket price
     */
    private double calculateTicketPrice(String ticketType, int quantity) {
        double basePrice = switch (ticketType) {
            case "ECONOMY" -> 50.0;
            case "BUSINESS" -> 150.0;
            case "VIP" -> 300.0;
            default -> 0.0;
        };
        return basePrice * quantity;
    }
    
    /**
     * Enum for ticket types
     */
    enum TicketType {
        ECONOMY(50.0),
        BUSINESS(150.0),
        FIRST_CLASS(200.0),
        VIP(300.0);
        
        private final double basePrice;
        
        TicketType(double basePrice) {
            this.basePrice = basePrice;
        }
        
        public double getBasePrice() {
            return basePrice;
        }
    }
}
