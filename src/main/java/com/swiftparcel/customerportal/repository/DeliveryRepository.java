package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.DeliveryChangeRequest;
import com.swiftparcel.customerportal.model.enums.DeliveryChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<DeliveryChangeRequest, Long> {
    List<DeliveryChangeRequest> findByCustomerId(Long customerId);
    List<DeliveryChangeRequest> findByTrackingNumber(String trackingNumber);
    Optional<DeliveryChangeRequest> findByCaseNumber(String caseNumber);

    @Query("SELECT d FROM DeliveryChangeRequest d WHERE d.customer.id = :customerId AND d.trackingNumber = :trackingNumber AND d.status IN :statuses")
    List<DeliveryChangeRequest> findByCustomerIdAndTrackingNumberAndStatusIn(Long customerId, String trackingNumber, List<DeliveryChangeStatus> statuses);
}
