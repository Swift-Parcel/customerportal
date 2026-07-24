package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select c from Customer c where c.email = :email")
    Optional<Customer> getCustomerFromDb(String email);

    boolean existsByEmail(String email);
}
