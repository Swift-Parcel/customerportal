package com.swiftparcel.customerportal.repository;

import com.swiftparcel.customerportal.model.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface QuotesRepository extends JpaRepository<Quote, Long> {

    Optional<Quote> findByPickupRequestId(Long pickupRequestId);

    @Query("""
            SELECT q FROM Quote q
            WHERE q.quotedAt >= :since
              AND EXISTS (SELECT 1 FROM PickupRequest p
                          WHERE p.id = q.pickupRequestId
                            AND p.customerId = :customerId)
            ORDER BY q.quotedAt DESC
            """)
    List<Quote> findQuoteHistory(@Param("customerId") Long customerId,
                                 @Param("since") Instant since);
}