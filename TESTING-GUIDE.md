# Car Rental Management System - Testing Guide

## Overview

This document provides comprehensive guidance for testing the Car Rental Management System using Selenium WebDriver. The testing framework is designed to validate the functionality of the application through automated UI tests.

## Test Structure

The tests are organized following the Page Object Model (POM) pattern:

### Directory Structure

```
src/test/java/org/selenium/
├── constants/           # Constants used across tests
│   └── CarFormConstants.java
├── pages/               # Page Object classes
│   ├── CarFormPage.java
│   └── LoginPage.java
├── tests/               # Test classes
│   ├── BaseTest.java    # Base class for all tests
│   ├── CarRegistrationTest.java
│   ├── DemoSeleniumTest.java
│   ├── LoginTest.java
│   ├── MockCarRegistrationTest.java
│   ├── MockLoginTest.java
│   ├── SimpleTest.java
│   ├── TestReportGenerator.java
│   └── TestSuite.java
└── TestRunner.java      # Main test runner
```

## Test Types

### 1. Integration Tests

These tests run against the actual application and require the application to be running:

- `LoginTest.java` - Tests login functionality
- `CarRegistrationTest.java` - Tests car registration form validation

### 2. Mock Tests

These tests use locally generated HTML files and don't require the application to be running:

- `MockLoginTest.java` - Mock version of login tests
- `MockCarRegistrationTest.java` - Mock version of car registration tests

### 3. Demo Tests

These tests simulate actions using console output rather than actual browser interactions:

- `DemoSeleniumTest.java` - Demonstration tests

## Running Tests

### Prerequisites

1. Java 17 or higher
2. Maven
3. Chrome browser (version 138 or compatible)

### Option 1: Using Scripts

#### Windows

```
run-tests.bat
```

This batch script provides a menu to run different test types.

#### Linux/Mac

```
chmod +x run-tests.sh
./run-tests.sh
```

This shell script provides a menu to run different test types.

### Option 2: Using Maven Commands

#### Run All Tests

```
mvn test
```

#### Run Specific Test Class

```
mvn test -Dtest=SimpleTest
```

#### Run Multiple Test Classes

```
mvn test -Dtest=LoginTest,CarRegistrationTest
```

### Option 3: Using TestRunner

#### Windows

```
run-test-suite.bat
```

#### Linux/Mac

```
chmod +x run-test-suite.sh
./run-test-suite.sh
```

This runs the TestRunner class, which executes all tests and generates an HTML report.

## Test Reports

The TestReportGenerator creates HTML reports in the `test-reports` directory. Each report includes:

- Test execution summary (total, passed, failed, skipped)
- Execution time and duration
- Detailed results for each test

## Configuring Tests

### BaseTest.java

This class contains common setup and teardown code for all tests:

- `baseUrl` - The URL of the application (default: http://localhost:8081)
- `HEADLESS_MODE` - Whether to run tests in headless mode (default: true)

### Chrome Options

Chrome options are configured in BaseTest.java:

```java
ChromeOptions options = new ChromeOptions();
if (HEADLESS_MODE) {
    options.addArguments("--headless=new");
}
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
options.addArguments("--remote-allow-origins=*");
```

### Test Credentials

Default test credentials are set in LoginTest.java and CarRegistrationTest.java:

```java
private static final String VALID_USERNAME = "admin";
private static final String VALID_PASSWORD = "admin123";
```

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure the application is running on the expected port
   - Check the baseUrl in BaseTest.java

2. **Element Not Found**
   - Check if element IDs or XPaths match your UI structure
   - Increase implicit wait time in BaseTest.java

3. **Chrome Driver Version**
   - WebDriverManager should automatically download the correct driver
   - If issues persist, manually specify the Chrome driver version

### CDP Warning

If you see CDP (Chrome DevTools Protocol) warnings:

```
WARNING: Unable to find CDP implementation matching 138
```

This is handled by:
1. Using a compatible devtools version in pom.xml
2. Suppressing warnings in BaseTest.java

## Extending Tests

### Adding New Tests

1. Create a new test class that extends BaseTest
2. Add test methods with @Test annotation
3. Use page objects to interact with the UI

### Adding New Page Objects

1. Create a new class in the pages package
2. Use @FindBy annotations to locate elements
3. Implement methods for page interactions

## Best Practices

1. **Use Page Objects** - Keep UI interaction code separate from test logic
2. **Use Constants** - Store element selectors in constants for easy maintenance
3. **Add Logging** - Include detailed logging for troubleshooting
4. **Handle Timeouts** - Use explicit waits for dynamic elements
5. **Clean Up Resources** - Ensure WebDriver is properly closed after tests

## References

- [Selenium Documentation](https://www.selenium.dev/documentation/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [WebDriverManager Documentation](https://bonigarcia.dev/webdrivermanager/) 