package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value = "select c from Customer c where c.email = :email")
    Optional<Customer> getCustomerFromDb(String email);

    @Query(value = "DELETE FROM customer WHERE email = :email RETURNING *", nativeQuery = true)
    Optional<Customer> deleteByEmail(@Param("email") String email);

    boolean existsByEmail(String email);
}
