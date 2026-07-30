package com.swiftparcel.customerportal.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParcelResponseDTO {
    private List<ParcelDTO> parcels;
}
