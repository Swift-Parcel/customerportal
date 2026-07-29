package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
