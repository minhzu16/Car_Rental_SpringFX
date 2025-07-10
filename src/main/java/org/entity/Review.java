package org.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @EmbeddedId
    private ReviewKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("customerId")
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("carId")
    @JoinColumn(name = "CarID", nullable = false)
    private Car car;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "review_star", nullable = false)
    private Integer reviewStar;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;
}
