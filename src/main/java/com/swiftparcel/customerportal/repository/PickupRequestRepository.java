package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {

    long countByCustomerIdAndCurrentStatusIn(Long customerId, Collection<CurrentStatus> statuses);
    Optional<PickupRequest> findByTrackingNumber(String trackingNumber);
    @Query("select p from PickupRequest p where p.id = :id")
    Optional<PickupRequest> findByIdForUpdate(@Param("id") Long id);

}

