package org.enums;

/**
 * Enum representing the possible statuses of a car.
 */
public enum CarStatus {
    AVAILABLE("Available"),
    RENTED("Rented"),
    RESERVED("Reserved"),
    MAINTENANCE("Maintenance");
    
    private final String displayValue;
    
    CarStatus(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    @Override
    public String toString() {
        return displayValue;
    }
    
    /**
     * Convert a string value to a CarStatus enum
     * @param value the string value
     * @return the corresponding CarStatus enum, or AVAILABLE if not found
     */
    public static CarStatus fromString(String value) {
        for (CarStatus status : CarStatus.values()) {
            if (status.displayValue.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return AVAILABLE; // Default value
    }
} 