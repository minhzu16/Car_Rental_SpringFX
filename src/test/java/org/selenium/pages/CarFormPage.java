package org.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.selenium.constants.CarFormConstants;

import java.time.Duration;

/**
 * Page object for the Car Form page
 */
public class CarFormPage {
    private WebDriver driver;
    private WebDriverWait wait;
    
    // Form fields
    @FindBy(id = CarFormConstants.CAPACITY_FIELD_ID)
    private WebElement capacityField;
    
    @FindBy(id = CarFormConstants.RENT_PRICE_FIELD_ID)
    private WebElement rentPriceField;
    
    @FindBy(id = CarFormConstants.STATUS_FIELD_ID)
    private WebElement statusField;
    
    @FindBy(id = CarFormConstants.NAME_FIELD_ID)
    private WebElement nameField;
    
    @FindBy(id = "licensePlate")
    private WebElement licensePlateField;
    
    @FindBy(id = CarFormConstants.COLOR_FIELD_ID)
    private WebElement colorField;
    
    @FindBy(id = CarFormConstants.PRODUCER_FIELD_ID)
    private WebElement producerField;
    
    @FindBy(id = "carModelYear")
    private WebElement modelYearField;
    
    @FindBy(id = "imageUrl")
    private WebElement imageUrlField;
    
    @FindBy(id = CarFormConstants.DESCRIPTION_FIELD_ID)
    private WebElement descriptionField;
    
    // Buttons
    @FindBy(xpath = CarFormConstants.SUBMIT_BUTTON_XPATH)
    private WebElement submitButton;
    
    /**
     * Constructor initializes the page objects
     * @param driver WebDriver instance
     */
    public CarFormPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(driver, this);
        
        // Print current URL for debugging
        System.out.println("CarFormPage constructor - Current URL: " + driver.getCurrentUrl());
        
