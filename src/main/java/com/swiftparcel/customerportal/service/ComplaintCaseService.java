package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseRequest;
import com.swiftparcel.customerportal.dto.integrationComplainCase.BackOfficeCaseStatusResponse;
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
import java.util.Optional;
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
                .title(request.getTitle())
                .build();

        String CaseNumber = backOfficeService.sendCaseToBackOffice(backOfficeRequest);

        ComplaintCase complaintCase = ComplaintCase.builder()
                .caseNumber(CaseNumber)
                .trackingNumbers(request.getTrackingNumbers())
                .caseType(request.getCaseType())
                .description(request.getDescription())
                .title(request.getTitle())
                .customer(customer)
                .build();

        ComplaintCase savedCase = caseRepository.save(complaintCase);


        return CaseResponse.builder()
                .caseNumber(savedCase.getCaseNumber())
                .trackingNumbers(savedCase.getTrackingNumbers())
                .caseType(savedCase.getCaseType())
                .description(savedCase.getDescription())
                .title(savedCase.getTitle())
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

    @Transactional
    public Optional<ComplaintCase> updateCaseStatus(CaseChangeDTO dto) {
        return caseRepository.findByCaseNumber(dto.getCaseNumber())
                .filter(c -> !c.getStatus().equals(dto.getCaseStatus()))
                .map(c -> {
                    c.setStatus(dto.getCaseStatus());
                    ComplaintCase updatedCase = caseRepository.save(c);
                    // Force initialization of lazy customer email while transaction is open
                    if (updatedCase.getCustomer() != null) {
                        updatedCase.getCustomer().getEmail();
                    }
                    return updatedCase;
                });
    }

    @Transactional(readOnly = true)
    public CaseDetailResponse getCaseDetail(String caseNumber, String customerEmail) {
        ComplaintCase complaintCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found: " + caseNumber));

        if (!complaintCase.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this case.");
        }

        BackOfficeCaseStatusResponse backOfficeStatus = backOfficeService.getCaseStatus(caseNumber);


        List<String> trackingNumbers = List.copyOf(complaintCase.getTrackingNumbers());


        List<CaseNoteResponse> notes = (backOfficeStatus.getNotes() == null)
                ? List.of()
                : backOfficeStatus.getNotes().stream()
                .map(n -> CaseNoteResponse.builder()
                        .timestamp(n.getTimestamp())
                        .note(n.getNote())
                        .build())
                .toList();

        return CaseDetailResponse.builder()
                .caseNumber(complaintCase.getCaseNumber())
                .caseType(complaintCase.getCaseType())
                .trackingNumbers(trackingNumbers)
                .description(complaintCase.getDescription())
                .status(backOfficeStatus.getCaseStatus())
                .resolution(backOfficeStatus.getResolution())
                .notes(notes)
                .createdAt(complaintCase.getCreatedAt())
                .updatedAt(complaintCase.getUpdatedAt())
                .build();
    }

    @Transactional
    public void addCaseNote(String caseNumber, String message, String customerEmail) {
        ComplaintCase complaintCase = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found: " + caseNumber));

        if (!complaintCase.getCustomer().getEmail().equalsIgnoreCase(customerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to access this case.");
        }

        backOfficeService.addCaseNote(caseNumber, customerEmail, message);
    }
}
