package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.*;
import com.swiftparcel.customerportal.service.ComplaintCaseService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customerportal/cases")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ComplaintCaseController {

    private final ComplaintCaseService complaintCaseService;

    @PostMapping
    public ResponseEntity<CaseResponse> openCase(
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        CaseResponse response = complaintCaseService.createCase(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CaseSummaryResponse>> getMyCases(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<CaseSummaryResponse> cases = complaintCaseService.getCustomerCases(userDetails.getUsername());
        return ResponseEntity.ok(cases);
    }

    @PostMapping("/{caseNumber}/feedback")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable String caseNumber,
            @Valid @RequestBody SubmitFeedbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        complaintCaseService.submitFeedback(caseNumber, request, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{caseNumber}")
    public ResponseEntity<CaseDetailResponse> getCaseDetail(
            @PathVariable String caseNumber,
            @AuthenticationPrincipal UserDetails userDetails) {

        CaseDetailResponse response = complaintCaseService.getCaseDetail(caseNumber, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{caseNumber}/notes")
    public ResponseEntity<Void> addCaseNote(
            @PathVariable String caseNumber,
            @Valid @RequestBody AddCaseNoteRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        complaintCaseService.addCaseNote(caseNumber, request.getMessage(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
