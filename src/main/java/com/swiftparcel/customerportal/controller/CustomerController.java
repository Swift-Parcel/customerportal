package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.dto.CustomerDTO;
import com.swiftparcel.customerportal.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO updateRequest) {
        return customerService.updateCustomer(id, updateRequest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public CustomerAccountResponse createCustomer(@RequestBody CustomerAccountRequest userRequest) {
        return customerService.createCustomer(userRequest);
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public void deleteCustomer(@RequestBody String email) {
        customerService.deleteCustomer(email);
    }

}
