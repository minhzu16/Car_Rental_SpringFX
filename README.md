# Car Rental System - Selenium Tests

This project includes automated tests for the Car Rental System using Selenium WebDriver, JUnit, and the Page Object Model pattern.

## Test Cases Implemented

Three validation test cases have been implemented for the car registration form:

1. **Registration16**: Verifies that an error message is shown when capacity is set to 1 seat (minimum required is 2 seats)
2. **Registration17**: Verifies that an error message is shown when rent price is set to 9$ (minimum required is 10$)
3. **Registration18**: Verifies that an error message is shown when status is set to an invalid value "XYZ"

## Project Structure

- `src/test/java/org/selenium/tests/`
  - `BaseTest.java`: Setup and teardown for WebDriver
  - `SimpleTest.java`: Basic JUnit test to verify test setup
  - `CarRegistrationTest.java`: Real application tests for car registration
  - `LoginTest.java`: Real application tests for login functionality
  - `MockCarRegistrationTest.java`: Mock tests that don't require a running application
  - `MockLoginTest.java`: Mock tests for login that don't require a running application
  - `DemoSeleniumTest.java`: Simple demonstration tests with console output only
- `src/test/java/org/selenium/pages/`
  - `LoginPage.java`: Page object for login functionality
  - `CarFormPage.java`: Page object for car form operations

## Running the Tests

### Option 1: Run with Application (Actual Integration Tests)

```
# Start the application
mvn spring-boot:run

# In a separate terminal, run the tests
mvn test -Dtest=CarRegistrationTest,LoginTest
```

### Option 2: Run Mock Tests (No Application Needed)

```
mvn test -Dtest=MockLoginTest,MockCarRegistrationTest
```

### Option 3: Run Demo Tests (Console Output Only)

```
mvn test -Dtest=DemoSeleniumTest
```

## Testing Approaches

This project demonstrates three different approaches to Selenium testing:

### 1. Full Integration Tests
- Tests against the actual running application
- Provides the most realistic test scenarios
- Requires the application to be running on localhost:8080
- Examples: `LoginTest.java`, `CarRegistrationTest.java`

### 2. Mock Tests
- Uses locally generated HTML files that simulate application behavior
- No need for a running server
- Good for CI/CD environments
- Examples: `MockLoginTest.java`, `MockCarRegistrationTest.java`

### 3. Demo Tests
- Simple console-based simulation of test steps
- No browser or server needed
- Used for demonstration purposes
- Example: `DemoSeleniumTest.java`

## Test Implementation Approach

The tests follow the Page Object Model (POM) design pattern:

1. Each web page is represented by a class (e.g., LoginPage, CarFormPage)
2. UI elements are defined as private fields with @FindBy annotations
3. Page interactions are defined as public methods
4. Test logic is separated from page interaction details

Each test follows these steps:

1. Log in to the application
2. Navigate to Add New Car page
3. Enter test values (invalid values to trigger validation)
4. Fill other required fields with valid values
5. Submit the form
6. Verify error message is displayed

## Dependencies

- Selenium WebDriver
- JUnit 5
- WebDriverManager (for automatic driver management)

## Troubleshooting

If you encounter connection errors like `net::ERR_CONNECTION_REFUSED`, make sure:
1. Your application is running on http://localhost:8080, or
2. Use the mock tests that don't require a running application

See [TESTING-NOTES.md](TESTING-NOTES.md) for more detailed information about the testing approach. 