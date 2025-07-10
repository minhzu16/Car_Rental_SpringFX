package org.selenium.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.selenium.constants.CarFormConstants;
import org.selenium.pages.AdminPage;
import org.selenium.pages.CarFormPage;
import org.selenium.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for car registration validation tests
 */
public class CarRegistrationTest extends BaseTest {
    
    // Admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    
    /**
     * Test case: TC16
     * Verify that an error message is shown when capacity is set to 1 seat
     * (Minimum required is 2 seats)
     */
    @Test
    @DisplayName("TC16: Verify Capacity with 1 seat")
    public void testCapacityMinimumValue() {
        try {
            System.out.println("Starting test: TC16 - Verify Capacity with 1 seat");
            
            // 1. Log in to the application as admin
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Login and navigate to admin page
            AdminPage adminPage = loginPage.loginAsAdmin(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertNotNull(adminPage, "Admin page should be accessible after login");
            
            // 2. Navigate to Car Management
            adminPage.navigateToCarManagement();
            
            // 3. Navigate to Add New Car page
            CarFormPage carFormPage = adminPage.navigateToAddCar();
            
            // 4. Fill form with test data from TC16
            carFormPage.setName("Car1");
            carFormPage.selectProducer(); // Chọn Producer1
            carFormPage.setModelYear("2020");
            carFormPage.setColor("Red");
            carFormPage.setCapacity("1"); // Test value: 1 seat
            carFormPage.setRentPrice("50");
            carFormPage.setStatus("Available");
            carFormPage.setImageUrl("images/car.jpg");
            carFormPage.setDescription("Test car description");
            
            // 5. Submit form
            carFormPage.submitForm();
            
            // 6. Verify error message is displayed
            boolean isErrorDisplayed = carFormPage.isCapacityErrorDisplayed();
            if (!isErrorDisplayed) {
                handleErrorAndExit("Expected capacity error message was not displayed");
            }
            
            assertTrue(isErrorDisplayed, "Error message for minimum 2 seats should be displayed");
            System.out.println("Test TC16 completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                handleErrorAndExit("Make sure the application is running at " + baseUrl);
            }
            handleErrorAndExit("WebDriver exception: " + e.getMessage());
        } catch (Exception e) {
            handleErrorAndExit("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Test case: TC17
     * Verify that an error message is shown when rent price is set to 9$
     * (Minimum required is 10$)
     */
    @Test
    @DisplayName("TC17: Verify Rent Price with 9$")
    public void testRentPriceMinimumValue() {
        try {
            System.out.println("Starting test: TC17 - Verify Rent Price with 9$");
            
            // 1. Log in to the application as admin
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Login and navigate to admin page
            AdminPage adminPage = loginPage.loginAsAdmin(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertNotNull(adminPage, "Admin page should be accessible after login");
            
            // 2. Navigate to Car Management
            adminPage.navigateToCarManagement();
            
            // 3. Navigate to Add New Car page
            CarFormPage carFormPage = adminPage.navigateToAddCar();
            
            // 4. Fill form with test data from TC17
            carFormPage.setName("Car1");
            carFormPage.selectProducer(); // Chọn Producer1
            carFormPage.setModelYear("2020");
            carFormPage.setColor("Red");
            carFormPage.setCapacity("5");
            carFormPage.setRentPrice("9"); // Test value: 9$
            carFormPage.setStatus("Available");
            carFormPage.setImageUrl("images/car.jpg");
            carFormPage.setDescription("Test car description");
            
            // 5. Submit form
            carFormPage.submitForm();
            
            // 6. Verify error message is displayed
            boolean isErrorDisplayed = carFormPage.isRentPriceErrorDisplayed();
            if (!isErrorDisplayed) {
                handleErrorAndExit("Expected rent price error message was not displayed");
            }
            
            assertTrue(isErrorDisplayed, "Error message for minimum 10$ should be displayed");
            System.out.println("Test TC17 completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                handleErrorAndExit("Make sure the application is running at " + baseUrl);
            }
            handleErrorAndExit("WebDriver exception: " + e.getMessage());
        } catch (Exception e) {
            handleErrorAndExit("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Test case: TC18
     * Verify that an error message is shown when status is set to an invalid value "XYZ"
     */
    @Test
    @DisplayName("TC18: Verify Status with invalid option (XYZ)")
    public void testInvalidStatus() {
        try {
            System.out.println("Starting test: TC18 - Verify Status with invalid option (XYZ)");
            
            // 1. Log in to the application as admin
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Login and navigate to admin page
            AdminPage adminPage = loginPage.loginAsAdmin(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertNotNull(adminPage, "Admin page should be accessible after login");
            
            // 2. Navigate to Car Management
            adminPage.navigateToCarManagement();
            
            // 3. Navigate to Add New Car page
            CarFormPage carFormPage = adminPage.navigateToAddCar();
            
            // 4. Fill form with test data from TC18
            carFormPage.setName("Car1");
            carFormPage.selectProducer(); // Chọn Producer1
            carFormPage.setModelYear("2020");
            carFormPage.setColor("Red");
            carFormPage.setCapacity("5");
            carFormPage.setRentPrice("50");
            carFormPage.setStatus("XYZ"); // Test value: XYZ (invalid)
            carFormPage.setImageUrl("images/car.jpg");
            carFormPage.setDescription("Test car description");
            
            // 5. Submit form
            carFormPage.submitForm();
            
            // 6. Verify error message is displayed
            boolean isErrorDisplayed = carFormPage.isStatusErrorDisplayed();
            if (!isErrorDisplayed) {
                handleErrorAndExit("Expected status error message was not displayed");
            }
            
            assertTrue(isErrorDisplayed, "Error message for invalid status should be displayed");
            System.out.println("Test TC18 completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                handleErrorAndExit("Make sure the application is running at " + baseUrl);
            }
            handleErrorAndExit("WebDriver exception: " + e.getMessage());
        } catch (Exception e) {
            handleErrorAndExit("Unexpected error: " + e.getMessage());
        }
    }
    
    /**
     * Test case: TC19
     * Verify that an error message is shown when image URL is invalid
     */
    @Test
    @DisplayName("TC19: Verify Image URL with invalid format")
    public void testInvalidImageURL() {
        try {
            System.out.println("Starting test: TC19 - Verify Image URL with invalid format");
            
            // 1. Log in to the application as admin
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Login and navigate to admin page
            AdminPage adminPage = loginPage.loginAsAdmin(ADMIN_USERNAME, ADMIN_PASSWORD);
            assertNotNull(adminPage, "Admin page should be accessible after login");
            
            // 2. Navigate to Car Management
            adminPage.navigateToCarManagement();
            
            // 3. Navigate to Add New Car page
            CarFormPage carFormPage = adminPage.navigateToAddCar();
            
            // 4. Fill form with test data from TC19
            carFormPage.setName("Car1");
            carFormPage.selectProducer(); // Chọn Producer1
            carFormPage.setModelYear("2020");
            carFormPage.setColor("Red");
            carFormPage.setCapacity("5");
            carFormPage.setRentPrice("50");
            carFormPage.setStatus("Available");
            carFormPage.setImageUrl("car.jpg"); // Test value: car.jpg (invalid format)
            carFormPage.setDescription("Test car description");
            
            // 5. Submit form
            carFormPage.submitForm();
            
            // 6. Verify error message is displayed
            boolean isErrorDisplayed = carFormPage.isImageURLErrorDisplayed();
            if (!isErrorDisplayed) {
                handleErrorAndExit("Expected image URL error message was not displayed");
            }
            
            assertTrue(isErrorDisplayed, "Error message for invalid image URL format should be displayed");
            System.out.println("Test TC19 completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                handleErrorAndExit("Make sure the application is running at " + baseUrl);
            }
            handleErrorAndExit("WebDriver exception: " + e.getMessage());
        } catch (Exception e) {
            handleErrorAndExit("Unexpected error: " + e.getMessage());
        }
    }
} 