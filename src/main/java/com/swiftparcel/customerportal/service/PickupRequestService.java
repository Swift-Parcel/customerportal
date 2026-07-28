package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.PickupRequestDTO;
import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import com.swiftparcel.customerportal.repository.PickupRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickupRequestService {
    private final PickupRequestRepository pickupRequestRepository;

    public String createPickupRequest(PickupRequestDTO pickupRequestDTO, Long customerId) {
        if(pickupRequestDTO == null){
            return null;
        }

        PickupRequest pickupRequest = PickupRequest.builder()
                .preferredPickupDate(pickupRequestDTO.getPreferredPickupDate())
                .declaredValue(pickupRequestDTO.getDeclaredValue())
                .parcelHeight(pickupRequestDTO.getParcelHeight())
                .parcelLength(pickupRequestDTO.getParcelLength())
                .parcelWeight(pickupRequestDTO.getParcelWeight())
                .parcelWidth(pickupRequestDTO.getParcelWidth())
                .preferredTimeSlot(pickupRequestDTO.getPreferredTimeSlot())
                .recipientAddress(pickupRequestDTO.getRecipientAddress())
                .recipientName(pickupRequestDTO.getRecipientName())
                .customerId(pickupRequestDTO.getCustomerId())
                .senderAddress(pickupRequestDTO.getSenderAddress())
                .serviceType(pickupRequestDTO.getServiceType())
                .currentStatus(CurrentStatus.DRAFT)
                .build();

        pickupRequestRepository.save(pickupRequest);

        return("Pickup Request created");
    }
}
