package com.swiftparcel.customerportal.auth.dto;

import com.swiftparcel.customerportal.model.Customer;

public record RotationResult(Customer customer, String refreshToken) {}