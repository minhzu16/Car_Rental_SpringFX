package org.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CarRentalKey implements Serializable {
    @NotNull
    @Column(name = "CustomerID")
    private Integer customerId;

    @NotNull
    @Column(name = "CarID")
    private Integer carId;

    @NotNull
    @Column(name = "PickupDate")
    private LocalDate pickupDate;
    
    // Constructor for convenience
    public CarRentalKey(int customerId, int carId, LocalDate pickupDate) {
        this.customerId = customerId;
        this.carId = carId;
        this.pickupDate = pickupDate;
    }
}
