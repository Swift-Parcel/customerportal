package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByCityAndPostalCodeAndCountryCode(String city, String postalCode, String countryCode);
}
