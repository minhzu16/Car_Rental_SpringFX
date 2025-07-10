# Selenium Testing Notes

## About the Connection Error

When running the tests, you might see errors like:
```
org.openqa.selenium.WebDriverException: unknown error: net::ERR_CONNECTION_REFUSED
```

This is expected because the tests try to connect to http://localhost:8080 where your application should be running.

## Running Options

### Option 1: Run with Application (Actual Integration Tests)

1. Start your Car Rental Management application:
   ```
   mvn spring-boot:run
   ```

2. In a separate terminal, run the tests:
   ```
   mvn test -Dtest=CarRegistrationTest,LoginTest
   ```

### Option 2: Mock Tests (No Application Needed)

If you want to run tests without starting the application, use the mock test classes:

```
mvn test -Dtest=MockLoginTest,MockCarRegistrationTest
```

These tests use locally generated HTML files that simulate your application's behavior, allowing you to test the validation logic without needing a running server.

### Option 3: Demo Mode (Simple Simulation)

If you just want to demonstrate the test structure without actual browser interactions:

```
mvn test -Dtest=DemoSeleniumTest
```

This class contains the same test cases but simulates actions using console output rather than actual browser interactions.

## Test Structure

The tests follow the Page Object Model pattern and test three validation cases:

1. **Registration16**: Minimum car capacity validation (2 seats required)
2. **Registration17**: Minimum rent price validation (10$ required)
3. **Registration18**: Valid status value validation

## Test Approaches

This project demonstrates three approaches to Selenium testing:

1. **Full Integration Tests** - Test against the actual running application
   - Most realistic tests
   - Requires application to be running
   - Examples: `LoginTest.java`, `CarRegistrationTest.java`

2. **Mock Tests** - Use locally generated HTML files
   - No server needed
   - Good for CI/CD environments
   - Examples: `MockLoginTest.java`, `MockCarRegistrationTest.java`

3. **Demo Tests** - Console simulation only
   - No browser or server needed
   - Used for demonstration purposes
   - Example: `DemoSeleniumTest.java`

## Code Organization

### Constants Approach

To improve maintainability, we use a constants-based approach:

- `CarFormConstants.java` - Contains all IDs, selectors, and error messages
- Page objects use these constants instead of hardcoded strings
- Tests use the constants for consistent validation

Benefits:
- Single place to update if element IDs change
- Consistent naming across tests
- Reduced typo errors
- Easier to understand the relationship between elements

### Page Objects

- `LoginPage.java` - Handles login functionality
- `CarFormPage.java` - Handles car form operations with helper methods

## Adapting to Your Environment

You may need to modify:

1. The base URL in `BaseTest.java` if your application runs on a different port
2. The login credentials in `CarRegistrationTest.java`
3. Field IDs and XPaths in `CarFormConstants.java` if they don't match your UI structure 