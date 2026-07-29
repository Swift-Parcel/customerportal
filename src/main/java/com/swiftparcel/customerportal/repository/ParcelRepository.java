package com.swiftparcel.customerportal.repository;


import com.swiftparcel.customerportal.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Customer, Long> {


}
