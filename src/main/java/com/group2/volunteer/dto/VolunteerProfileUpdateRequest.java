package com.group2.volunteer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class VolunteerProfileUpdateRequest {
    private String fullName;
    private String phoneNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String gender;
    private String city;
    private String address;
    private String avatarUrl;
    private String bio;
    private String website;
}
