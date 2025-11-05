# Complete JUnit and Mockito Testing Guide
## Ticket Booking System - From Beginner to Advanced

---

## Table of Contents
1. [Introduction](#introduction)
2. [Project Overview](#project-overview)
3. [Unit Testing Fundamentals](#unit-testing-fundamentals)
4. [JUnit 5 Basics](#junit-5-basics)
5. [Advanced JUnit Features](#advanced-junit-features)
6. [Mockito Fundamentals](#mockito-fundamentals)
7. [Advanced Mockito Techniques](#advanced-mockito-techniques)
8. [Spring Boot Testing](#spring-boot-testing)
9. [Best Practices](#best-practices)
10. [Running Tests](#running-tests)

---

## Introduction

This comprehensive guide covers **Unit Testing** with **JUnit 5** and **Mockito** from beginner to advanced level using a real-world **Ticket Booking System** backend application.

### What You'll Learn
- Complete understanding of unit testing concepts
- JUnit 5 framework from basics to advanced
- Mockito for mocking dependencies
- Spring Boot testing strategies
- Integration testing
- Best practices and real-world patterns

---

## Project Overview

### Application Architecture

```
Ticket Booking System
├── Domain Models (Entities)
│   ├── User
│   ├── Ticket
│   └── Booking
├── Repository Layer (Data Access)
│   ├── UserRepository
│   ├── TicketRepository
│   └── BookingRepository
├── Service Layer (Business Logic)
│   ├── UserService
│   ├── TicketService
│   └── BookingService
└── Controller Layer (REST APIs)
    ├── UserController
    └── BookingController
```

### Key Features
- **User Management**: Create, update, activate/deactivate users
- **Ticket Management**: CRUD operations for event tickets
- **Booking System**: Book tickets, confirm, cancel bookings
- **Configuration Management**: Property-based configuration

---

## Unit Testing Fundamentals

### What is Unit Testing?

**Unit Testing** is testing the smallest piece of code (a "unit") in isolation.

#### Characteristics:
- ✅ Tests **one function/method** at a time
- ✅ **Fast** execution (milliseconds)
- ✅ **Isolated** from external dependencies
- ✅ **Repeatable** and deterministic
- ✅ **Independent** of other tests

### Types of Tests

#### 1️⃣ Unit Tests
- Test individual methods/functions
- Mock all dependencies
- **Example**: Testing `calculateTotalPrice()` method

#### 2️⃣ Integration Tests
- Test multiple components together
- Use real dependencies (database, services)
- **Example**: Testing UserService + UserRepository with real database

#### 3️⃣ Functional/E2E Tests
- Test entire application flow
- Test from user perspective
- **Example**: Testing complete booking workflow via REST API

### Comparison Table

| Aspect | Unit Test | Integration Test | Functional Test |
|--------|-----------|------------------|-----------------|
| **Scope** | Single method/class | Multiple components | Entire system |
| **Speed** | Very Fast (ms) | Medium (seconds) | Slow (seconds/minutes) |
| **Dependencies** | Mocked | Real | Real |
| **Isolation** | High | Medium | Low |
| **Maintenance** | Easy | Medium | Complex |
| **When to Run** | Every commit | Before merge | Before release |

---

## JUnit 5 Basics

### Framework Overview

**JUnit 5** = JUnit Platform + JUnit Jupiter + JUnit Vintage

- **JUnit Platform**: Foundation for running tests
- **JUnit Jupiter**: New API for writing tests
- **JUnit Vintage**: Backward compatibility with JUnit 3/4

### Maven Dependencies

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### AAA Pattern (Arrange-Act-Assert)

Every test should follow this structure:

```java
@Test
void testExample() {
    // ARRANGE: Set up test data and preconditions
    Calculator calculator = new Calculator();
    int a = 5, b = 3;
    
    // ACT: Execute the method under test
    int result = calculator.add(a, b);
    
    // ASSERT: Verify the results
    assertEquals(8, result);
}
```

### Basic Annotations

#### @Test
Marks a method as a test method.

```java
@Test
void myFirstTest() {
    assertTrue(true);
}
```

**📍 Location**: `BasicJUnitConceptsTest.java`

#### @DisplayName
Provides custom, readable test name for reports.

```java
@Test
@DisplayName("Addition of two positive numbers should return correct sum")
void testAddition() {
    assertEquals(5, 2 + 3);
}
```

#### @Disabled
Skips test execution (temporarily disable tests).

```java
@Test
@Disabled("Feature not yet implemented")
void testFutureFeature() {
    fail("Not implemented yet");
}
```

### Test Lifecycle Annotations

#### Execution Order

```
@BeforeAll (once)
    ↓
  @BeforeEach
    ↓
    @Test
    ↓
  @AfterEach
    ↓
  @BeforeEach
    ↓
    @Test
    ↓
  @AfterEach
    ↓
@AfterAll (once)
```

#### @BeforeAll
- Runs **once** before all tests
- Must be **static**
- Use for expensive setup (database connections, etc.)

```java
@BeforeAll
static void initAll() {
    System.out.println("Setting up test suite");
    // Initialize shared resources
}
```

#### @AfterAll
- Runs **once** after all tests
- Must be **static**
- Use for cleanup of shared resources

```java
@AfterAll
static void tearDownAll() {
    System.out.println("Cleaning up test suite");
    // Close connections, release resources
}
```

#### @BeforeEach
- Runs **before each** test method
- Creates fresh test objects
- Ensures test isolation

```java
@BeforeEach
void init() {
    calculator = new Calculator();
    testData = new ArrayList<>();
}
```

#### @AfterEach
- Runs **after each** test method
- Cleanup after individual tests

```java
@AfterEach
void tearDown() {
    calculator = null;
    testData.clear();
}
```

**📍 Example Location**: `BasicJUnitConceptsTest.java`

### Assertions

Assertions verify expected outcomes.

#### assertEquals / assertNotEquals

```java
@Test
void testEquals() {
    assertEquals(expected, actual);
    assertEquals(10, calculator.add(5, 5));
    assertEquals("Hello", "Hello");
    
    assertNotEquals(5, 10);
}
```

#### assertTrue / assertFalse

```java
@Test
void testBoolean() {
    assertTrue(5 > 3);
    assertTrue(calculator.isPositive(10));
    
    assertFalse(3 > 5);
    assertFalse(calculator.isPositive(-5));
}
```

#### assertNull / assertNotNull

```java
@Test
void testNull() {
    String nullString = null;
    assertNull(nullString);
    
    String notNull = "Hello";
    assertNotNull(notNull);
}
```

#### assertThrows
Tests that code throws expected exception.

```java
@Test
void testException() {
    Exception exception = assertThrows(
        ArithmeticException.class,
        () -> calculator.divide(10, 0)
    );
    
    assertEquals("Cannot divide by zero", exception.getMessage());
}
```

**📍 Real Example**: `UserServiceMockitoTest.java` - `testGetUserById_WhenUserNotFound_ThrowsException()`

#### assertAll
Groups multiple assertions; all execute even if some fail.

```java
@Test
void testGrouped() {
    assertAll("calculator operations",
        () -> assertEquals(5, calculator.add(2, 3)),
        () -> assertEquals(6, calculator.multiply(2, 3)),
        () -> assertTrue(calculator.isPositive(5))
    );
}
```

---

## Advanced JUnit Features

### Parameterized Tests

Run same test with different inputs.

#### @ValueSource
Simple array of values for single parameter.

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 5, 8, 13})
void testPositiveNumbers(int number) {
    assertTrue(number > 0);
}

@ParameterizedTest
@ValueSource(strings = {"apple", "banana", "cherry"})
void testStrings(String fruit) {
    assertNotNull(fruit);
    assertTrue(fruit.length() > 0);
}
```

#### @CsvSource
Multiple parameters in CSV format.

```java
@ParameterizedTest
@CsvSource({
    "2, 3, 5",
    "10, 20, 30",
    "-5, 5, 0"
})
void testAddition(int a, int b, int expected) {
    assertEquals(expected, a + b);
}
```

#### @MethodSource
Complex objects from a method.

```java
@ParameterizedTest
@MethodSource("ticketPriceProvider")
void testTicketPrice(String type, int quantity, double expectedPrice) {
    double price = calculatePrice(type, quantity);
    assertEquals(expectedPrice, price, 0.01);
}

static Stream<Arguments> ticketPriceProvider() {
    return Stream.of(
        Arguments.of("ECONOMY", 1, 50.0),
        Arguments.of("BUSINESS", 1, 150.0),
        Arguments.of("VIP", 1, 300.0)
    );
}
```

#### @EnumSource
Test all or specific enum values.

```java
@ParameterizedTest
@EnumSource(TicketType.class)
void testAllTicketTypes(TicketType type) {
    assertNotNull(type);
    assertTrue(type.getBasePrice() > 0);
}

@ParameterizedTest
@EnumSource(value = TicketType.class, names = {"BUSINESS", "VIP"})
void testPremiumTypes(TicketType type) {
    assertTrue(type.getBasePrice() >= 150.0);
}
```

**📍 Location**: `ParameterizedAndRepeatedTestsTest.java`

### Repeated Tests

Run test multiple times.

#### @RepeatedTest

```java
@RepeatedTest(5)
void testRepeated() {
    int random = (int) (Math.random() * 100);
    assertTrue(random >= 0 && random < 100);
}

@RepeatedTest(value = 3, name = "Repetition {currentRepetition} of {totalRepetitions}")
void testWithCustomName() {
    assertTrue(true);
}
```

#### With RepetitionInfo

```java
@RepeatedTest(5)
void testWithInfo(RepetitionInfo info) {
    int current = info.getCurrentRepetition();
    int total = info.getTotalRepetitions();
    
    System.out.println("Running " + current + " of " + total);
    assertTrue(current <= total);
}
```

---

## Mockito Fundamentals

### Why Mocking?

**Problem**: Testing a service that depends on database, API, etc.

```java
class BookingService {
    private UserRepository userRepo;  // Database dependency
    private TicketService ticketService;  // Another service
    
    public Booking createBooking(Long userId, Long ticketId, int quantity) {
        User user = userRepo.findById(userId);  // ❌ Need real database?
        Ticket ticket = ticketService.getTicket(ticketId);  // ❌ Need real service?
        // ... booking logic
    }
}
```

**Solution**: Use **mocks** (fake objects) instead of real dependencies.

### Mockito Setup

#### Maven Dependency

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

#### Enable Mockito

```java
@ExtendWith(MockitoExtension.class)  // Enable Mockito
class MyTest {
    // tests here
}
```

### Core Annotations

#### @Mock
Creates a mock object (fake implementation).

```java
@Mock
private UserRepository userRepository;  // Fake repository
```

#### @InjectMocks
Creates instance and **injects mocks** into it.

```java
@InjectMocks
private UserService userService;  // Real service with mocked dependencies
```

#### @Captor
Captures arguments passed to mocked methods.

```java
@Captor
private ArgumentCaptor<User> userCaptor;
```

### Basic Example

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;  // Fake repository
    
    @InjectMocks
    private UserService userService;  // Real service with fake repo
    
    @Test
    void testGetUser() {
        // ARRANGE: Define mock behavior
        User mockUser = new User(1L, "John");
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        
        // ACT: Call real service (which uses mock)
        User result = userService.getUserById(1L);
        
        // ASSERT: Verify result
        assertEquals("John", result.getName());
        
        // VERIFY: Check mock was called
        verify(userRepository).findById(1L);
    }
}
```

**📍 Location**: `UserServiceMockitoTest.java`

---

## Mockito Core Concepts

### Stubbing with when().thenReturn()

Define what mock should return when method is called.

```java
@Test
void testStubbing() {
    // When findById(1L) is called, return testUser
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    User result = userService.getUserById(1L);
    
    assertEquals(testUser, result);
}
```

### Stubbing Exceptions with when().thenThrow()

Make mock throw exception.

```java
@Test
void testException() {
    // When findById is called, throw exception
    when(userRepository.findById(999L))
        .thenThrow(new ResourceNotFoundException("User not found"));
    
    assertThrows(ResourceNotFoundException.class, () -> {
        userService.getUserById(999L);
    });
}
```

### Argument Matchers

Match method arguments flexibly.

#### Common Matchers

```java
// Match any value of type
any(User.class)
anyString()
anyInt()
anyLong()
anyBoolean()

// Match exact value
eq(5)
eq("exact string")

// Match null
isNull()
isNotNull()
```

#### Examples

```java
@Test
void testMatchers() {
    // Match any User object
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    
    // Match any string
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
    
    // Match exact value
    when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
}
```

⚠️ **IMPORTANT**: When using matchers, **ALL arguments must use matchers**.

```java
// ❌ WRONG - mixing matcher with exact value
when(service.method(anyString(), 5)).thenReturn(result);

// ✅ CORRECT - all matchers
when(service.method(anyString(), eq(5))).thenReturn(result);

// ✅ CORRECT - no matchers
when(service.method("exact", 5)).thenReturn(result);
```

**📍 Example**: `BookingServiceMockitoTest.java` - `testArgumentMatcherMixing()`

### Void Method Stubbing

#### doNothing()
Explicitly state void method does nothing (default behavior).

```java
@Test
void testVoidMethod() {
    doNothing().when(userRepository).delete(any(User.class));
    
    userService.deleteUser(1L);
    
    verify(userRepository).delete(any(User.class));
}
```

#### doThrow()
Make void method throw exception.

```java
@Test
void testVoidException() {
    doThrow(new RuntimeException("Delete failed"))
        .when(userRepository).delete(any(User.class));
    
    assertThrows(RuntimeException.class, () -> {
        userService.deleteUser(1L);
    });
}
```

### Verification

Verify mock methods were called.

#### verify()

```java
@Test
void testVerify() {
    userService.getUserById(1L);
    
    // Verify method was called
    verify(userRepository).findById(1L);
}
```

#### Verification Modes

```java
verify(mock, times(2)).method();      // Exactly 2 times
verify(mock, never()).method();        // Never called
verify(mock, atLeastOnce()).method();  // At least once
verify(mock, atLeast(3)).method();     // At least 3 times
verify(mock, atMost(5)).method();      // At most 5 times
```

#### verifyNoMoreInteractions()

```java
@Test
void testNoMoreInteractions() {
    userService.getUserById(1L);
    
    verify(userRepository).findById(1L);
    verifyNoMoreInteractions(userRepository);  // No other methods called
}
```

**📍 Location**: `UserServiceMockitoTest.java` - `testVerificationMethods()`

### Argument Captors

Capture arguments passed to mocks for detailed verification.

```java
@Captor
private ArgumentCaptor<User> userCaptor;

@Test
void testCaptor() {
    when(userRepository.save(any(User.class))).thenReturn(testUser);
    
    userService.createUser(newUser);
    
    // Capture the User passed to save()
    verify(userRepository).save(userCaptor.capture());
    
    // Assert on captured value
    User captured = userCaptor.getValue();
    assertEquals("John", captured.getName());
    assertEquals("john@example.com", captured.getEmail());
}
```

**📍 Location**: `BookingServiceMockitoTest.java` - `testCancelBooking_WithArgumentCaptor()`

---

## Advanced Mockito Techniques

### Spies vs Mocks

#### Mock
- **Fake** object with no real behavior
- All methods return defaults (null, 0, false) unless stubbed
- Use when you want **full control**

```java
UserRepository mock = mock(UserRepository.class);
mock.findById(1L);  // Returns null (not stubbed)
```

#### Spy
- **Real** object with real behavior
- Real methods are called unless stubbed
- Use for **partial mocking**

```java
UserRepository spy = spy(new UserRepositoryImpl());
spy.findById(1L);  // Calls REAL method

when(spy.findById(1L)).thenReturn(mockUser);  // Now stubbed
spy.findById(1L);  // Returns mockUser
```

#### Example

```java
@Test
void testSpyVsMock() {
    // Mock: Returns 0 by default
    BookingConfigProperties mock = mock(BookingConfigProperties.class);
    assertEquals(0, mock.getMaxTicketsPerUser());
    
    // Spy: Calls real method
    BookingConfigProperties spy = spy(new BookingConfigProperties());
    assertEquals(10, spy.getMaxTicketsPerUser());  // Real default value
    
    // Can stub spy
    when(spy.getMaxTicketsPerUser()).thenReturn(5);
    assertEquals(5, spy.getMaxTicketsPerUser());
}
```

**📍 Location**: `BookingServiceMockitoTest.java` - `testSpyVsMock()`

### Answer Interface

Custom behavior based on arguments.

```java
@Test
void testWithAnswer() {
    when(ticketService.calculateTotalPrice(anyLong(), anyInt()))
        .thenAnswer(invocation -> {
            Long ticketId = invocation.getArgument(0);
            Integer quantity = invocation.getArgument(1);
            
            // Custom logic
            return BigDecimal.valueOf(100).multiply(BigDecimal.valueOf(quantity));
        });
    
    assertEquals(new BigDecimal("200"), ticketService.calculateTotalPrice(1L, 2));
    assertEquals(new BigDecimal("500"), ticketService.calculateTotalPrice(1L, 5));
}
```

### Verifying Call Order

```java
@Test
void testCallOrder() {
    userService.createUser(user);
    
    InOrder inOrder = inOrder(userRepository);
    inOrder.verify(userRepository).existsByEmail(anyString());
    inOrder.verify(userRepository).save(any(User.class));
}
```

### Multiple Return Values

```java
@Test
void testMultipleReturns() {
    when(repository.findById(1L))
        .thenReturn(user1)
        .thenReturn(user2)
        .thenThrow(new RuntimeException());
    
    assertEquals(user1, repository.findById(1L));  // First call
    assertEquals(user2, repository.findById(1L));  // Second call
    assertThrows(RuntimeException.class, () -> repository.findById(1L));  // Third call
}
```

**📍 Location**: `BookingServiceMockitoTest.java`

---

## Spring Boot Testing

### Configuration Testing

#### @ConfigurationProperties

Test property binding.

```java
@SpringBootTest
class ConfigTest {
    
    @Autowired
    private BookingConfigProperties config;
    
    @Test
    void testProperties() {
        assertEquals(10, config.getMaxTicketsPerUser());
        assertEquals(50.0, config.getCancellationFee());
    }
}
```

#### @Value Injection

```java
@SpringBootTest
class ValueTest {
    
    @Value("${booking.max-tickets-per-user}")
    private int maxTickets;
    
    @Test
    void testValue() {
        assertEquals(10, maxTickets);
    }
}
```

#### Environment Bean

```java
@SpringBootTest
class EnvironmentTest {
    
    @Autowired
    private Environment environment;
    
    @Test
    void testEnvironment() {
        String value = environment.getProperty("booking.max-tickets-per-user");
        assertEquals("10", value);
        
        String withDefault = environment.getProperty("non.existent", "default");
        assertEquals("default", withDefault);
    }
}
```

**📍 Location**: `ConfigurationPropertiesTest.java`

### Testing with Profiles

#### @ActiveProfiles

```java
@SpringBootTest
@ActiveProfiles("test")
class ProfileTest {
    
    @Autowired
    private BookingConfigProperties config;
    
    @Test
    void testProfileProperties() {
        // Values from application-test.properties
        assertEquals(5, config.getMaxTicketsPerUser());
    }
}
```

### @TestPropertySource

Override properties for specific test.

```java
@SpringBootTest
@TestPropertySource(properties = {
    "booking.max-tickets-per-user=3",
    "booking.cancellation-fee=10.0"
})
class PropertyOverrideTest {
    
    @Autowired
    private BookingConfigProperties config;
    
    @Test
    void testOverride() {
        assertEquals(3, config.getMaxTicketsPerUser());
        assertEquals(10.0, config.getCancellationFee());
    }
}
```

### Integration Testing

Full Spring context with real database.

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional  // Rollback after each test
class IntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testCompleteFlow() {
        // Use real services and repositories
        User user = userService.createUser(newUser);
        assertNotNull(user.getId());
        
        User found = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(user.getEmail(), found.getEmail());
    }
}
```

**📍 Location**: `BookingIntegrationTest.java`

---

## Best Practices

### 1. Test Naming Conventions

```java
// ✅ GOOD: Descriptive names
@Test
void getUserById_WhenUserExists_ReturnsUser() { }

@Test
void createBooking_WhenUserInactive_ThrowsException() { }

@Test
void calculateRefund_WhenBookingCancelled_ReturnsCorrectAmount() { }

// ❌ BAD: Vague names
@Test
void test1() { }

@Test
void testUser() { }
```

### 2. One Assertion Per Concept

```java
// ✅ GOOD
@Test
void testUserCreation() {
    User user = userService.createUser(newUser);
    assertNotNull(user.getId());
}

@Test
void testUserEmail() {
    User user = userService.createUser(newUser);
    assertEquals("test@example.com", user.getEmail());
}

// ⚠️ ACCEPTABLE with assertAll
@Test
void testUserProperties() {
    User user = userService.createUser(newUser);
    assertAll(
        () -> assertNotNull(user.getId()),
        () -> assertEquals("test@example.com", user.getEmail()),
        () -> assertTrue(user.getActive())
    );
}
```

### 3. Test Independence

```java
// ❌ BAD: Tests depend on each other
static User sharedUser;

@Test
void test1() {
    sharedUser = userService.createUser(newUser);
}

@Test
void test2() {
    userService.updateUser(sharedUser.getId(), updates);  // Depends on test1
}

// ✅ GOOD: Each test is independent
@BeforeEach
void setUp() {
    testUser = userService.createUser(newUser);
}

@Test
void test1() {
    userService.updateUser(testUser.getId(), updates);
}

@Test
void test2() {
    userService.deleteUser(testUser.getId());
}
```

### 4. DRY Principle with @BeforeEach

```java
@BeforeEach
void setUp() {
    // Common setup for all tests
    testUser = User.builder()
        .name("Test User")
        .email("test@example.com")
        .active(true)
        .build();
    
    when(userRepository.save(any(User.class))).thenReturn(testUser);
}
```

### 5. Verify Only What Matters

```java
// ❌ OVER-VERIFICATION
verify(userRepository).findById(1L);
verify(userRepository, times(1)).findById(1L);
verify(userRepository, atLeastOnce()).findById(1L);
verifyNoMoreInteractions(userRepository);

// ✅ SUFFICIENT
verify(userRepository).findById(1L);
```

### 6. Use Descriptive Messages

```java
// ✅ GOOD
assertEquals(expected, actual, "Booking total should include all ticket prices");
assertTrue(user.getActive(), "User should be active after creation");

// ❌ BAD
assertEquals(expected, actual);
assertTrue(user.getActive());
```

### 7. Test Edge Cases

```java
@Test
void testWithNullInput() { }

@Test
void testWithEmptyList() { }

@Test
void testWithNegativeNumber() { }

@Test
void testWithMaxValue() { }
```

---

## Running Tests

### From Command Line

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceMockitoTest

# Run specific test method
mvn test -Dtest=UserServiceMockitoTest#testGetUserById_WhenUserExists

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Skip tests
mvn install -DskipTests
```

### From IDE

**IntelliJ IDEA / VS Code:**
- Right-click test class → Run
- Click green arrow next to test method
- Run all tests in package

### Test Reports

After running `mvn test`, reports are in:
```
target/surefire-reports/
├── TEST-*.xml
└── *.txt
```

---

## Project Structure

```
ticket-booking-system/
├── src/
│   ├── main/
│   │   ├── java/com/ticketbooking/
│   │   │   ├── model/              # Domain entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Ticket.java
│   │   │   │   └── Booking.java
│   │   │   ├── repository/         # Data access
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── TicketRepository.java
│   │   │   │   └── BookingRepository.java
│   │   │   ├── service/            # Business logic
│   │   │   │   ├── UserService.java
│   │   │   │   ├── TicketService.java
│   │   │   │   └── BookingService.java
│   │   │   ├── controller/         # REST APIs
│   │   │   │   ├── UserController.java
│   │   │   │   └── BookingController.java
│   │   │   ├── config/             # Configuration
│   │   │   │   └── BookingConfigProperties.java
│   │   │   └── exception/          # Custom exceptions
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-test.properties
│   └── test/
│       ├── java/com/ticketbooking/
│       │   ├── basic/              # JUnit basics
│       │   │   ├── BasicJUnitConceptsTest.java
│       │   │   └── ParameterizedAndRepeatedTestsTest.java
│       │   ├── mockito/            # Mockito concepts
│       │   │   ├── UserServiceMockitoTest.java
│       │   │   └── BookingServiceMockitoTest.java
│       │   ├── config/             # Configuration tests
│       │   │   └── ConfigurationPropertiesTest.java
│       │   └── integration/        # Integration tests
│       │       └── BookingIntegrationTest.java
│       └── resources/
│           └── application-test.properties
└── pom.xml
```

---

## Key Concepts Summary

### Unit Testing
- ✅ Test smallest units in isolation
- ✅ Fast, independent, repeatable
- ✅ Mock external dependencies

### JUnit 5
- ✅ `@Test` - Mark test methods
- ✅ `@BeforeEach/@AfterEach` - Setup/teardown per test
- ✅ `@BeforeAll/@AfterAll` - Setup/teardown once
- ✅ `@DisplayName` - Readable test names
- ✅ `@ParameterizedTest` - Multiple inputs
- ✅ `@RepeatedTest` - Run multiple times

### Mockito
- ✅ `@Mock` - Create mock objects
- ✅ `@InjectMocks` - Inject mocks
- ✅ `when().thenReturn()` - Stub methods
- ✅ `when().thenThrow()` - Stub exceptions
- ✅ `verify()` - Verify method calls
- ✅ `any()`, `eq()` - Argument matchers
- ✅ `@Captor` - Capture arguments
- ✅ `spy()` - Partial mocking

### Spring Boot Testing
- ✅ `@SpringBootTest` - Full context
- ✅ `@ActiveProfiles` - Use test profile
- ✅ `@TestPropertySource` - Override properties
- ✅ `@Transactional` - Rollback tests

---

## Learning Path

### Beginner
1. ✅ Understand unit testing concepts
2. ✅ Learn basic JUnit annotations (`@Test`, `@BeforeEach`)
3. ✅ Master basic assertions
4. ✅ Practice AAA pattern

**📂 Start with**: `BasicJUnitConceptsTest.java`

### Intermediate
1. ✅ Parameterized tests
2. ✅ Introduction to Mockito
3. ✅ Basic mocking and stubbing
4. ✅ Verification

**📂 Practice with**: `ParameterizedAndRepeatedTestsTest.java`, `UserServiceMockitoTest.java`

### Advanced
1. ✅ Argument matchers
2. ✅ Argument captors
3. ✅ Spies vs Mocks
4. ✅ Complex stubbing scenarios
5. ✅ Integration testing
6. ✅ Configuration testing

**📂 Master with**: `BookingServiceMockitoTest.java`, `BookingIntegrationTest.java`, `ConfigurationPropertiesTest.java`

---

## Common Pitfalls & Solutions

### 1. Mixing Matchers with Exact Values

```java
// ❌ WRONG
when(service.method(anyString(), 5)).thenReturn(result);

// ✅ CORRECT
when(service.method(anyString(), eq(5))).thenReturn(result);
```

### 2. Forgetting to Stub

```java
// ❌ Mock returns null
@Mock
private UserRepository userRepository;

User user = userRepository.findById(1L);  // Returns null!

// ✅ Stub the mock
when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
```

### 3. Testing Implementation Instead of Behavior

```java
// ❌ BAD: Testing internals
verify(userRepository).save(any(User.class));
verify(userRepository, times(1)).save(any(User.class));

// ✅ GOOD: Testing behavior
User result = userService.createUser(newUser);
assertNotNull(result.getId());
assertEquals("test@example.com", result.getEmail());
```

### 4. Non-Isolated Tests

```java
// ❌ Tests affect each other
static User sharedUser;

// ✅ Isolated with @BeforeEach
@BeforeEach
void setUp() {
    testUser = createFreshUser();
}
```

---

## Resources

### Official Documentation
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

### Test Files Reference
- **Basic JUnit**: `BasicJUnitConceptsTest.java`
- **Parameterized Tests**: `ParameterizedAndRepeatedTestsTest.java`
- **Mockito Basics**: `UserServiceMockitoTest.java`
- **Advanced Mockito**: `BookingServiceMockitoTest.java`
- **Configuration**: `ConfigurationPropertiesTest.java`
- **Integration**: `BookingIntegrationTest.java`

---

## Next Steps

1. ✅ Run all tests: `mvn test`
2. ✅ Read through each test file
3. ✅ Modify tests to experiment
4. ✅ Add your own test cases
5. ✅ Build a similar project
6. ✅ Practice TDD (Test-Driven Development)

---

## Conclusion

You now have a **complete understanding** of:
- ✅ Unit testing fundamentals
- ✅ JUnit 5 from basic to advanced
- ✅ Mockito for mocking dependencies
- ✅ Spring Boot testing strategies
- ✅ Best practices and patterns

**Happy Testing! 🎉**

---

*Created for learning JUnit and Mockito from beginner to advanced level.*
