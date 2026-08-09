package com.swiftparcel.customerportal.model;

import com.swiftparcel.customerportal.model.enums.CaseStatus;
import com.swiftparcel.customerportal.model.enums.CaseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "complaint_cases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String caseNumber; // Format e.g., CASE-2026-XXXXX

    @ElementCollection
    @CollectionTable(name = "case_tracking_numbers", joinColumns = @JoinColumn(name = "case_id"))
    @Column(name = "tracking_number")
    private List<String> trackingNumbers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseType caseType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private String channel; // e.g., "PORTAL"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @Min(1)
    @Max(5)
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore;

    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;

    @Column(name = "feedback_submitted_at")
    private LocalDateTime feedbackSubmittedAt;





    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.channel == null) {
            this.channel = "PORTAL";
        }
        if (this.status == null) {
            this.status = CaseStatus.OPEN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}