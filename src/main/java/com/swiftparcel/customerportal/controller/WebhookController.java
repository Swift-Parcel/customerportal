package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.CaseChangeDTO;
import com.swiftparcel.customerportal.dto.DeliveryChangeDTO;
import com.swiftparcel.customerportal.dto.ParcelStatusWebhookDTO;
import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.service.ComplaintCaseService;
import com.swiftparcel.customerportal.service.DeliveryService;
import com.swiftparcel.customerportal.service.NotificationService;
import com.swiftparcel.customerportal.service.ParcelService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final ParcelService parcelService;
    private final DeliveryService deliveryService;
    private final NotificationService notificationService;
    private final ComplaintCaseService complaintCaseService;
    private final CustomerRepository customerRepository;

    @PostMapping("/cases/delivery-change")
    @SecurityRequirement(name = "apiKey")
    @Transactional
    public ResponseEntity<ApiResponse> deliveryChangeWebhook(@RequestBody DeliveryChangeDTO deliveryChangeDTO) {
        log.info("Received delivery change webhook for case: {}", deliveryChangeDTO.getCaseNumber());
        
        deliveryService.updateDeliveryChangeRequest(deliveryChangeDTO)
                .ifPresent(request -> {
                    String message = "Your Delivery change request for the case: "
                            + request.getCaseNumber()
                            + " was "
                            + request.getStatus();

                    notificationService.processNotification(
                            request.getCustomer().getEmail(),
                            NotificationEventType.DELIVERY_CHANGE,
                            message
                    );
                });

        return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
    }

    @PostMapping("/parcels/status")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> parcelStatusWebhook(
            @Valid @RequestBody ParcelStatusWebhookDTO dto) {
        log.info("Received parcel status webhook for tracking number: {}", dto.getTrackingNumber());
        parcelService.updateParcelStatus(dto)
                .ifPresent(parcel -> {
                    if (parcel.getCustomerId() == null) {
                        return;
                    }
                    String message = "Your parcel " + parcel.getTrackingNumber()
                            + " status changed to " + parcel.getStatus();
                    customerRepository.findById(parcel.getCustomerId())
                            .ifPresent(customer -> notificationService.processNotification(
                                    customer.getEmail(),
                                    NotificationEventType.PARCEL_STATUS,
                                    message
                            ));
                });
        return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
    }


    @PostMapping("/cases/status")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> caseChangeWebhook(@RequestBody CaseChangeDTO caseChangeDTO) {
        log.info("Received case status change webhook for case: {}", caseChangeDTO.getCaseNumber());

        complaintCaseService.updateCaseStatus(caseChangeDTO)
                .ifPresent(updatedCase -> {
                    String message = "Your case " + updatedCase.getCaseNumber()
                            + " status changed to " + updatedCase.getStatus();

                    notificationService.processNotification(
                            updatedCase.getCustomer().getEmail(),
                            NotificationEventType.CASE_STATUS,
                            message
                    );
                });

        return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
    }
}
