package com.group2.volunteer.dto;

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
    @NotBlank(message = "Tên dự án không được để trống")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    private String imageUrl;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là hôm nay hoặc trong tương lai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull(message = "Số lượng tình nguyện viên cần có")
    @Min(value = 1, message = "Số lượng tối thiểu là 1")
    private Integer targetVolunteers;

    @NotNull(message = "Vui lòng chọn category")
    private Long categoryId;
}
