package org.selenium.constants;

/**
 * Constants for car form field IDs and selectors
 */
public class CarFormConstants {
    // Form field IDs
    public static final String NAME_FIELD_ID = "carName";
    public static final String LICENSE_PLATE_FIELD_ID = "licensePlate";
    public static final String CAPACITY_FIELD_ID = "capacity";
    public static final String RENT_PRICE_FIELD_ID = "rentPrice";
    public static final String STATUS_FIELD_ID = "status";
    public static final String COLOR_FIELD_ID = "color";
    public static final String PRODUCER_FIELD_ID = "producer";
    public static final String DESCRIPTION_FIELD_ID = "description";
    public static final String MODEL_YEAR_FIELD_ID = "carModelYear";
    public static final String IMAGE_URL_FIELD_ID = "imageUrl";
    
    // Button selectors
    public static final String SUBMIT_BUTTON_XPATH = "//button[@type='submit']"; 
    public static final String ADD_NEW_CAR_BUTTON_XPATH = "//a[contains(text(), 'Add Car') or contains(@href, '/admin/car/add') or contains(@href, '/admin/cars/add')]"; 
    
    // Error message patterns
    public static final String NAME_ERROR_MESSAGE = "Car Name is required";
    public static final String PRODUCER_ERROR_MESSAGE = "Producer is required";
    public static final String MODEL_YEAR_ERROR_MESSAGE = "Model Year must be at least 1900";
    public static final String COLOR_ERROR_MESSAGE = "Color is required";
    public static final String CAPACITY_ERROR_MESSAGE = "Số chỗ ngồi phải từ 2 trở lên";
    public static final String RENT_PRICE_ERROR_MESSAGE = "Giá thuê phải từ 10$ trở lên";
    public static final String STATUS_ERROR_MESSAGE = "Trạng thái không hợp lệ";
    public static final String IMAGE_URL_ERROR_MESSAGE = "URL hình ảnh không hợp lệ";
    
    // URLs
    public static final String ADMIN_PAGE_URL = "http://localhost:8081/admin";
    public static final String CAR_FORM_URL = "http://localhost:8081/admin/cars/add";
}