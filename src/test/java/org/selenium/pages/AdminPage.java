package org.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.selenium.constants.CarFormConstants;

import java.time.Duration;

/**
 * Page object for the Admin page
 */
public class AdminPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Navigation links
    @FindBy(xpath = "//a[contains(@href, '/admin/cars')]")
    private WebElement carsLink;
    
    @FindBy(xpath = "//a[contains(@href, '/admin/rentals')]")
    private WebElement rentalsLink;
    
    @FindBy(xpath = "//a[contains(@href, '/admin/customers')]")
    private WebElement customersLink;
    
    // Add Car button
    @FindBy(xpath = CarFormConstants.ADD_NEW_CAR_BUTTON_XPATH)
    private WebElement addCarButton;
    
    /**
     * Constructor initializes the page objects
     * @param driver WebDriver instance
     */
    public AdminPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
        
        // Print current URL for debugging
        System.out.println("AdminPage constructor - Current URL: " + driver.getCurrentUrl());
        
        // Wait for admin page to load
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin"),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, '/admin/cars')]"))
            ));
            System.out.println("Successfully navigated to admin page: " + driver.getCurrentUrl());
            
            // Wait after page loads
            sleep(500);
            
        } catch (Exception e) {
            System.err.println("Failed to load admin page: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
        }
    }
    
    /**
     * Navigate to Car Management page
     */
    public void navigateToCarManagement() {
        try {
            // Try to find and click the cars link
            try {
                wait.until(ExpectedConditions.elementToBeClickable(carsLink));
                carsLink.click();
                System.out.println("Clicked cars link");
            } catch (Exception e) {
                System.err.println("Could not click cars link: " + e.getMessage());
                
                // Direct navigation as fallback
                String carsUrl = "http://localhost:8081/admin/cars";
                System.out.println("Navigating directly to cars page: " + carsUrl);
                driver.navigate().to(carsUrl);
            }
            
            // Wait for navigation to complete
            wait.until(ExpectedConditions.urlContains("/admin/cars"));
            System.out.println("Navigated to car management page: " + driver.getCurrentUrl());
            
            // Wait after navigation
            sleep(500);
            
        } catch (Exception e) {
            System.err.println("Failed to navigate to car management: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
        }
    }
    
    /**
     * Navigate to Add Car page
     * @return CarFormPage instance
     */
    public CarFormPage navigateToAddCar() {
        try {
            // Try to find and click the add car button
            try {
                wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath(CarFormConstants.ADD_NEW_CAR_BUTTON_XPATH)
                ));
                WebElement addButton = driver.findElement(By.xpath(CarFormConstants.ADD_NEW_CAR_BUTTON_XPATH));
                addButton.click();
                System.out.println("Clicked add car button");
            } catch (Exception e) {
                System.err.println("Could not click add car button: " + e.getMessage());
                
                // Direct navigation as fallback
                String addCarUrl = "http://localhost:8081/admin/cars/add";
                System.out.println("Navigating directly to add car page: " + addCarUrl);
                driver.navigate().to(addCarUrl);
            }
            
            // Wait for navigation to complete
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/admin/cars/add"),
                ExpectedConditions.urlContains("/admin/car/add")
            ));
            System.out.println("Navigated to add car page: " + driver.getCurrentUrl());
            
            // Wait after navigation
            sleep(500);
            
            return new CarFormPage(driver);
            
        } catch (Exception e) {
            System.err.println("Failed to navigate to add car page: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
            throw e;
        }
    }
    
    /**
     * Navigate to Rentals page
     */
    public void navigateToRentals() {
        wait.until(ExpectedConditions.elementToBeClickable(rentalsLink));
        rentalsLink.click();
    }
    
    /**
     * Navigate to Customers page
     */
    public void navigateToCustomers() {
        wait.until(ExpectedConditions.elementToBeClickable(customersLink));
        customersLink.click();
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