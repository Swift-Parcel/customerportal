package com.swiftparcel.customerportal.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAccountResponse {
    Long id;
    String email;
    String fullName;
    String phoneNumber;
    String preferredLanguage;
    AddressDTO defaultAddress;

}
