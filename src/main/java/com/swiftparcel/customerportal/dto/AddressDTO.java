package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "Postal code is required")
    @JsonProperty("postal_code")
    private String postalCode;
    @NotBlank(message = "Country code is required")
    @Size(min = 2, max = 2, message = "Country code must be 2 characters")
    @JsonProperty("country_code")
    private String countryCode;
    private String street;
    @JsonProperty("street_number")
    private String streetNumber;
    @Override
    public String toString() {
        return city + ", " + postalCode + ", " + countryCode;
    }
}
