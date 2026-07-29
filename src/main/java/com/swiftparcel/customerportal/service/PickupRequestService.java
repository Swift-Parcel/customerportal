package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.PickupRequestDTO;
import com.swiftparcel.customerportal.model.PickupRequest;
import com.swiftparcel.customerportal.model.enums.CurrentStatus;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.enums.ServiceType;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.repository.AddressRepository;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.repository.PickupRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PickupRequestService {
    private final PickupRequestRepository pickupRequestRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    public String createPickupRequest(PickupRequestDTO pickupRequestDTO, Long customerId) {
        if(pickupRequestDTO == null){
            return null;
        }

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            return "Customer not found";
        }
        Customer customer = customerOpt.get();

        if (!customerLimit(customerId)) {
            return "Customer cannot have more than 5 unconfirmed pickup requests";
        }

        Long senderAddressId = pickupRequestDTO.getSenderAddress();

        Optional<Address> senderAddressOpt = addressRepository.findById(senderAddressId);
        Optional<Address> recipientAddressOpt = addressRepository.findById(pickupRequestDTO.getRecipientAddress());

        if (senderAddressOpt.isEmpty()) return "Sender address not found";
        if (recipientAddressOpt.isEmpty()) return "Recipient address not found";

        Address senderAddress = senderAddressOpt.get();
        Address recipientAddress = recipientAddressOpt.get();

        String sameDayError = validateSameDayRules(pickupRequestDTO, senderAddress, recipientAddress);
        if (sameDayError != null) return sameDayError;

        String expressError = validateExpressLeadTime(pickupRequestDTO);
        if (expressError != null) return expressError;

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
                .customerId(customerId)
                .senderAddress(senderAddressId)
                .serviceType(pickupRequestDTO.getServiceType())
                .currentStatus(CurrentStatus.DRAFT)
                .build();

        pickupRequestRepository.save(pickupRequest);

        return("Pickup Request created");
    }

    public boolean customerLimit(Long customerId) {
        long count = pickupRequestRepository.countByCustomerIdAndCurrentStatusIn(
                customerId,
                List.of(CurrentStatus.DRAFT, CurrentStatus.QUOTED)
        );
        return count < 5;
    }

    private String validateSameDayRules(PickupRequestDTO pickupRequestDTO, Address sender, Address recipient) {
        if (pickupRequestDTO.getServiceType() == ServiceType.SAME_DAY) {
            if (!sender.getCountryCode().equals(recipient.getCountryCode())) {
                return "Same-Day service is not available for cross-country routes";
            }
            
            if (pickupRequestDTO.getPreferredPickupDate().equals(LocalDate.now())) {
                if (LocalTime.now().isAfter(LocalTime.of(10, 0))) {
                    return "Same-Day service must be requested before 10:00 AM on the same day";
                }
            }
        }
        return null;
    }

    private String validateExpressLeadTime(PickupRequestDTO pickupRequestDTO) {
        if (pickupRequestDTO.getServiceType() == ServiceType.EXPRESS) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime slotStart = LocalDateTime.of(
                    pickupRequestDTO.getPreferredPickupDate(),
                    LocalTime.of(pickupRequestDTO.getPreferredTimeSlot().getStartingHour(), 0)
            );
            
            if (now.isAfter(slotStart.minusHours(2))) {
                return "Express service must be requested at least 2 hours before the time slot starts";
            }
        }
        return null;
    }
}
