package com.swiftparcel.customerportal.services;

import com.swiftparcel.customerportal.dto.CustomerDTO;
import com.swiftparcel.customerportal.dto.AddressDTO;
import com.swiftparcel.customerportal.model.Address;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::mapToDTO);
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
