package com.group2.volunteer.dto;

import lombok.Data;

@Data
public class AttendanceProofRequest {
    private Long registrationId;
    private String reportText;
    private String proofImage;
}
