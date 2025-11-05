# Ticket Booking System - Complete JUnit & Mockito Testing Guide

A comprehensive **backend application** demonstrating **Unit Testing** with **JUnit 5** and **Mockito** from **beginner to advanced** level.

## 🎯 Project Purpose

This project is a complete learning resource for:
- Understanding unit testing concepts
- Mastering JUnit 5 framework
- Learning Mockito for mocking dependencies
- Spring Boot testing strategies
- Integration testing
- Best practices and real-world patterns

## 📚 What's Included

### Application Features
- ✅ **User Management**: Create, update, activate/deactivate users
- ✅ **Ticket Management**: CRUD operations for event tickets
- ✅ **Booking System**: Book tickets, confirm, cancel bookings
- ✅ **Configuration Management**: Property-based configuration

### Testing Concepts Covered

#### JUnit 5 Basics
- `@Test`, `@DisplayName`, `@Disabled`
- `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`
- Assertions: `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertThrows`
- `@ParameterizedTest` with `@ValueSource`, `@CsvSource`, `@MethodSource`, `@EnumSource`
- `@RepeatedTest` for multiple executions

#### Mockito Concepts
- `@Mock` and `@InjectMocks` for dependency injection
- `when().thenReturn()` for stubbing
- `when().thenThrow()` for exception stubbing
- `verify()` for method call verification
- Argument matchers: `any()`, `eq()`, `anyString()`, etc.
- `doNothing()`, `doThrow()` for void methods
- `@Spy` vs `@Mock` differences
- `ArgumentCaptor` for capturing method arguments
- `Answer` interface for custom behavior
- `InOrder` for verifying call sequence

#### Spring Boot Testing
- `@SpringBootTest` for integration testing
- `@ActiveProfiles` for test profiles
- `@TestPropertySource` for property overrides
- Testing `@ConfigurationProperties`
- Testing `@Value` injection
- Using `Environment` bean in tests
- `@Transactional` for test isolation

## 🏗️ Project Structure

```
ticket-booking-system/
├── src/main/java/com/ticketbooking/
│   ├── model/                 # Domain entities (User, Ticket, Booking)
│   ├── repository/            # Spring Data JPA repositories
│   ├── service/               # Business logic layer
│   ├── controller/            # REST API controllers
│   ├── config/                # Configuration classes
│   └── exception/             # Custom exceptions
│
├── src/test/java/com/ticketbooking/
│   ├── basic/                 # Basic JUnit concepts
│   │   ├── BasicJUnitConceptsTest.java
│   │   └── ParameterizedAndRepeatedTestsTest.java
│   ├── mockito/               # Mockito concepts
│   │   ├── UserServiceMockitoTest.java
│   │   └── BookingServiceMockitoTest.java
│   ├── config/                # Configuration testing
│   │   └── ConfigurationPropertiesTest.java
│   └── integration/           # Integration tests
│       └── BookingIntegrationTest.java
│
├── notes.md                   # Comprehensive study guide
├── pom.xml                    # Maven configuration
└── README.md                  # This file
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, VS Code, Eclipse)

### Installation

1. Clone or download the project:
```bash
cd /workspaces/test_JUnit
```

2. Build the project:
```bash
mvn clean install
```

3. Run all tests:
```bash
mvn test
```

### Running Specific Tests

```bash
# Run specific test class
mvn test -Dtest=BasicJUnitConceptsTest

# Run specific test method
mvn test -Dtest=UserServiceMockitoTest#testGetUserById_WhenUserExists

