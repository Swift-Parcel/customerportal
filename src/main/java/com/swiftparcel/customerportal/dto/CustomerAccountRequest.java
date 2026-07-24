package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.processing.Pattern;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAccountRequest {

    String email;
    String fullName;
    String phoneNumber;
    @Setter(AccessLevel.NONE)
    String passwordHash;


    @JsonProperty("password")
    public void setAndHashPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.passwordHash = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(
                    password,
                    org.springframework.security.crypto.bcrypt.BCrypt.gensalt()
            );

            password = null;
        }
    }

}
