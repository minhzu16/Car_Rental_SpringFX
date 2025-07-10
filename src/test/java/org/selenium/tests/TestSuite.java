package org.selenium.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test suite that organizes all the tests in the project.
 * This allows running all tests with a single command.
 */
@Suite
@SuiteDisplayName("Car Rental System Test Suite")
@SelectClasses({
    // Simple test to verify the testing environment
    SimpleTest.class,
    
    // Integration tests (require running application)
    LoginTest.class,
    CarRegistrationTest.class,
    
    // Mock tests (don't require running application)
    MockLoginTest.class,
    MockCarRegistrationTest.class
})
public class TestSuite {
    // This class doesn't need any code - it's just a container for the test suite configuration
} 