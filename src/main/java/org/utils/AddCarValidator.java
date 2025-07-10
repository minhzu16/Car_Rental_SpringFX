package org.utils;

import org.entity.Car;
import org.entity.CarProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator for car data when adding or updating cars
 */
public class AddCarValidator {

    // Danh sách các trạng thái hợp lệ
    private static final List<String> VALID_STATUSES = Arrays.asList("Available", "Rented", "Reserved", "Maintenance");
    
    // Năm mô hình tối thiểu
    private static final int MIN_MODEL_YEAR = 1900;
    
    // Số chỗ ngồi tối thiểu
    private static final int MIN_CAPACITY = 2;
    
    // Giá thuê tối thiểu
    private static final double MIN_RENT_PRICE = 10.0;
    
    // Regex cho URL hình ảnh hợp lệ (bắt đầu bằng /images/ hoặc http:// hoặc https://)
    private static final Pattern IMAGE_URL_PATTERN = Pattern.compile("^(/images/|http://|https://).*");

    /**
     * Validate car name
     * @param carName Car name to validate
     * @return Error message or null if valid
     */
    public static String validateCarName(String carName) {
        if (carName == null || carName.trim().isEmpty()) {
            return "Car Name is required";
        }
        return null;
    }
    
    /**
     * Validate car producer
     * @param producer Car producer to validate
     * @return Error message or null if valid
     */
    public static String validateProducer(CarProducer producer) {
        if (producer == null || producer.getProducerID() == null) {
            return "Producer is required";
        }
        return null;
    }
    
    /**
     * Validate model year
     * @param modelYear Model year to validate
     * @return Error message or null if valid
     */
    public static String validateModelYear(Integer modelYear) {
        if (modelYear == null) {
            return "Model Year is required";
        }
        if (modelYear < MIN_MODEL_YEAR) {
            return "Model Year must be at least " + MIN_MODEL_YEAR;
        }
        return null;
    }
    
    /**
     * Validate color
     * @param color Color to validate
     * @return Error message or null if valid
     */
    public static String validateColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            return "Color is required";
        }
        return null;
    }
    
    /**
     * Validate capacity
     * @param capacity Capacity to validate
     * @return Error message or null if valid
     */
    public static String validateCapacity(Integer capacity) {
        if (capacity == null) {
            return "Capacity is required";
        }
        if (capacity < MIN_CAPACITY) {
            return "Số chỗ ngồi phải từ " + MIN_CAPACITY + " trở lên";
        }
        return null;
    }
    
    /**
     * Validate rent price
     * @param rentPrice Rent price to validate
     * @return Error message or null if valid
     */
    public static String validateRentPrice(Double rentPrice) {
        if (rentPrice == null) {
            return "Rent Price is required";
        }
        if (rentPrice < MIN_RENT_PRICE) {
            return "Giá thuê phải từ " + MIN_RENT_PRICE + "$ trở lên";
        }
        return null;
    }
    
    /**
     * Validate status
     * @param status Status to validate
     * @return Error message or null if valid
     */
    public static String validateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Status is required";
        }
        if (!VALID_STATUSES.contains(status)) {
            return "Trạng thái không hợp lệ";
        }
        return null;
    }
    
    /**
     * Validate image URL
     * @param imageUrl Image URL to validate
     * @return Error message or null if valid
     */
    public static String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return "Image URL is required";
        }
        if (!IMAGE_URL_PATTERN.matcher(imageUrl).matches()) {
            return "URL hình ảnh không hợp lệ";
        }
        return null;
    }
    
    /**
     * Validate all car fields
     * @param car Car to validate
     * @return List of error messages or empty list if all valid
     */
    public static List<String> validateCar(Car car) {
        List<String> errors = new ArrayList<>();
        
        String carNameError = validateCarName(car.getCarName());
        if (carNameError != null) {
            errors.add(carNameError);
        }
        
        String producerError = validateProducer(car.getProducer());
        if (producerError != null) {
            errors.add(producerError);
        }
        
        String modelYearError = validateModelYear(car.getCarModelYear());
        if (modelYearError != null) {
            errors.add(modelYearError);
        }
        
        String colorError = validateColor(car.getColor());
        if (colorError != null) {
            errors.add(colorError);
        }
        
        String capacityError = validateCapacity(car.getCapacity());
        if (capacityError != null) {
            errors.add(capacityError);
        }
        
        String rentPriceError = validateRentPrice(car.getRentPrice());
        if (rentPriceError != null) {
            errors.add(rentPriceError);
        }
        
        String statusError = validateStatus(car.getStatus());
        if (statusError != null) {
            errors.add(statusError);
        }
        
        String imageUrlError = validateImageUrl(car.getImageUrl());
        if (imageUrlError != null) {
            errors.add(imageUrlError);
        }
        
        return errors;
    }
} 