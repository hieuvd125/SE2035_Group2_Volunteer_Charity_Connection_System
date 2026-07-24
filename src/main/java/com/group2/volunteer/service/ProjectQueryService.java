package com.group2.volunteer.service;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.dto.RegistrationRequest;
import com.group2.volunteer.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProjectQueryService {

    Page<Project> getAvailableProjects(ProjectSearchCriteria criteria,
                                       Pageable pageable);

    Project getProjectById(Long id);

    void applyToProject(RegistrationRequest request) throws Exception;

    Long getApprovedVolunteerCount(Long projectId);

    boolean hasApplied(Long projectId, Long userId);
}
