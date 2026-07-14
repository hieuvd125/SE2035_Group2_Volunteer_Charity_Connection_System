package com.group2.volunteer.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class DonationDTO {
    private Long projectId;
    private Double amount;
    private boolean anonymous;
}
