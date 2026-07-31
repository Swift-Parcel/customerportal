package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.ServiceRate;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRateRepository extends JpaRepository<ServiceRate, Long> {
    Optional<ServiceRate> findByServiceType(ServiceType serviceType);
}
