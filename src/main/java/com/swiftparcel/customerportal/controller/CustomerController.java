package com.swiftparcel.customerportal.controller;

import com.swiftparcel.customerportal.dto.ApiResponse;
import com.swiftparcel.customerportal.dto.CustomerAccountRequest;
import com.swiftparcel.customerportal.dto.CustomerAccountResponse;
import com.swiftparcel.customerportal.dto.CustomerDTO;
import com.swiftparcel.customerportal.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customerportal/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok((Object) customer))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Customer not found")));
    }

    @PatchMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody CustomerDTO updateRequest) {
        return customerService.updateCustomer(id, updateRequest)
                .map(customer -> ResponseEntity.ok((Object) customer))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Customer not found")));
    }


    @PostMapping
    public CustomerAccountResponse createCustomer(@RequestBody CustomerAccountRequest userRequest) {
        return customerService.createCustomer(userRequest);
    }

    @DeleteMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse> deleteCustomer(@RequestBody String email) {
        customerService.deleteCustomer(email);
        return ResponseEntity.ok(new ApiResponse("Customer deleted successfully"));
    }

}
