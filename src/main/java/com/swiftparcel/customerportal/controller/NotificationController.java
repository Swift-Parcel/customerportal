package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PatchMapping("/{customerId}/notification-preference")
    public ResponseEntity<String> updatingNotificationPreference(@PathVariable Long customerId, @Valid @RequestBody NotificationPreference updateRequest) {
        return notificationService.updateNotificationPreference(customerId, updateRequest)
                .map( _ -> ResponseEntity.ok("Notification preference updated successfully"))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
