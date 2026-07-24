package com.group2.volunteer.service;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.dto.RegistrationRequest;
import com.group2.volunteer.entity.Project;
import java.util.List;

import com.group2.volunteer.dto.ProjectCreationDTO;
import com.group2.volunteer.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    Project createProject(ProjectCreationDTO dto, Long organizerId);
    Project updateProject(Long projectId, ProjectCreationDTO dto, Long organizerId);
    void approveProject(Long projectId);
    void rejectProject(Long projectId);
    void closeRecruitment(Long projectId, Long organizerId);
    void startProject(Long projectId, Long organizerId);
    void completeProject(Long projectId, Long organizerId);
    Page<Project> getPendingProjects(Pageable pageable);
    Page<Project> getOrganizerProjects(Long organizerId, String title, String location, String status, Pageable pageable);
    Project getProjectById(Long projectId);

}
