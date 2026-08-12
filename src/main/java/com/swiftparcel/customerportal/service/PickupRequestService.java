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
            throw new IllegalArgumentException("Request body is missing");
        }

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new java.util.NoSuchElementException("Customer not found");
        }

        if (!customerLimit(customerId)) {
            throw new IllegalStateException("Customer cannot have more than 5 unconfirmed pickup requests");
        }

        if (pickupRequestDTO.getPreferredPickupDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("invalid date in the past");
        }

        Address senderAddress = findOrCreateAddress(pickupRequestDTO.getSenderAddress());
        Address recipientAddress = findOrCreateAddress(pickupRequestDTO.getRecipientAddress());

        validateSameDayRules(pickupRequestDTO, senderAddress, recipientAddress);

        validateExpressLeadTime(pickupRequestDTO);

        PickupRequest pickupRequest = PickupRequest.builder()
                .preferredPickupDate(pickupRequestDTO.getPreferredPickupDate())
                .declaredValue(pickupRequestDTO.getDeclaredValue())
                .parcelHeight(pickupRequestDTO.getParcelHeight())
                .parcelLength(pickupRequestDTO.getParcelLength())
                .parcelWeight(pickupRequestDTO.getParcelWeight())
                .parcelWidth(pickupRequestDTO.getParcelWidth())
                .preferredTimeSlot(pickupRequestDTO.getPreferredTimeSlot())
                .recipientAddress(recipientAddress.getId())
                .recipientName(pickupRequestDTO.getRecipientName())
                .customerId(customerId)
                .senderAddress(senderAddress.getId())
                .serviceType(pickupRequestDTO.getServiceType())
                .currentStatus(CurrentStatus.DRAFT)
                .build();

        pickupRequestRepository.save(pickupRequest);

        return("Pickup Request created");
    }

    private Address findOrCreateAddress(com.swiftparcel.customerportal.dto.AddressDTO addressDTO) {
        if (addressDTO == null) {
            throw new IllegalArgumentException("Address information is missing");
        }
        return addressRepository.findByCityAndPostalCodeAndCountryCode(
                addressDTO.getCity(),
                addressDTO.getPostalCode(),
                addressDTO.getCountryCode()
        ).orElseGet(() -> {
            Address newAddress = Address.builder()
                    .city(addressDTO.getCity())
                    .postalCode(addressDTO.getPostalCode())
                    .countryCode(addressDTO.getCountryCode())
                    .build();
            return addressRepository.save(newAddress);
        });
    }

    public boolean customerLimit(Long customerId) {
        long count = pickupRequestRepository.countByCustomerIdAndCurrentStatusIn(
                customerId,
                List.of(CurrentStatus.DRAFT, CurrentStatus.QUOTED)
        );
        return count < 5;
    }

    private void validateSameDayRules(PickupRequestDTO pickupRequestDTO, Address sender, Address recipient) {
        if (pickupRequestDTO.getServiceType() == ServiceType.SAME_DAY) {
            if (!sender.getCountryCode().equals(recipient.getCountryCode())) {
                throw new IllegalArgumentException("Same-Day service is not available for cross-country routes");
            }
            
            if (pickupRequestDTO.getPreferredPickupDate().equals(LocalDate.now())) {
                if (LocalTime.now().isAfter(LocalTime.of(10, 0))) {
                    throw new IllegalArgumentException("Same-Day service must be requested before 10:00 AM on the same day");
                }
            }
        }
    }

    private void validateExpressLeadTime(PickupRequestDTO pickupRequestDTO) {
        if (pickupRequestDTO.getServiceType() == ServiceType.EXPRESS) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime slotStart = LocalDateTime.of(
                    pickupRequestDTO.getPreferredPickupDate(),
                    LocalTime.of(pickupRequestDTO.getPreferredTimeSlot().getStartingHour(), 0)
            );
            
            if (now.isAfter(slotStart.minusHours(2))) {
                throw new IllegalArgumentException("Express service must be requested at least 2 hours before the time slot starts");
            }
        }
    }
}
