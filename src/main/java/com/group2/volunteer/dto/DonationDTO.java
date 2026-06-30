package com.group2.volunteer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DonationDTO {

    @NotBlank(message = "Donor name is required")
    private String donorName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1000.0", message = "Amount must be at least 1000")
    private Double amount;
}
