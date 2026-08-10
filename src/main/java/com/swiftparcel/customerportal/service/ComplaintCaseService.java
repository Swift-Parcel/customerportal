package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.CaseResponse;
import com.swiftparcel.customerportal.dto.CaseSummaryResponse;
import com.swiftparcel.customerportal.dto.CreateCaseRequest;
import com.swiftparcel.customerportal.dto.SubmitFeedbackRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseRequest;
import com.swiftparcel.customerportal.model.ComplaintCase;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.enums.CaseStatus;
import org.springframework.transaction.annotation.Transactional;
import com.swiftparcel.customerportal.repository.ComplaintCaseRepository;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.repository.PickupRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ComplaintCaseService {

    private final ComplaintCaseRepository caseRepository;
    private final CustomerRepository customerRepository;
    private final PickupRequestRepository pickupRequestRepository;
    private final BackOfficeIntegrationService backOfficeService;

    @Transactional
    public CaseResponse createCase(CreateCaseRequest request, String customerEmail) {

        Customer customer = customerRepository.getCustomerFromDb(customerEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Customer with email " + customerEmail + " not found"
                ));

        BackOfficeCaseRequest backOfficeRequest = BackOfficeCaseRequest.builder()
                .customerEmail(customer.getEmail())
                .trackingNumbers(request.getTrackingNumbers())
                .caseType(request.getCaseType().name())
                .description(request.getDescription())
                .build();

        String backOfficeResponse = backOfficeService.sendCaseToBackOffice(backOfficeRequest);


        String generatedCaseNumber = "CASE-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ComplaintCase complaintCase = ComplaintCase.builder()
                .caseNumber(generatedCaseNumber)
                .trackingNumbers(request.getTrackingNumbers())
                .caseType(request.getCaseType())
                .description(request.getDescription())
                .channel("PORTAL")
                .customer(customer)
                .build();

        ComplaintCase savedCase = caseRepository.save(complaintCase);


        return CaseResponse.builder()
                .caseNumber(savedCase.getCaseNumber())
                .trackingNumbers(savedCase.getTrackingNumbers())
                .caseType(savedCase.getCaseType())
                .description(savedCase.getDescription())
                .channel(savedCase.getChannel())
                .createdAt(savedCase.getCreatedAt())
                .build();




    }

    @Transactional(readOnly = true)
    public List<CaseSummaryResponse> getCustomerCases(String customerEmail) {

        List<ComplaintCase> cases = caseRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail);

        return cases.stream()
                .map(c -> CaseSummaryResponse.builder()
                        .caseNumber(c.getCaseNumber())
                        .caseType(c.getCaseType())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .build())
                .toList();
    }


    @Transactional
    public void submitFeedback(String caseNumber, SubmitFeedbackRequest request, String customerEmail) {

        ComplaintCase complaintCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found: " + caseNumber));

        if (!complaintCase.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this case.");
        }

        if (complaintCase.getStatus() != CaseStatus.CLOSED && complaintCase.getStatus() != CaseStatus.RESOLVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback can only be submitted for closed or resolved cases.");
        }

        if (complaintCase.getSatisfactionScore() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Feedback has already been submitted for this case.");
        }

        complaintCase.setSatisfactionScore(request.getScore());
        complaintCase.setFeedbackComment(request.getComment());
        complaintCase.setFeedbackSubmittedAt(LocalDateTime.now());

        caseRepository.save(complaintCase);
    }
}
