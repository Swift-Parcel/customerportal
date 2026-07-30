package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

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
