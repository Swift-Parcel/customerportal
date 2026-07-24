package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public CustomerAccountResponse createCustomer(CustomerAccountRequest customerAccountRequest) {
        if(customerAccountRequest == null){
            return null;
        }

        Customer customer = Customer.builder()
                .email(customerAccountRequest.getEmail())
                .fullName(customerAccountRequest.getFullName())
                .phoneNumber(customerAccountRequest.getPhoneNumber())
                .passwordHash(bCryptPasswordEncoder.encode(customerAccountRequest.getPasswordHash()))
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        return CustomerAccountResponse.builder()
                .id(savedCustomer.getId())
                .email(savedCustomer.getEmail())
                .fullName(savedCustomer.getFullName())
                .phoneNumber(savedCustomer.getPhoneNumber())
                .build();
    }
    public void deleteCustomer(Long customerId) {
        customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException());
        customerRepository.deleteById(customerId);

    }

    }
