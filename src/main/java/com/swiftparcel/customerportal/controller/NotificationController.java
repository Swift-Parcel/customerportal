package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.DeliveryChangeWebhookDTO;
import com.swiftparcel.customerportal.dto.ParcelStatusWebhookDTO;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PatchMapping("/api/customerportal/customer/{customerId}/notification-preference")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> updatingNotificationPreference(@PathVariable Long customerId, @Valid @RequestBody NotificationPreference updateRequest) {
        return notificationService.updateNotificationPreference(customerId, updateRequest)
                .map( _ -> ResponseEntity.ok(new ApiResponse("Notification preference updated successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Customer not found")));
    }

    @PostMapping("/api/webhooks/cases/delivery-change")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> deliveryChangeWebhook(@RequestBody DeliveryChangeWebhookDTO deliveryChangeWebhookDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String message = "Your Delivery change request for the case: "
                    + deliveryChangeWebhookDTO.getCaseNumber()
                    + " was "
                    + deliveryChangeWebhookDTO.getOutcome();

            notificationService.processNotification(deliveryChangeWebhookDTO.getCustomerEmail(), NotificationEventType.DELIVERY_CHANGE, message);

            return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Not authenticated"));
    }

    @PostMapping("/api/webhooks/parcel/status-change")
    @SecurityRequirement(name = "apiKey")
    public ResponseEntity<ApiResponse> parcelStatusChangeWebhook(@RequestBody ParcelStatusWebhookDTO parcelStatusUpdateWebhookDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String message = "The status for the parcel with tracking number "
                    + parcelStatusUpdateWebhookDTO.getTrackingNumber()
                    + " was updated to"
                    + parcelStatusUpdateWebhookDTO.getParcelStatus();

//            notificationService.processNotification(parcelStatusUpdateWebhookDTO.getCustomerEmail(), NotificationEventType.DELIVERY_CHANGE, message);

            return ResponseEntity.ok(new ApiResponse("Webhook received successfully"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse("Not authenticated"));
    }
}
