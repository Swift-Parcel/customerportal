package com.swiftparcel.customerportal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swiftparcel.customerportal.model.enums.TimeSlot;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDeliveryDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Future
    private LocalDate date;



    private TimeSlot timeslot;
}
