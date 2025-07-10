package org.selenium.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.selenium.constants.CarFormConstants;

import java.io.File;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Mock car registration test that uses local HTML files instead of requiring the application to run
 */
public class MockCarRegistrationTest {
    
    protected WebDriver driver;
    protected final boolean HEADLESS_MODE = true;
    private File carFormHtmlFile;
    
    @BeforeEach
    public void setUp() {
        // Create the test HTML file
        try {
            carFormHtmlFile = createCarFormHtml();
        } catch (Exception e) {
            System.err.println("Failed to create car form HTML file: " + e.getMessage());
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
        if (carFormHtmlFile != null && carFormHtmlFile.exists()) {
            carFormHtmlFile.delete();
        }
    }
    
    private File createCarFormHtml() throws Exception {
        File tempFile = File.createTempFile("car_form_mock", ".html");
        java.io.FileWriter writer = new java.io.FileWriter(tempFile);
        
        String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Mock Car Registration Form</title>\n" +
                "    <script>\n" +
                "        function validateForm() {\n" +
                "            var capacity = document.getElementById('" + CarFormConstants.CAPACITY_FIELD_ID + "').value;\n" +
                "            var rentPrice = document.getElementById('" + CarFormConstants.RENT_PRICE_FIELD_ID + "').value;\n" +
                "            var status = document.getElementById('" + CarFormConstants.STATUS_FIELD_ID + "').value;\n" +
                "            var hasError = false;\n" +
                "            \n" +
                "            // Reset error messages\n" +
                "            document.getElementById('capacity-error').style.display = 'none';\n" +
                "            document.getElementById('rentPrice-error').style.display = 'none';\n" +
                "            document.getElementById('status-error').style.display = 'none';\n" +
                "            \n" +
                "            // Validate capacity (minimum 2)\n" +
                "            if (capacity < 2) {\n" +
                "                document.getElementById('capacity-error').style.display = 'block';\n" +
                "                hasError = true;\n" +
                "            }\n" +
                "            \n" +
                "            // Validate rent price (minimum 10)\n" +
                "            if (rentPrice < 10) {\n" +
                "                document.getElementById('rentPrice-error').style.display = 'block';\n" +
                "                hasError = true;\n" +
                "            }\n" +
                "            \n" +
                "            // Validate status (must be AVAILABLE or UNAVAILABLE)\n" +
                "            if (status !== 'AVAILABLE' && status !== 'UNAVAILABLE') {\n" +
                "                document.getElementById('status-error').style.display = 'block';\n" +
                "                hasError = true;\n" +
                "            }\n" +
                "            \n" +
                "            return !hasError;\n" +
                "        }\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Add New Car</h1>\n" +
                "    <form id=\"car-form\" onsubmit=\"return validateForm()\">\n" +
                "        <div>\n" +
                "            <label for=\"" + CarFormConstants.NAME_FIELD_ID + "\">Car Name:</label>\n" +
                "            <input type=\"text\" id=\"" + CarFormConstants.NAME_FIELD_ID + "\" name=\"" + CarFormConstants.NAME_FIELD_ID + "\" required>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <label for=\"" + CarFormConstants.LICENSE_PLATE_FIELD_ID + "\">License Plate:</label>\n" +
                "            <input type=\"text\" id=\"" + CarFormConstants.LICENSE_PLATE_FIELD_ID + "\" name=\"" + CarFormConstants.LICENSE_PLATE_FIELD_ID + "\" required>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <label for=\"" + CarFormConstants.CAPACITY_FIELD_ID + "\">Capacity:</label>\n" +
                "            <input type=\"number\" id=\"" + CarFormConstants.CAPACITY_FIELD_ID + "\" name=\"" + CarFormConstants.CAPACITY_FIELD_ID + "\" required>\n" +
                "            <div id=\"capacity-error\" class=\"error\" style=\"color: red; display: none;\">" + CarFormConstants.CAPACITY_ERROR_MESSAGE + "</div>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <label for=\"" + CarFormConstants.RENT_PRICE_FIELD_ID + "\">Rent Price ($):</label>\n" +
                "            <input type=\"number\" id=\"" + CarFormConstants.RENT_PRICE_FIELD_ID + "\" name=\"" + CarFormConstants.RENT_PRICE_FIELD_ID + "\" required>\n" +
                "            <div id=\"rentPrice-error\" class=\"error\" style=\"color: red; display: none;\">" + CarFormConstants.RENT_PRICE_ERROR_MESSAGE + "</div>\n" +
                "        </div>\n" +
                "        <div>\n" +
                "            <label for=\"" + CarFormConstants.STATUS_FIELD_ID + "\">Status:</label>\n" +
                "            <select id=\"" + CarFormConstants.STATUS_FIELD_ID + "\" name=\"" + CarFormConstants.STATUS_FIELD_ID + "\">\n" +
                "                <option value=\"AVAILABLE\">AVAILABLE</option>\n" +
                "                <option value=\"UNAVAILABLE\">UNAVAILABLE</option>\n" +
                "            </select>\n" +
                "            <div id=\"status-error\" class=\"error\" style=\"color: red; display: none;\">" + CarFormConstants.STATUS_ERROR_MESSAGE + "</div>\n" +
                "        </div>\n" +
                "        <button type=\"submit\" id=\"submit-button\">Add Car</button>\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>";
        
        writer.write(html);
        writer.close();
        
        return tempFile;
    }
    
    /**
     * Test case: Registration16
     * Verify that an error message is shown when capacity is set to 1 seat
     * (Minimum required is 2 seats)
     */
    @Test
    @DisplayName("Registration16: Verify Capacity with 1 seat")
    public void testCapacityMinimumValue() {
        // Open the car form page
        driver.get(carFormHtmlFile.toURI().toString());
        
        // Enter "1" in Capacity field
        WebElement capacityField = driver.findElement(By.id(CarFormConstants.CAPACITY_FIELD_ID));
        capacityField.sendKeys("1");
        
        // Fill other fields with valid values
        driver.findElement(By.id(CarFormConstants.NAME_FIELD_ID)).sendKeys("Test Car");
        driver.findElement(By.id(CarFormConstants.LICENSE_PLATE_FIELD_ID)).sendKeys("TEST123");
        driver.findElement(By.id(CarFormConstants.RENT_PRICE_FIELD_ID)).sendKeys("20");
        
        // Click Submit button
        driver.findElement(By.id("submit-button")).click();
        
        // Verify error message is displayed
        WebElement errorMessage = driver.findElement(By.id("capacity-error"));
        assertTrue(errorMessage.isDisplayed());
        assertEquals(CarFormConstants.CAPACITY_ERROR_MESSAGE, errorMessage.getText());
    }
    
    /**
     * Test case: Registration17
     * Verify that an error message is shown when rent price is set to 9$
     * (Minimum required is 10$)
     */
    @Test
    @DisplayName("Registration17: Verify Rent Price with 9$")
    public void testRentPriceMinimumValue() {
        // Open the car form page
        driver.get(carFormHtmlFile.toURI().toString());
        
        // Enter "9" in Rent Price field
        WebElement rentPriceField = driver.findElement(By.id(CarFormConstants.RENT_PRICE_FIELD_ID));
        rentPriceField.sendKeys("9");
        
        // Fill other fields with valid values
        driver.findElement(By.id(CarFormConstants.NAME_FIELD_ID)).sendKeys("Test Car");
        driver.findElement(By.id(CarFormConstants.LICENSE_PLATE_FIELD_ID)).sendKeys("TEST123");
        driver.findElement(By.id(CarFormConstants.CAPACITY_FIELD_ID)).sendKeys("4");
        
        // Click Submit button
        driver.findElement(By.id("submit-button")).click();
        
        // Verify error message is displayed
        WebElement errorMessage = driver.findElement(By.id("rentPrice-error"));
        assertTrue(errorMessage.isDisplayed());
        assertEquals(CarFormConstants.RENT_PRICE_ERROR_MESSAGE, errorMessage.getText());
    }
    
    /**
     * Test case: Registration18
     * Verify that an error message is shown when status is set to an invalid value "XYZ"
     */
    @Test
    @DisplayName("Registration18: Verify Status with invalid option (XYZ)")
    public void testInvalidStatus() {
        // Open the car form page
        driver.get(carFormHtmlFile.toURI().toString());
        
        // Fill other fields with valid values
        driver.findElement(By.id(CarFormConstants.NAME_FIELD_ID)).sendKeys("Test Car");
        driver.findElement(By.id(CarFormConstants.LICENSE_PLATE_FIELD_ID)).sendKeys("TEST123");
        driver.findElement(By.id(CarFormConstants.CAPACITY_FIELD_ID)).sendKeys("4");
        driver.findElement(By.id(CarFormConstants.RENT_PRICE_FIELD_ID)).sendKeys("20");
        
        // Change the status to a custom value using JavaScript
        ((JavascriptExecutor) driver).executeScript(
            "var select = document.getElementById('" + CarFormConstants.STATUS_FIELD_ID + "');" +
            "select.options.length = 0;" +
            "var option = document.createElement('option');" +
            "option.value = 'XYZ';" +
            "option.text = 'XYZ';" +
            "select.add(option);" +
            "select.value = 'XYZ';"
        );
        
        // Click Submit button
        driver.findElement(By.id("submit-button")).click();
        
        // Verify error message is displayed
        WebElement errorMessage = driver.findElement(By.id("status-error"));
        assertTrue(errorMessage.isDisplayed());
        assertEquals(CarFormConstants.STATUS_ERROR_MESSAGE, errorMessage.getText());
    }
} 