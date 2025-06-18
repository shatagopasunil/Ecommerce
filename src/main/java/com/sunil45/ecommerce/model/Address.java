package com.sunil45.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5, message = "Street name must be at least 5 characters")
    @Column(length = 100)
    private String street;

    @NotBlank
    @Size(min = 5, message = "Building name must be at least 5 characters")
    @Column(length = 100)
    private String buildingName;

    @NotBlank
    @Size(min = 4, message = "City name must be at least 4 characters")
    @Column(length = 50)
    private String city;

    @NotBlank
    @Size(min = 2, message = "State name must be at least 2 characters")
    @Column(length = 50)
    private String state;

    @NotBlank
    @Size(min = 2, message = "Country name must be at least 2 characters")
    @Column(length = 50)
    private String country;

    @NotBlank
    @Pattern(regexp = "\\d{5,}", message = "Pin code must be numeric and at least 5 digits")
    @Column(length = 10)
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    public Address(String street, String buildingName, String city, String state, String country, String pincode) {
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pincode;
    }
}
