package com.group2.volunteer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreationDTO {
    @NotBlank(message = "Project title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String imageUrl;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Target volunteers is required")
    @Min(value = 1, message = "Target volunteers must be at least 1")
    private Integer targetVolunteers;

    @NotNull(message = "Target donation is required")
    @DecimalMin(value = "0.0", message = "Target donation must be greater than or equal to 0")
    private Double targetDonation;

    @NotNull(message = "Category is required")
    private Long categoryId;
}
