package org.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validator for user profile fields based on defined test cases
 */
public class UserProfileValidator {
    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    
    // Date constraints
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy");
    private static final LocalDate MIN_DATE = LocalDate.of(1900, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(2025, 12, 31);

    /**
     * Validates all user profile fields at once and returns errors if any
     * 
     * @param customerName the user's full name
     * @param email the user's email address
     * @param address the user's physical address
     * @param mobile the user's phone number
     * @param identityCard the user's identity card number
     * @param licenceNumber the user's licence number
     * @param licenceDate the user's licence date
     * @param birthday the user's birthday
     * @return a map of field names to error messages (empty if all valid)
     */
    public static Map<String, String> validateUserProfile(
            String customerName, 
            String email, 
            String address, 
            String mobile, 
            String identityCard, 
            String licenceNumber, 
            String licenceDate, 
            String birthday) {
        
        Map<String, String> errors = new HashMap<>();
        
        String error = validateCustomerName(customerName);
        if (error != null) {
            errors.put("customerNameError", error);
        }
        
        error = validateEmail(email);
        if (error != null) {
            errors.put("emailError", error);
        }
        
        error = validateAddress(address);
        if (error != null) {
            errors.put("addressError", error);
        }
        
        error = validateMobile(mobile);
        if (error != null) {
            errors.put("mobileError", error);
        }
        
        error = validateIdentityCard(identityCard);
        if (error != null) {
            errors.put("identityCardError", error);
        }
        
        error = validateLicenceNumber(licenceNumber);
        if (error != null) {
            errors.put("licenceNumberError", error);
        }
        
        error = validateDate(licenceDate, "Licence Date");
        if (error != null) {
            errors.put("licenceDateError", error);
        }
        
        error = validateDate(birthday, "Birthday");
        if (error != null) {
            errors.put("birthdayError", error);
        }
        
        return errors;
    }

    /**
     * Validates customer name (2-50 characters)
     * 
     * @param name the customer name to validate
     * @return error message or null if valid
     */
    public static String validateCustomerName(String name) {
        if (name == null || name.length() < 2) {
            return "Error: Minimum 2 characters";
        }
        if (name.length() > 50) {
            return "Error: Maximum 50 characters";
        }
        return null;
    }

    /**
     * Validates email address (must be in valid format)
     * 
     * @param email the email to validate
     * @return error message or null if valid
     */
    public static String validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Error: Invalid email";
        }
        return null;
    }

    /**
     * Validates address (5-100 characters)
     * 
     * @param address the address to validate
     * @return error message or null if valid
     */
    public static String validateAddress(String address) {
        if (address == null || address.length() < 5) {
            return "Error: Minimum 5 characters";
        }
        if (address.length() > 100) {
            return "Error: Maximum 100 characters";
        }
        return null;
    }

    /**
     * Validates mobile number (must be exactly 10 digits)
     * 
     * @param mobile the mobile number to validate
     * @return error message or null if valid
     */
    public static String validateMobile(String mobile) {
        if (mobile == null || mobile.length() < 10) {
            return "Error: Minimum 10 digits";
        }
        if (mobile.length() > 10) {
            return "Error: Maximum 10 digits";
        }
        if (!PHONE_PATTERN.matcher(mobile).matches()) {
            return "Error: Phone number must contain only digits";
        }
        return null;
    }

    /**
     * Validates identity card (must be exactly 9 digits)
     * 
     * @param id the identity card number to validate
     * @return error message or null if valid
     */
    public static String validateIdentityCard(String id) {
        if (id == null || id.length() < 9) {
            return "Error: Minimum 9 digits";
        }
        if (id.length() > 9) {
            return "Error: Maximum 9 digits";
        }
        return null;
    }

    /**
     * Validates licence number (must be exactly 7 characters)
     * 
     * @param licence the licence number to validate
     * @return error message or null if valid
     */
    public static String validateLicenceNumber(String licence) {
        if (licence == null || licence.length() < 7) {
            return "Error: Minimum 7 characters";
        }
        if (licence.length() > 7) {
            return "Error: Maximum 7 characters";
        }
        return null;
    }

    /**
     * Validates date (must be between 01/01/1900 and 31/12/2025)
     * 
     * @param dateStr the date string to validate
     * @param field the name of the field being validated
     * @return error message or null if valid
     */
    public static String validateDate(String dateStr, String field) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "Error: Date cannot be empty";
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            if (date.isBefore(MIN_DATE) || date.isAfter(MAX_DATE)) {
                return "Error: Date out of range";
            }
        } catch (Exception e) {
            return "Error: Invalid date format";
        }
        return null;
    }
} 