package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import java.util.List;

public interface ProjectService {
    // Thêm dòng này vào trong interface của bạn:
    List<Project> getAllProject();

    List<Project> getRecruitingProjects(String keyword, String location);
    Project getProjectById(Long id);
    void applyForProject(Long projectId);
}