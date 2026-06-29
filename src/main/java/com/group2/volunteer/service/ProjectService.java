package com.group2.volunteer.service;

import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.Project;
import java.util.List;

public interface ProjectService {
    // Thêm dòng này vào trong interface của bạn:
    Project createProject(ProjectDTO dto, Long organizerId);

    Project approveProject(Long projectId);

    // Các method khác nếu cần, ví dụ lấy danh sách PENDING
    List<Project> getPendingProjects();

    // Lấy danh sách dự án của một Organizer
    List<Project> getProjectsByOrganizer(Long organizerId);
    List<Project> getAllProject();

    List<Project> getRecruitingProjects(String keyword, String location);
    Project getProjectById(Long id);
    void applyForProject(Long projectId);
}