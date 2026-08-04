package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PatchMapping("/{customerId}/notification-preference")
    public ResponseEntity<ApiResponse> updatingNotificationPreference(@PathVariable Long customerId, @Valid @RequestBody NotificationPreference updateRequest) {
        return notificationService.updateNotificationPreference(customerId, updateRequest)
                .map( _ -> ResponseEntity.ok(new ApiResponse("Notification preference updated successfully")))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse("Customer not found")));
    }
}
