package com.swiftparcel.customerportal.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

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



    @Enumerated(EnumType.STRING)
    @Column(name = "backoffice_sync_status", nullable = false, length = 20)
    private BackofficeSyncStatus backofficeSyncStatus;   // PENDING, SYNCED, FAILED

    @Column(name = "backoffice_id")
    private String backofficeId;

}
