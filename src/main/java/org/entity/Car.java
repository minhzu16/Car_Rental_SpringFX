package org.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CarID")
    private Integer carID;

    @NotBlank
    @Column(name = "CarName", nullable = false)
    private String carName;

    @NotNull
    @Min(1900)
    @Column(name = "CarModelYear", nullable = false)
    private Integer carModelYear;

    @NotBlank
    @Column(nullable = false)
    private String color;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Integer capacity;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "ImportDate", nullable = false)
    private LocalDate importDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ProducerID", nullable = false)
    private CarProducer producer;

    @NotNull
    @Positive
    @Column(name = "RentPrice", nullable = false)
    private Double rentPrice;

    @NotBlank
    @Column(nullable = false)
    private String status;

    @NotBlank
    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarRental> rentals;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

}
