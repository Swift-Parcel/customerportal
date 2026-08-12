package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByCityAndPostalCodeAndCountryCodeAndStreetAndStreetNumber(String city, String postalCode, String countryCode, String street, String streetNumber);
}
