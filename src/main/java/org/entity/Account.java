package org.entity; 

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "Account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AccountID")
    private Integer accountID;

    @NotBlank
    @Column(name = "AccountName", nullable = false, unique = true)
    private String accountName;

    @NotBlank
    @Column(name = "Password", nullable = false)
    private String password;

    @NotBlank
    @Column(name = "Role", nullable = false)
    private String role;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Customer customer;
}
