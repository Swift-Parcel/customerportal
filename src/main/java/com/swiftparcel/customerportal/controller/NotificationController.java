package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.service.DeliveryService;
import com.swiftparcel.customerportal.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PatchMapping("/api/customerportal/customer/{customerId}/notification-preference")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> updatingNotificationPreference(@PathVariable Long customerId, @Valid @RequestBody NotificationPreference updateRequest) {
        return notificationService.updateNotificationPreference(customerId, updateRequest)
                .map( _ -> ResponseEntity.ok(new ApiResponse("Notification preference updated successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Customer not found")));
    }
}
