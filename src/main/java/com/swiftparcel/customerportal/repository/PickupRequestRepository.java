package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {
    long countByCustomerIdAndCurrentStatusIn(Long customerId, Collection<CurrentStatus> statuses);
}
