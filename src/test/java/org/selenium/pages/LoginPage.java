package org.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the login page
 */
public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private String currentUrl;
    
    // Login form elements
    @FindBy(css = "#username, [name='username'], input[type='text']")
    private WebElement usernameInput;
    
    @FindBy(css = "#password, [name='password'], input[type='password']")
    private WebElement passwordInput;
    
    @FindBy(css = "button[type='submit'], input[type='submit'], .btn-login, button:contains('Login'), input[value='Login']")
    private WebElement loginButton;
    
    // Error messages
    @FindBy(css = ".alert-danger, .error-message, .text-danger")
    private WebElement errorMessage;
    
    // Success indicators
    @FindBy(css = ".navbar-brand, header .logo, #main-nav")
    private WebElement navbarBrand;
    
    /**
     * Constructor initializes the page objects
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigate to the login page
     * @param baseUrl Base URL of the application
     * @return This LoginPage for method chaining
     */
    public LoginPage open(String baseUrl) {
        currentUrl = baseUrl + "/login";
        driver.get(currentUrl);
        System.out.println("Navigated to login page: " + currentUrl);
        
        // Wait for the page to load
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("form, .login-form, .form-signin")));
            System.out.println("Login form loaded successfully");
            
            // Wait after page load
            sleep(500);
            
        } catch (TimeoutException e) {
            System.err.println("Login form not found or not loaded in time");
            System.err.println("Current URL: " + driver.getCurrentUrl());
            System.err.println("Page source: " + driver.getPageSource().substring(0, 500) + "...");
        }
        
        return this;
    }
    
    /**
     * Perform login with the given credentials
     * @param username Username
     * @param password Password
     * @return This LoginPage for method chaining
     */
    public LoginPage login(String username, String password) {
        try {
            // Xóa tất cả thông báo đang hiển thị trước khi nhấp vào nút login
            try {
                String removeNotificationsScript = 
                    "const notifications = document.querySelectorAll('.notification');" +
                    "notifications.forEach(notification => {" +
                    "  if (notification.parentNode) {" +
                    "    notification.parentNode.removeChild(notification);" +
                    "  }" +
                    "});";
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(removeNotificationsScript);
                System.out.println("Removed any existing notifications that might block the login button");
                
                // Đợi một chút sau khi xóa thông báo
                sleep(500);
            } catch (Exception e) {
                System.err.println("Failed to remove notifications: " + e.getMessage());
            }
            
            // Take screenshot before login
            takeScreenshot("before_login");
            
            // Find form elements
            WebElement userField = findElementSafely(By.cssSelector("#username, [name='username'], input[type='text']"), "username field");
            WebElement passField = findElementSafely(By.cssSelector("#password, [name='password'], input[type='password']"), "password field");
            WebElement loginBtn = findElementSafely(By.cssSelector("button[type='submit'], input[type='submit'], .btn-login"), "login button");
            
            // Enter credentials
            userField.clear();
            userField.sendKeys(username);
            System.out.println("Entered username: " + username);
            
            passField.clear();
            passField.sendKeys(password);
            System.out.println("Entered password: " + password.replaceAll(".", "*"));
            
            // Click login button
            try {
                // Thử nhấp vào nút bằng cách thông thường
                loginBtn.click();
                System.out.println("Clicked login button");
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                System.err.println("Login button click was intercepted, trying JavaScript click instead: " + e.getMessage());
                
                // Nếu bị chặn, sử dụng JavaScript để nhấp vào nút
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
                System.out.println("Clicked login button using JavaScript");
            }
            
            // Wait after clicking login
            sleep(500);
            
            // Take screenshot after login
            takeScreenshot("after_login");
            
            return this;
            
        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Perform login and navigate to admin page if successful
     * @param username Username
     * @param password Password
     * @return AdminPage if login successful, null otherwise
     */
    public AdminPage loginAsAdmin(String username, String password) {
        login(username, password);
        
        // Wait for redirection away from login page
        if (waitForRedirection(5)) {
            System.out.println("Logged in successfully, now navigating to admin page");
            
            // Always navigate to admin page regardless of current URL
            String baseUrl = driver.getCurrentUrl().split("/")[0] + "//" + driver.getCurrentUrl().split("/")[2];
            String adminUrl = baseUrl + "/admin";
            
            System.out.println("Navigating to admin page: " + adminUrl);
            driver.get(adminUrl);
            
            // Wait after navigation
            sleep(500);
            
            // Wait for admin page to load
            try {
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/admin"),
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".admin-panel, .dashboard, .admin-content"))
                ));
                System.out.println("Successfully navigated to admin page: " + driver.getCurrentUrl());
                
                // Wait after admin page loads
                sleep(500);
                
                return new AdminPage(driver);
            } catch (TimeoutException e) {
                System.err.println("Failed to load admin page: " + e.getMessage());
                System.err.println("Current URL: " + driver.getCurrentUrl());
                
                System.out.println("Trying direct navigation to admin page again");
                driver.get(adminUrl);
                
                // Wait after navigation
                sleep(500);
                
                return new AdminPage(driver);
            }
        }
        
        System.err.println("Failed to login as admin");
        return null;
    }
    
    /**
     * Wait for redirection after login
     * @param timeoutSeconds Timeout in seconds
     * @return true if redirected, false if still on login page
     */
    public boolean waitForRedirection(int timeoutSeconds) {
        try {
            System.out.println("Waiting for redirection after login...");
            WebDriverWait redirectWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
            
            redirectWait.until(driver -> {
                String url = driver.getCurrentUrl();
                boolean redirected = !url.contains("/login");
                if (redirected) {
                    System.out.println("Redirected to: " + url);
                }
                return redirected;
            });
            
            // Wait after redirection
            sleep(500);
            
            return true;
        } catch (TimeoutException e) {
            System.out.println("No redirection occurred within " + timeoutSeconds + " seconds");
            return false;
        }
    }
    
    /**
     * Check if login was successful by looking for navigation elements
     * @return true if login was successful, false otherwise
     */
    public boolean isLoginSuccessful() {
        try {
            // Wait for navigation elements that indicate successful login
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".navbar-brand")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("header .logo")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#main-nav")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".user-profile")),
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".logout-btn"))
            ));
            System.out.println("Login successful - navigation elements found");
            return true;
        } catch (TimeoutException e) {
            System.out.println("Login appears to have failed - navigation elements not found");
            return false;
        }
    }
    
    /**
     * Check if an error message is displayed
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-danger, .error-message, .text-danger")
            ));
            System.out.println("Error message displayed: " + error.getText());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    /**
     * Get the text of the error message
     * @return Error message text or empty string if no error message
     */
    public String getErrorMessage() {
        try {
            WebElement error = driver.findElement(By.cssSelector(".alert-danger, .error-message, .text-danger"));
            return error.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * Get the current URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Find an element safely with wait
     * @param locator Element locator
     * @param elementName Name of the element for logging
     * @return WebElement if found
     */
    private WebElement findElementSafely(By locator, String elementName) {
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            System.out.println("Found " + elementName + ": " + element.getTagName());
            return element;
        } catch (Exception e) {
            System.err.println("Failed to find " + elementName + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Take a screenshot for debugging
     * @param name Screenshot name
     */
    private void takeScreenshot(String name) {
        try {
            // This is just a placeholder - implement actual screenshot logic if needed
            System.out.println("Taking screenshot: " + name);
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
    
    /**
     * Sleep for the specified milliseconds
     * @param millis Milliseconds to sleep
     */
    private void sleep(long millis) {
        try {
            System.out.println("Waiting for " + millis/1000.0 + " seconds...");
            Thread.sleep(millis);
            System.out.println("Wait completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 