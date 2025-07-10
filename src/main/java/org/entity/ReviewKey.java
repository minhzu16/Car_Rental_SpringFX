package org.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ReviewKey implements Serializable {

    @NotNull
    @Column(name = "CustomerID")
    private Integer customerId;

    @NotNull
    @Column(name = "CarID")
    private Integer carId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewKey reviewKey = (ReviewKey) o;
        return Objects.equals(customerId, reviewKey.customerId) &&
               Objects.equals(carId, reviewKey.carId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, carId);
    }
}
