package org.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.enums.RentalStatus;

import java.time.LocalDate;

@Entity
@Table(name = "CarRental")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRental {

    @EmbeddedId
    private CarRentalKey id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("customerId")
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("carId")
    @JoinColumn(name = "CarID", nullable = false)
    private Car car;

    @NotNull
    @Column(name = "ReturnDate", nullable = false)
    private LocalDate returnDate;
    
    @Column(name = "NewReturnDate")
    private LocalDate newReturnDate;

    @NotNull
    @Positive
    @Column(name = "RentPrice", nullable = false)
    private Double rentPrice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RentalStatus status;
    
    // Convenience method to get status as string for backward compatibility
    public String getStatusString() {
        return status != null ? status.name() : null;
    }

    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (id.getPickupDate().isAfter(returnDate)) {
            throw new IllegalArgumentException("PickupDate must be before ReturnDate");
        }
    }
}
