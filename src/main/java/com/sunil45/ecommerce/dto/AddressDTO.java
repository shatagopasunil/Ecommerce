package com.sunil45.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressId;

    @NotBlank
    @Size(min = 5, max = 100, message = "Street name must be between 5 and 100 characters")
    private String street;

    @NotBlank
    @Size(min = 5, max = 100, message = "Building name must be between 5 and 100 characters")
    private String buildingName;

    @NotBlank
    @Size(min = 4, max = 50, message = "City name must be between 4 and 50 characters")
    private String city;

    @NotBlank
    @Size(min = 2, max = 50, message = "State name must be between 2 and 50 characters")
    private String state;

    @NotBlank
    @Size(min = 2, max = 50, message = "Country name must be between 2 and 50 characters")
    private String country;

    @NotBlank
    @Pattern(regexp = "\\d{5,10}", message = "Pin code must be numeric and between 5 to 10 digits")
    private String pinCode;
}
