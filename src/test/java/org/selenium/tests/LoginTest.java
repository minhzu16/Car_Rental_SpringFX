package org.selenium.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;
import org.selenium.pages.LoginPage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify if login functionality works
 */
public class LoginTest extends BaseTest {
    
    // Credentials for testing
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";
    private static final String INVALID_USERNAME = "invalid_user";
    private static final String INVALID_PASSWORD = "invalid_pass";
    
    /**
     * Test case: Login with valid credentials
     */
    @Test
    @DisplayName("Login with valid credentials")
    public void testLoginWithValidCredentials() {
        try {
            System.out.println("Starting test: Login with valid credentials");
            
            // Navigate to login page
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Print page title to help debug
            System.out.println("Page title: " + driver.getTitle());
            
            // Attempt login with valid credentials
            loginPage.login(VALID_USERNAME, VALID_PASSWORD);
            
            // Wait for redirection and verify success
            boolean redirected = loginPage.waitForRedirection(5);
            assertTrue(redirected, "User should be redirected after successful login");
            
            // Verify login was successful
            boolean loginSuccessful = loginPage.isLoginSuccessful();
            assertTrue(loginSuccessful, "Login should be successful with valid credentials");
            
            // Verify we're not on the login page anymore
            assertFalse(loginPage.getCurrentUrl().contains("/login"), 
                    "URL should not contain '/login' after successful login");
            
            System.out.println("Login test completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                System.err.println("Make sure the application is running at " + baseUrl);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test case: Login with invalid credentials
     */
    @Test
    @DisplayName("Login with invalid credentials")
    public void testLoginWithInvalidCredentials() {
        try {
            System.out.println("Starting test: Login with invalid credentials");
            
            // Navigate to login page
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Print page title to help debug
            System.out.println("Page title: " + driver.getTitle());
            
            // Attempt login with invalid credentials
            loginPage.login(INVALID_USERNAME, INVALID_PASSWORD);
            
            // We should not be redirected
            boolean redirected = loginPage.waitForRedirection(2);
            assertFalse(redirected, "User should not be redirected with invalid credentials");
            
            // Verify error message is displayed
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed();
            assertTrue(errorDisplayed, "Error message should be displayed for invalid credentials");
            
            // Verify we're still on the login page
            assertTrue(loginPage.getCurrentUrl().contains("/login"), 
                    "URL should still contain '/login' after failed login attempt");
            
            System.out.println("Invalid login test completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                System.err.println("Make sure the application is running at " + baseUrl);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Test case: Login with valid username but invalid password
     */
    @Test
    @DisplayName("Login with valid username but invalid password")
    public void testLoginWithValidUsernameInvalidPassword() {
        try {
            System.out.println("Starting test: Login with valid username but invalid password");
            
            // Navigate to login page
            LoginPage loginPage = new LoginPage(driver);
            loginPage.open(baseUrl);
            
            // Attempt login with valid username but invalid password
            loginPage.login(VALID_USERNAME, INVALID_PASSWORD);
            
            // We should not be redirected
            boolean redirected = loginPage.waitForRedirection(2);
            assertFalse(redirected, "User should not be redirected with invalid password");
            
            // Verify error message is displayed
            boolean errorDisplayed = loginPage.isErrorMessageDisplayed();
            assertTrue(errorDisplayed, "Error message should be displayed for invalid password");
            
            System.out.println("Invalid password test completed successfully");
            
        } catch (WebDriverException e) {
            System.err.println("WebDriver exception: " + e.getMessage());
            if (e.getMessage().contains("ERR_CONNECTION_REFUSED")) {
                System.err.println("Make sure the application is running at " + baseUrl);
            }
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            throw e;
        }
    }
} 