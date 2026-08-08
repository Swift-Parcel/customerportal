package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.CaseChangeDTO;
import com.swiftparcel.customerportal.dto.DeliveryChangeDTO;
import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.service.DeliveryService;
import com.swiftparcel.customerportal.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final DeliveryService deliveryService;
    private final NotificationService notificationService;

    @PostMapping("/cases/delivery-change")
    @SecurityRequirement(name = "apiKey")
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


    @PostMapping("/cases/status")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> caseChangeWebhook(@RequestBody CaseChangeDTO caseChangeDTO) {
        log.info("Received case status change webhook for case: {}", caseChangeDTO.getCaseNumber());

        // todo Update the case table and extract the Customer from that table
        notificationService.processNotification(
                "custumerEmail",
                NotificationEventType.DELIVERY_CHANGE,
                "message"
        );

        return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
    }
}
