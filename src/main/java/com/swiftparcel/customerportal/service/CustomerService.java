package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.AddressDTO;
import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.dto.CustomerDTO;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.model.NotificationPreference;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public CustomerAccountResponse createCustomer(CustomerAccountRequest customerAccountRequest) {
        if (customerAccountRequest == null) {
            return null;
        }

        Customer customer = Customer.builder()
                .email(customerAccountRequest.getEmail())
                .fullName(customerAccountRequest.getFullName())
                .phoneNumber(customerAccountRequest.getPhoneNumber())
                .passwordHash(bCryptPasswordEncoder.encode(customerAccountRequest.getPassword()))
                .build();

        // Initialize default notification preferences
        NotificationPreference defaultPreferences = NotificationPreference.builder()
                .customer(customer)
                .parcelStatus(true)
                .deliveryStatus(true)
                .caseStatus(true)
                .deliveryChange(true)
                .pickupConfirmed(true)
                .quoteExpiring(true)
                .build();
        customer.setNotificationPreference(defaultPreferences);

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerAccountResponse.builder()
                .id(savedCustomer.getId())
                .email(savedCustomer.getEmail())
                .fullName(savedCustomer.getFullName())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .build();
    }

    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::mapToDTO);
    }

    public void deleteCustomer(String email) {
        customerRepository.deleteByEmail(email);

    }

    public Optional<CustomerDTO> updateCustomer(Long id, CustomerDTO updateRequest) {
        return customerRepository.findById(id)
                .map(existing -> {
                    patchCustomerFromDTO(updateRequest, existing);
                    return mapToDTO(customerRepository.save(existing));
                });
    }

    private CustomerDTO mapToDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerDTO.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .preferredLanguage(customer.getPreferredLanguage())
                .defaultAddress(mapToAddressDTO(customer.getDefaultAddress()))
                .build();
    }

    private AddressDTO mapToAddressDTO(Address address) {
        if (address == null) {
            return null;
        }
        return AddressDTO.builder()
                .id(address.getId())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .countryCode(address.getCountryCode())
                .build();
    }

    private void patchCustomerFromDTO(CustomerDTO customerDto, Customer customer) {
        if (customerDto == null || customer == null) {
            return;
        }
        if (customerDto.getEmail() != null) {
            customer.setEmail(customerDto.getEmail());
        }
        if (customerDto.getFullName() != null) {
            customer.setFullName(customerDto.getFullName());
        }
        if (customerDto.getPhoneNumber() != null) {
            customer.setPhoneNumber(customerDto.getPhoneNumber());
        }
        if (customerDto.getPreferredLanguage() != null) {
            customer.setPreferredLanguage(customerDto.getPreferredLanguage());
        }
        if (customerDto.getDefaultAddress() != null) {
            if (customer.getDefaultAddress() == null) {
                customer.setDefaultAddress(new Address());
            }
            patchAddressFromDTO(customerDto.getDefaultAddress(), customer.getDefaultAddress());
        }
    }

    private void patchAddressFromDTO(AddressDTO addressDto, Address address) {
        if (addressDto == null || address == null) {
            return;
        }
        if (addressDto.getCity() != null) {
            address.setCity(addressDto.getCity());
        }
        if (addressDto.getPostalCode() != null) {
            address.setPostalCode(addressDto.getPostalCode());
        }
        if (addressDto.getCountryCode() != null) {
            address.setCountryCode(addressDto.getCountryCode());
        }
    }

}