# Run tests with specific profile
mvn test -Dspring.profiles.active=test
```

## 📖 Learning Path

### 1️⃣ Beginner Level
**Start here if you're new to testing:**

1. Read `notes.md` sections:
   - Introduction
   - Unit Testing Fundamentals
   - JUnit 5 Basics

2. Study and run:
   - `BasicJUnitConceptsTest.java`
   - Practice: Modify tests, add your own

3. Key concepts to master:
   - `@Test` annotation
   - Lifecycle annotations (`@BeforeEach`, `@AfterEach`)
   - Basic assertions
   - AAA pattern (Arrange-Act-Assert)

### 2️⃣ Intermediate Level
**Once comfortable with basics:**

1. Read `notes.md` sections:
   - Advanced JUnit Features
   - Mockito Fundamentals

2. Study and run:
   - `ParameterizedAndRepeatedTestsTest.java`
   - `UserServiceMockitoTest.java`

3. Key concepts:
   - Parameterized tests
   - Creating mocks with `@Mock`
   - Stubbing with `when().thenReturn()`
   - Verification with `verify()`

### 3️⃣ Advanced Level
**For mastering testing:**

1. Read `notes.md` sections:
   - Advanced Mockito Techniques
   - Spring Boot Testing
   - Best Practices

2. Study and run:
   - `BookingServiceMockitoTest.java`
   - `ConfigurationPropertiesTest.java`
   - `BookingIntegrationTest.java`

3. Key concepts:
   - Argument matchers and captors
   - Spies vs Mocks
   - Integration testing
   - Configuration testing

## 🧪 Test Examples

### Basic Test Example
```java
@Test
@DisplayName("Addition of two positive numbers should return correct sum")
void testAddition() {
    // ARRANGE
    Calculator calculator = new Calculator();
    
    // ACT
    int result = calculator.add(2, 3);
    
    // ASSERT
    assertEquals(5, result);
}
```

### Mockito Test Example
```java
@Test
void testGetUserById_WhenUserExists() {
    // ARRANGE
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    
    // ACT
    User result = userService.getUserById(1L);
    
    // ASSERT
    assertEquals("John Doe", result.getName());
    verify(userRepository).findById(1L);
}
```

### Parameterized Test Example
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

## 📝 Key Files Reference

| File | Purpose | Concepts Covered |
|------|---------|------------------|
| `notes.md` | **Complete study guide** | All concepts with explanations |
| `BasicJUnitConceptsTest.java` | JUnit fundamentals | @Test, lifecycle, assertions |
| `ParameterizedAndRepeatedTestsTest.java` | Advanced JUnit | Parameterized tests, @RepeatedTest |
| `UserServiceMockitoTest.java` | Mockito basics | @Mock, stubbing, verification |
| `BookingServiceMockitoTest.java` | Advanced Mockito | Spies, captors, complex scenarios |
| `ConfigurationPropertiesTest.java` | Spring configuration | Properties, profiles, Environment |
| `BookingIntegrationTest.java` | Integration testing | Full Spring context, database |

## 🎓 Testing Best Practices

### ✅ DO
- Use descriptive test names
- Follow AAA pattern (Arrange-Act-Assert)
- Keep tests independent
- Test one concept per test
- Use `@BeforeEach` for common setup
- Verify only what matters
- Test edge cases

### ❌ DON'T
- Mix argument matchers with exact values
- Make tests depend on each other
- Test implementation details
- Forget to stub mocks
- Over-verify method calls
- Use vague test names

## 🔧 Technologies Used

- **Java 17**
- **Spring Boot 3.1.5**
- **JUnit 5** (Jupiter)
- **Mockito 5.x**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok**
- **Maven**

## 📊 Test Coverage

The project includes:
- ✅ **50+ test methods**
- ✅ Unit tests for services
- ✅ Integration tests
- ✅ Configuration tests
- ✅ Parameterized tests
- ✅ Exception testing
- ✅ Mock verification tests

## 🤝 Contributing

This is a learning project. Feel free to:
- Add more test examples
- Improve documentation
- Fix bugs or issues
- Suggest better practices

## 📚 Additional Resources

### Official Documentation
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

### Recommended Reading
- Test-Driven Development (TDD)
- Behavior-Driven Development (BDD)
- Clean Code principles

## 💡 Tips for Learning

1. **Start with `notes.md`** - Read the comprehensive guide
2. **Run tests** - Execute and observe outputs
3. **Modify tests** - Change values, break tests, fix them
4. **Add your own** - Create new test cases
5. **Debug** - Step through tests in debugger
6. **Practice TDD** - Write tests first, then implementation

## 🎯 What You'll Master

After completing this project, you'll be able to:
- ✅ Write effective unit tests
- ✅ Mock dependencies with Mockito
- ✅ Test Spring Boot applications
- ✅ Use parameterized and repeated tests
- ✅ Test configuration and properties
- ✅ Write integration tests
- ✅ Follow testing best practices
- ✅ Apply TDD principles

## 📞 Support

For questions or issues:
1. Check `notes.md` for detailed explanations
2. Review test examples in the codebase
3. Consult official documentation

## 📄 License

This project is created for educational purposes.

---

**Happy Learning! 🚀**

*From Beginner to Advanced - Master JUnit and Mockito Testing*