package org.selenium.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.time.Duration;
import java.util.logging.Level;

/**
 * Base test class with common setup and teardown methods
 */
public class BaseTest {
    protected WebDriver driver;
    protected String baseUrl = "http://localhost:8081";
    
    @BeforeEach
    public void setUp() {
        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        
        driver = new ChromeDriver(options);
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    /**
     * Handle error by pausing for 2 seconds and then exiting the program
     * @param errorMessage Error message to display
     */
    protected void handleErrorAndExit(String errorMessage) {
        System.err.println("ERROR: " + errorMessage);
        
        try {
            // Pause for 2 seconds
            System.err.println("Pausing for 2 seconds before exit...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Exit the program with error code 1
        System.err.println("Exiting program due to error");
        System.exit(1);
    }
} 