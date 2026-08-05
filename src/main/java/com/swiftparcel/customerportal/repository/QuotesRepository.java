package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuotesRepository extends JpaRepository<Quote, Long> {
}