package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    Optional<Parcel> findByTrackingNumber(String trackingNumber);

    List<Parcel> findByCustomerId(Long customerId);
}