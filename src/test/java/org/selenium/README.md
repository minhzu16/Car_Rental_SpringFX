# Selenium Testing Documentation

## Overview

This project includes automated UI tests using Selenium WebDriver. The tests verify the functionality of the Car Rental Management System, focusing on form validation and user authentication.

## Test Structure

The tests follow the Page Object Model (POM) pattern:

- **Page Objects**: Encapsulate the UI elements and actions for each page
  - `LoginPage.java` - Handles login functionality
  - `CarFormPage.java` - Handles car form operations

- **Test Classes**:
  - `SimpleTest.java` - Basic test to verify the testing environment
  - `LoginTest.java` - Tests login functionality
  - `CarRegistrationTest.java` - Tests car registration form validation
  - `MockLoginTest.java` - Mock version of login tests (no server needed)
  - `MockCarRegistrationTest.java` - Mock version of car registration tests
  - `DemoSeleniumTest.java` - Demonstration tests using console output
  - `TestSuite.java` - Organizes all tests into a suite

## Running the Tests

### Prerequisites

1. Java 17 or higher
2. Maven
3. Chrome browser (version 138 or compatible)

### Test Execution Options

#### Option 1: Run with Application (Integration Tests)

1. Start the Car Rental Management application:
   ```
   mvn spring-boot:run
   ```

2. In a separate terminal, run the tests:
   ```
   mvn test -Dtest=LoginTest,CarRegistrationTest
   ```

#### Option 2: Run Mock Tests (No Application Needed)

```
mvn test -Dtest=MockLoginTest,MockCarRegistrationTest
```

#### Option 3: Run Demo Tests (Console Simulation)

```
mvn test -Dtest=DemoSeleniumTest
```

#### Option 4: Run All Tests Using the Test Suite

```
mvn test -Dtest=TestSuite
```

#### Option 5: Run a Specific Test

```
mvn test -Dtest=SimpleTest
```

## Test Cases

### Login Tests

- **Valid Login**: Tests successful login with valid credentials
- **Invalid Login**: Tests error handling with invalid credentials

### Car Registration Tests

- **Registration16**: Verifies minimum car capacity validation (2 seats required)
- **Registration17**: Verifies minimum rent price validation (10$ required)
- **Registration18**: Verifies status value validation

## Configuration

### Headless Mode

By default, tests run in headless mode. To see the browser UI during test execution, modify `BaseTest.java`:

```java
protected final boolean HEADLESS_MODE = false;
```

### Base URL

The default base URL is `http://localhost:8081`. To change it, modify `BaseTest.java`:

```java
protected final String baseUrl = "http://your-application-url";
```

### Test Credentials

Default test credentials are set in `LoginTest.java` and `CarRegistrationTest.java`. Update them if needed:

```java
private static final String VALID_USERNAME = "your-username";
private static final String VALID_PASSWORD = "your-password";
```

## Troubleshooting

### Common Issues

1. **Connection Refused**: Ensure the application is running on the expected port
2. **Element Not Found**: Check if element IDs or XPaths match your UI structure
3. **Chrome Driver Version**: Make sure WebDriverManager is configured correctly

### Debug Logging

The tests include debug logging to help identify issues:

```
mvn test -Dtest=SimpleTest -Dorg.slf4j.simpleLogger.defaultLogLevel=debug
``` 