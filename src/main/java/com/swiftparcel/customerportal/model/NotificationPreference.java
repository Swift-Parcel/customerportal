package com.swiftparcel.customerportal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_preference")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Customer customer;


    @Column(name = "parcel_status")
    @JsonProperty("parcel_status")
    private Boolean parcelStatus;

    @Column(name = "delivery_status")
    @JsonProperty("delivery_status")
    private Boolean deliveryStatus;

    @Column(name = "case_status")
    @JsonProperty("case_status")
    private Boolean caseStatus;

    @Column(name = "delivery_change")
    @JsonProperty("delivery_change")
    private Boolean deliveryChange;

    @Column(name = "pickup_confirmed")
    @JsonProperty("pickup_confirmed")
    private Boolean pickupConfirmed;

    @Column(name = "quote_expiring")
    @JsonProperty("quote_expiring")
    private Boolean quoteExpiring;
}
