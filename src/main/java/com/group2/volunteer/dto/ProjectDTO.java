package com.group2.volunteer.dto;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class ProjectDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    private String description;
    private String imageUrl;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    @NotNull(message = "Ngày bắt đầu là bắt buộc")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc là bắt buộc")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    @NotNull(message = "Số tình nguyện viên là bắt buộc")
    @Min(value = 1, message = "Cần ít nhất 1 tình nguyện viên")
    private Integer targetVolunteers;

}