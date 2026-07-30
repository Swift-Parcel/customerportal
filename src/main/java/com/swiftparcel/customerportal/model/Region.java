package com.swiftparcel.customerportal.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "region")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Region {
    @Id
    Long id;

    @Column(name = "code")
    String code;

    @Column(name = "city")
    String city;

    @Column(name = "country_code")
    String countryCode;

    //IANA timezone
    @Column(name = "timezone")
    String timezone;

}
