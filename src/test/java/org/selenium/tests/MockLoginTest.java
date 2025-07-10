package org.selenium.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mock login test that uses a local HTML file instead of requiring the application to run
 */
public class MockLoginTest {
    
    protected WebDriver driver;
    protected final boolean HEADLESS_MODE = true;
    private File loginHtmlFile;
    
    @BeforeEach
    public void setUp() {
        // Create a simple HTML file for testing
        try {
            loginHtmlFile = createLoginHtml();
        } catch (Exception e) {
            System.err.println("Failed to create login HTML file: " + e.getMessage());
        }
        
        // Setup WebDriver
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        if (HEADLESS_MODE) {
            options.addArguments("--headless");
        }
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }
    
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        
        // Delete the temporary HTML file
        if (loginHtmlFile != null && loginHtmlFile.exists()) {
            loginHtmlFile.delete();
        }
    }
    
    private File createLoginHtml() throws Exception {
        File tempFile = File.createTempFile("login_mock", ".html");
        java.io.FileWriter writer = new java.io.FileWriter(tempFile);
        
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Mock Login Page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Login</h1>\n" +
                "    <form id=\"login-form\">\n" +
                "        <div>\n" +
                "            <label for=\"username\">Username:</label>\n" +
                "            <input type=\"text\" id=\"username\" name=\"username\">\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <label for=\"password\">Password:</label>\n" +
                "            <input type=\"password\" id=\"password\" name=\"password\">\n" +
                "        </div>\n" +
                "        <button type=\"button\" id=\"login-button\" onclick=\"validateForm()\">Login</button>\n" +
                "        <div id=\"error-message\" style=\"color: red; display: none;\">Invalid username or password</div>\n" +
                "    </form>\n" +
                "    <script>\n" +
                "        function validateForm() {\n" +
                "            var username = document.getElementById('username').value;\n" +
                "            var password = document.getElementById('password').value;\n" +
                "            \n" +
                "            if (username === 'admin' && password === 'password') {\n" +
                "                document.getElementById('error-message').style.display = 'none';\n" +
                "                document.getElementById('success-message').style.display = 'block';\n" +
                "            } else {\n" +
                "                document.getElementById('error-message').style.display = 'block';\n" +
                "                document.getElementById('success-message').style.display = 'none';\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "    <div id=\"success-message\" style=\"color: green; display: none;\">Login successful!</div>\n" +
                "</body>\n" +
                "</html>";
        
        writer.write(html);
        writer.close();
        
        return tempFile;
    }
    
    @Test
    @DisplayName("Login with valid credentials should succeed")
    public void testValidLogin() {
        // Open the login page
        driver.get(loginHtmlFile.toURI().toString());
        
        // Enter valid credentials
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("password");
        
        // Click login button
        driver.findElement(By.id("login-button")).click();
        
        // Verify success message appears
        WebElement successMessage = driver.findElement(By.id("success-message"));
        assertTrue(successMessage.isDisplayed());
        assertEquals("Login successful!", successMessage.getText());
    }
    
    @Test
    @DisplayName("Login with invalid credentials should show error")
    public void testInvalidLogin() {
        // Open the login page
        driver.get(loginHtmlFile.toURI().toString());
        
        // Enter invalid credentials
        driver.findElement(By.id("username")).sendKeys("wrong");
        driver.findElement(By.id("password")).sendKeys("wrong");
        
        // Click login button
        driver.findElement(By.id("login-button")).click();
        
        // Verify error message appears
        WebElement errorMessage = driver.findElement(By.id("error-message"));
        assertTrue(errorMessage.isDisplayed());
        assertEquals("Invalid username or password", errorMessage.getText());
    }
} 