package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationPreference, Long> {
    Optional<NotificationPreference> findByCustomer_Id(Long customerId);
    Optional<NotificationPreference> findByCustomer_Email(String email);
}
