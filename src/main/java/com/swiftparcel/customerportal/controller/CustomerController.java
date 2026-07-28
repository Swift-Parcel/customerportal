package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.repository.CustomerRepository;
import com.swiftparcel.customerportal.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public CustomerAccountResponse createCustomer(@RequestBody CustomerAccountRequest userRequest){
        return customerService.createCustomer(userRequest);
    }

    @DeleteMapping
    public void deleteCustomer(@RequestBody String email){
        customerService.deleteCustomer(email);
    }

}
