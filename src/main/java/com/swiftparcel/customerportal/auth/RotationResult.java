package com.swiftparcel.customerportal.auth;

import com.swiftparcel.customerportal.model.Customer;

public record RotationResult(Customer customer, String refreshToken) {}