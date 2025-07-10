package org.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private Integer customerID;

    @NotBlank
    @Column(name = "CustomerName", nullable = false)
    private String customerName;

    @NotBlank
    @Column(nullable = false)
    private String mobile;

    @NotNull
    @Column(nullable = false)
    private LocalDate birthday;

    @NotBlank
    @Column(name = "IdentityCard", nullable = false, unique = true)
    private String identityCard;

    @NotBlank
    @Column(name = "LicenceNumber", nullable = false, unique = true)
    private String licenceNumber;

    @NotNull
    @Column(name = "LicenceDate", nullable = false)
    private LocalDate licenceDate;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AccountID", nullable = false, unique = true)
    private Account account;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarRental> rentals;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}
