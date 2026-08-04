package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerId(Long customerId);
}
