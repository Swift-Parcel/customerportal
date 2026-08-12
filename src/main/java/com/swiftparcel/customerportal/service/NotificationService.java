package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.model.Notification;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.model.enums.NotificationEventType;
import com.swiftparcel.customerportal.model.enums.NotificationStatus;
import com.swiftparcel.customerportal.repository.NotificationHistoryRepository;
import com.swiftparcel.customerportal.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;

    public void processNotification(String email, NotificationEventType eventType, String message) {
        notificationRepository.findByCustomer_Email(email).ifPresent(preference -> {
            sendNotificationIfEnabled(preference, eventType, message);
        });
    }

    private void sendNotificationIfEnabled(NotificationPreference preference, NotificationEventType eventType, String message) {
        if (isEventEnabled(preference, eventType)) {
            Notification notification = Notification.builder()
                    .customer(preference.getCustomer())
                    .eventType(eventType)
                    .status(NotificationStatus.SENT)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationHistoryRepository.save(notification);
        }
    }

    private boolean isEventEnabled(NotificationPreference preference, NotificationEventType eventType) {
        return switch (eventType) {
            case PARCEL_STATUS    -> preference.getParcelStatus();
            case DELIVERY_STATUS  -> preference.getDeliveryStatus();
            case CASE_STATUS      -> preference.getCaseStatus();
            case DELIVERY_CHANGE  -> preference.getDeliveryChange();
            case PICKUP_CONFIRMED -> preference.getPickupConfirmed();
            case QUOTE_EXPIRING   -> preference.getQuoteExpiring();
        };
    }

    public Optional<NotificationPreference> updateNotificationPreference(Long customerId, NotificationPreference updateRequest) {
        return notificationRepository.findByCustomer_Id(customerId)
                .map(existing -> {
                   patchNotificationPreference(updateRequest, existing);
                   return notificationRepository.save(existing);
                });
    }

    private void patchNotificationPreference(NotificationPreference source, NotificationPreference target) {
        if (source == null || target == null) { return; }
        if (source.getParcelStatus() != null) { target.setParcelStatus(source.getParcelStatus()); }
        if (source.getDeliveryStatus() != null) { target.setDeliveryStatus(source.getDeliveryStatus()); }
        if (source.getCaseStatus() != null) { target.setCaseStatus(source.getCaseStatus()); }
        if (source.getDeliveryChange() != null) { target.setDeliveryChange(source.getDeliveryChange()); }
        if (source.getPickupConfirmed() != null) { target.setPickupConfirmed(source.getPickupConfirmed()); }
        if (source.getQuoteExpiring() != null) { target.setQuoteExpiring(source.getQuoteExpiring()); }
    }
}
