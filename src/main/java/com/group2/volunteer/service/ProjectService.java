package com.group2.volunteer.service;

import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.Project;
import java.util.List;

public interface ProjectService {
    Project createProject(ProjectDTO dto, Long organizerId);

    Project approveProject(Long projectId);

    List<Project> getPendingProjects();

    List<Project> getProjectsByOrganizer(Long organizerId, String keyword, String location);
    List<Project> getAllProject();

    List<Project> getRecruitingProjects(String keyword, String location);
    Project getProjectById(Long id);
    void applyForProject(Long projectId, Long userId);
}