        // Wait for the form to be present
        try {
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.id("carName")),
                ExpectedConditions.presenceOfElementLocated(By.id("capacity")),
                ExpectedConditions.presenceOfElementLocated(By.id("rentPrice"))
            ));
            System.out.println("Car form loaded successfully");
            
            // Wait after form loads
            sleep(500);
            
        } catch (Exception e) {
            System.err.println("Failed to load car form: " + e.getMessage());
            System.err.println("Current URL: " + driver.getCurrentUrl());
        }
    }
    
    /**
     * Fill the car form with default values for all required fields
     */
    public void fillDefaultValues() {
        try {
            // Điền tất cả các trường bắt buộc
            setName("Test Car");
            setLicensePlate("ABC-123");
            setColor("Red");
            setModelYear("2023");
            setImageUrl("/images/car-1.jpg");
            
            // Chọn producer từ dropdown
            selectProducer();
            
            // Điền mô tả
            setDescription("Test car description for automated testing. This car is in excellent condition with all features working properly.");
            
            System.out.println("Filled form with default values for all required fields");
            
            // Wait after filling default values
            sleep(500);
            
        } catch (Exception e) {
            System.err.println("Failed to fill default values: " + e.getMessage());
        }
    }
    
    /**
     * Set the name field value
     * @param value The value to set
     */
    public void setName(String value) {
        setFieldValue(nameField, value, "name");
    }
    
    /**
     * Set the license plate field value
     * @param value The value to set
     */
    public void setLicensePlate(String value) {
        setFieldValue(licensePlateField, value, "license plate");
    }
    
    /**
     * Set the capacity field value
     * @param value The value to set
     */
    public void setCapacity(String value) {
        setFieldValue(capacityField, value, "capacity");
    }
    
    /**
     * Set the rent price field value
     * @param value The value to set
     */
    public void setRentPrice(String value) {
        setFieldValue(rentPriceField, value, "rent price");
    }
    
    /**
     * Set the status field value
     * @param value The value to set
     */
    public void setStatus(String value) {
        try {
            Select statusSelect = new Select(statusField);
            
            // Nếu là giá trị XYZ (không hợp lệ), sử dụng JavaScript để thiết lập giá trị
            if ("XYZ".equals(value)) {
                System.out.println("Setting invalid status value: " + value);
                
                // Sử dụng JavaScript để thêm tùy chọn mới vào dropdown
                String script = "var select = arguments[0];" +
                                "var option = document.createElement('option');" +
                                "option.text = arguments[1];" +
                                "option.value = arguments[1];" +
                                "select.add(option);" +
                                "select.value = arguments[1];";
                
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, statusField, value);
                
                System.out.println("Set status to: " + value + " using JavaScript");
            } else {
                // Nếu là giá trị hợp lệ, sử dụng Select API
                statusSelect.selectByVisibleText(value);
                System.out.println("Set status to: " + value);
            }
        } catch (Exception e) {
            System.err.println("Failed to set status: " + e.getMessage());
            
            try {
                // Thử phương pháp khác: sử dụng JavaScript để thiết lập giá trị trực tiếp
                String script = "arguments[0].value = arguments[1];";
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, statusField, value);
                System.out.println("Set status to: " + value + " using JavaScript fallback");
            } catch (Exception ex) {
                System.err.println("Failed to set status using JavaScript: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Set the color field value
     * @param value The value to set
     */
    public void setColor(String value) {
        setFieldValue(colorField, value, "color");
    }
    
    /**
     * Select a producer from the dropdown
     */
    public void selectProducer() {
        try {
            Select producerSelect = new Select(producerField);
            if (producerSelect.getOptions().size() > 1) {
                producerSelect.selectByIndex(1); // Select the first real option (index 0 is often "Select Producer")
                System.out.println("Selected producer: " + producerSelect.getFirstSelectedOption().getText());
            } else {
                System.err.println("No producer options available");
            }
        } catch (Exception e) {
            System.err.println("Failed to select producer: " + e.getMessage());
        }
    }
    
    /**
     * Set the model year field value
     * @param value The value to set
     */
    public void setModelYear(String value) {
        setFieldValue(modelYearField, value, "model year");
    }
    
    /**
     * Set the image URL field value
     * @param value The value to set
     */
    public void setImageUrl(String value) {
        setFieldValue(imageUrlField, value, "image URL");
    }
    
    /**
     * Set the description field value
     * @param value The value to set
     */
    public void setDescription(String value) {
        if (descriptionField != null) {
            setFieldValue(descriptionField, value, "description");
        }
    }
    
    /**
     * Helper method to set a field value with proper error handling
     * @param field The WebElement field
     * @param value The value to set
     * @param fieldName The name of the field for logging
     */
    private void setFieldValue(WebElement field, String value, String fieldName) {
        try {
            wait.until(ExpectedConditions.visibilityOf(field));
            field.clear();
            field.sendKeys(value);
            System.out.println("Set " + fieldName + " to: " + value);
        } catch (Exception e) {
            System.err.println("Failed to set " + fieldName + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Submit the form
     */
    public void submitForm() {
        try {
            // Xóa tất cả thông báo đang hiển thị trước khi nhấp vào nút submit
            try {
                String removeNotificationsScript = 
                    "const notifications = document.querySelectorAll('.notification');" +
                    "notifications.forEach(notification => {" +
                    "  if (notification.parentNode) {" +
                    "    notification.parentNode.removeChild(notification);" +
                    "  }" +
                    "});";
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(removeNotificationsScript);
                System.out.println("Removed any existing notifications that might block the submit button");
                
                // Đợi một chút sau khi xóa thông báo
                sleep(500);
            } catch (Exception e) {
                System.err.println("Failed to remove notifications: " + e.getMessage());
            }
            
            // Scroll to the submit button to ensure it's visible
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", 
                submitButton
            );
            
            // Wait a moment for the scroll to complete
            sleep(500);
            
            try {
                // Thử nhấp vào nút bằng cách thông thường
                wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        submitButton.click();
                System.out.println("Clicked submit button");
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                System.err.println("Button click was intercepted, trying JavaScript click instead: " + e.getMessage());
                
                // Nếu bị chặn, sử dụng JavaScript để nhấp vào nút
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);
                System.out.println("Clicked submit button using JavaScript");
            }
            
            // Wait after form submission
            sleep(500);
        } catch (Exception e) {
            System.err.println("Failed to submit form: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Fill all fields and submit the form
     */
    public void fillAndSubmitForm(String capacity, String rentPrice, String status) {
        try {
            System.out.println("Starting to fill form with test data");
            
            // Điền các trường bắt buộc trước
            setName("Test Car");
            setLicensePlate("ABC-123");
            setColor("Red");
            setModelYear("2023");
            setImageUrl("/images/car-1.jpg");
            selectProducer();
            setDescription("Test car description for automated testing.");
            
            // Đợi một chút để đảm bảo các trường đã được điền
            sleep(500);
            
            // Điền các giá trị test đặc biệt
            System.out.println("Setting test data: capacity=" + capacity + ", rentPrice=" + rentPrice + ", status=" + status);
            
            // Xóa và điền lại các trường test
            if (capacityField != null) {
                capacityField.clear();
                capacityField.sendKeys(capacity);
                System.out.println("Set capacity to: " + capacity);
            } else {
                System.err.println("Capacity field not found");
            }
            
            if (rentPriceField != null) {
                rentPriceField.clear();
                rentPriceField.sendKeys(rentPrice);
                System.out.println("Set rent price to: " + rentPrice);
            } else {
                System.err.println("Rent price field not found");
            }
            
            try {
                Select statusSelect = new Select(statusField);
                statusSelect.selectByVisibleText(status);
                System.out.println("Set status to: " + status);
            } catch (Exception e) {
                System.err.println("Failed to set status via Select: " + e.getMessage());
                
                if (statusField != null) {
                    statusField.clear();
                    statusField.sendKeys(status);
                    System.out.println("Set status to: " + status + " via direct input");
                } else {
                    System.err.println("Status field not found");
                }
            }
            
            // Đợi một chút để đảm bảo các trường test đã được điền
            sleep(500);
            
            // Chụp ảnh màn hình trước khi submit (nếu cần)
            System.out.println("Form filled with test data, ready to submit");
            
            // Submit form
            submitForm();
            
        } catch (Exception e) {
            System.err.println("Failed to fill and submit form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if capacity error message is displayed
     * @return true if error message is displayed, false otherwise
     */
    public boolean isCapacityErrorDisplayed() {
        return isErrorMessageDisplayed(CarFormConstants.CAPACITY_ERROR_MESSAGE);
    }
    
    /**
     * Check if rent price error message is displayed
     * @return true if error message is displayed, false otherwise
     */
    public boolean isRentPriceErrorDisplayed() {
        return isErrorMessageDisplayed(CarFormConstants.RENT_PRICE_ERROR_MESSAGE);
    }
    
    /**
     * Check if status error message is displayed
     * @return true if error message is displayed, false otherwise
     */
    public boolean isStatusErrorDisplayed() {
        return isErrorMessageDisplayed(CarFormConstants.STATUS_ERROR_MESSAGE);
    }
    
    /**
     * Check if image URL error message is displayed
     * @return true if error message is displayed, false otherwise
     */
    public boolean isImageURLErrorDisplayed() {
        return isErrorMessageDisplayed(CarFormConstants.IMAGE_URL_ERROR_MESSAGE);
    }
    
    /**
     * Check if error message is displayed
     * @param expectedErrorMessage The expected error message
     * @return true if error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed(String expectedErrorMessage) {
        try {
            // Tìm kiếm thông báo lỗi trong phần tử thông báo hoặc phản hồi xác thực
            By errorInValidationFeedback = By.xpath("//div[contains(text(), '" + expectedErrorMessage + "') or " +
                                                  "contains(@class, 'error') and contains(text(), '" + expectedErrorMessage + "')]");
            
            By errorInNotification = By.xpath("//div[contains(@class, 'notification') and contains(., '" + expectedErrorMessage + "')]");
            
            // Chờ đợi một trong hai phần tử xuất hiện
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(errorInValidationFeedback),
                ExpectedConditions.visibilityOfElementLocated(errorInNotification)
            ));
            
            // Kiểm tra xem có thông báo lỗi trong phản hồi xác thực không
            boolean hasErrorInValidation = !driver.findElements(errorInValidationFeedback).isEmpty();
            
            // Kiểm tra xem có thông báo lỗi trong thông báo không
            boolean hasErrorInNotification = !driver.findElements(errorInNotification).isEmpty();
            
            // Nếu tìm thấy lỗi ở bất kỳ vị trí nào, hiển thị thông tin và trả về true
            if (hasErrorInValidation || hasErrorInNotification) {
                System.out.println("Error message found: " + expectedErrorMessage);
                return true;
            }
            
            return false;
            
        } catch (TimeoutException e) {
            System.out.println("Error message not found: " + expectedErrorMessage);
            return false;
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