package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.ComplaintCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ComplaintCaseRepository extends JpaRepository<ComplaintCase, Long> {
    List<ComplaintCase> findByCustomerEmailOrderByCreatedAtDesc(String email);
    Optional<ComplaintCase> findByCaseNumber(String caseNumber);
}
