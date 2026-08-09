package com.swiftparcel.customerportal.auth.controller;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customerportal")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DemoController {

    @PostMapping("/demo")
    public String welcome(){
        return "Welcome from secure endpoint";
    }
}
