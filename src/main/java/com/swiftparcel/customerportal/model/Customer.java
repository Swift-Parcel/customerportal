package com.swiftparcel.customerportal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "phone_number", nullable = false, length = 30)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "default_address_id", referencedColumnName = "id")
    private Address defaultAddress;
}
