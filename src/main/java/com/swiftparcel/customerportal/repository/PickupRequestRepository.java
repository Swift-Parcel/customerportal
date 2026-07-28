package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.PickupRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickupRequestRepository extends JpaRepository<PickupRequest, Long> {

}
