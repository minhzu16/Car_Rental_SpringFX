package org.enums;

/**
 * Enum representing the possible statuses of a car rental.
 */
public enum RentalStatus {
    PENDING("Rental is awaiting admin approval"),
    ACTIVE("Active rental in progress"),
    COMPLETED("Rental has been completed"),
    CANCELLED("Rental has been cancelled"),
    EARLY_RETURN("Customer has requested to return early");
    
    private final String description;
    
    RentalStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return name();
    }
} 