package com.group2.volunteer.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AttendanceProofRequest {
    private Long registrationId;
    private String reportText;
    private MultipartFile proofImage;
}
