package com.swiftparcel.customerportal.service;

import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.model.Customer;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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



//    @Transactional
//    public CustomerAccountResponse createCustomer(CustomerAccountRequest request) {
//        if (request == null) throw new BadRequestException("Request body required");
//        if (customerRepository.existsByEmail(request.getEmail()))
//            throw new EmailAlreadyExistsException(request.getEmail());
//
//        Customer customer = customerRepository.save(Customer.builder()
//                .email(request.getEmail())
//                .fullName(request.getFullName())
//                .phoneNumber(request.getPhoneNumber())
//                .passwordHash(passwordEncoder.encode(request.getRawPassword()))
//                .backofficeSyncStatus(BackofficeSyncStatus.PENDING)
//                .build());
//
//        return toResponse(customer);
//    }
//
//
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onCustomerCreated(CustomerCreatedEvent event) {
//        Customer c = customerRepository.findById(event.customerId()).orElseThrow();
//        try {
//            var resp = backofficeClient.createCustomer(
//                    new BackofficeCustomerRequest(c.getEmail(), c.getFullName(), c.getPhoneNumber()));
//            c.setBackofficeId(resp.backofficeId());
//            c.setBackofficeSyncStatus(BackofficeSyncStatus.SYNCED);
//        } catch (BackofficeSyncException e) {
//            c.setBackofficeSyncStatus(BackofficeSyncStatus.FAILED);  // picked up by a retry job later
//        }
//        customerRepository.save(c);
//    }


    }
