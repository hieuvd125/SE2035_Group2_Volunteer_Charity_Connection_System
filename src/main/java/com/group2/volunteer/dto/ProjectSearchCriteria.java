package com.group2.volunteer.dto;
import lombok.Data;

@Data
public class ProjectSearchCriteria {
    private String title;
    private String location;
    private Long categoryId;
}
