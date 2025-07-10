package org.selenium.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This is a demonstration test class for the three test cases shown in the image.
 * It doesn't actually run Selenium WebDriver but shows the test structure.
 * In a real implementation, you would use Selenium WebDriver to interact with the browser.
 */
public class DemoSeleniumTest {

    /**
     * Test case: Registration16
     * Verify that an error message is shown when capacity is set to 1 seat
     * (Minimum required is 2 seats)
     */
    @Test
    @DisplayName("Registration16: Verify Capacity with 1 seat")
    public void testCapacityMinimumValue() {
        // 1. Log in to the application
        System.out.println("Step 1: Log in to the application");
        // driver.get("http://localhost:8080/login");
        // Enter username and password and click Login

        // 2 & 3. Navigate to Add New Car page & Click Add New Car button
        System.out.println("Step 2-3: Navigate to Add New Car page and click Add New Car button");
        // driver.get("http://localhost:8080/admin");
        // Click on Add New Car button

        // 4. Enter "1" in Capacity field
        System.out.println("Step 4: Enter '1' in Capacity field");
        // WebElement capacityField = driver.findElement(By.id("capacity"));
        // capacityField.clear();
        // capacityField.sendKeys("1");

        // 5. Fill other fields with default values
        System.out.println("Step 5: Fill other fields with default values");
        // Fill form with default values for name, license plate, etc.

        // 6. Click Submit button
        System.out.println("Step 6: Click Submit button");
        // WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        // submitButton.click();

        // Verify error message is displayed
        System.out.println("Verifying error message: 'Minimum 2 seats'");
        // assertTrue(isErrorMessageDisplayed("Minimum 2 seats"));
    }

    /**
     * Test case: Registration17
     * Verify that an error message is shown when rent price is set to 9$
     * (Minimum required is 10$)
     */
    @Test
    @DisplayName("Registration17: Verify Rent Price with 9$")
    public void testRentPriceMinimumValue() {
        // 1. Log in to the application
        System.out.println("Step 1: Log in to the application");
        // driver.get("http://localhost:8080/login");
        // Enter username and password and click Login

        // 2 & 3. Navigate to Add New Car page & Click Add New Car button
        System.out.println("Step 2-3: Navigate to Add New Car page and click Add New Car button");
        // driver.get("http://localhost:8080/admin");
        // Click on Add New Car button

        // 4. Enter "9" in Rent Price field
        System.out.println("Step 4: Enter '9' in Rent Price field");
        // WebElement rentPriceField = driver.findElement(By.id("rentPrice"));
        // rentPriceField.clear();
        // rentPriceField.sendKeys("9");

        // 5. Fill other fields with default values
        System.out.println("Step 5: Fill other fields with default values");
        // Fill form with default values for name, license plate, etc.

        // 6. Click Submit button
        System.out.println("Step 6: Click Submit button");
        // WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        // submitButton.click();

        // Verify error message is displayed
        System.out.println("Verifying error message: 'Minimum 10 $'");
        // assertTrue(isErrorMessageDisplayed("Minimum 10 $"));
    }

    /**
     * Test case: Registration18
     * Verify that an error message is shown when status is set to an invalid value "XYZ"
     */
    @Test
    @DisplayName("Registration18: Verify Status with invalid option (XYZ)")
    public void testInvalidStatus() {
        // 1. Log in to the application
        System.out.println("Step 1: Log in to the application");
        // driver.get("http://localhost:8080/login");
        // Enter username and password and click Login

        // 2 & 3. Navigate to Add New Car page & Click Add New Car button
        System.out.println("Step 2-3: Navigate to Add New Car page and click Add New Car button");
        // driver.get("http://localhost:8080/admin");
        // Click on Add New Car button

        // 4. Enter "XYZ" in Status field
        System.out.println("Step 4: Enter 'XYZ' in Status field");
        // WebElement statusField = driver.findElement(By.id("status"));
        // statusField.clear();
        // statusField.sendKeys("XYZ");

        // 5. Fill other fields with default values
        System.out.println("Step 5: Fill other fields with default values");
        // Fill form with default values for name, license plate, etc.

        // 6. Click Submit button
        System.out.println("Step 6: Click Submit button");
        // WebElement submitButton = driver.findElement(By.xpath("//button[@type='submit']"));
        // submitButton.click();

        // Verify error message is displayed
        System.out.println("Verifying error message: 'Invalid status'");
        // assertTrue(isErrorMessageDisplayed("Invalid status"));
    }
} 