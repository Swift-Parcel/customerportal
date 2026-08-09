package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;

import com.swiftparcel.customerportal.dto.DeliveryChangeDTO;
import com.swiftparcel.customerportal.dto.ParcelStatusWebhookDTO;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.service.DeliveryService;
import com.swiftparcel.customerportal.service.NotificationService;
import com.swiftparcel.customerportal.service.ParcelService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final DeliveryService deliveryService;
    private final ParcelService parcelService;

    @PatchMapping("/api/customerportal/customer/{customerId}/notification-preference")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> updatingNotificationPreference(@PathVariable Long customerId, @Valid @RequestBody NotificationPreference updateRequest) {
        return notificationService.updateNotificationPreference(customerId, updateRequest)
                .map( _ -> ResponseEntity.ok(new ApiResponse("Notification preference updated successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Customer not found")));
    }

    @PostMapping("/cases/delivery-change")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> deliveryChangeWebhook(@RequestBody DeliveryChangeDTO deliveryChangeDTO) {
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


    @PostMapping("/api/webhooks/parcels/status")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> parcelStatusWebhook(
            @Valid @RequestBody ParcelStatusWebhookDTO dto) {

        log.info("Received parcel status webhook for tracking number: {}", dto.getTrackingNumber());

        parcelService.updateParcelStatus(dto)
                .ifPresent(parcel -> {
                    String message = "Your parcel " + parcel.getTrackingNumber()
                            + " status changed to " + parcel.getStatus();

                    notificationService.processNotification(
                            parcel.getCustomer().getEmail(),
                            NotificationEventType.PARCEL_STATUS,
                            message
                    );
                });

        return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
    }
}
