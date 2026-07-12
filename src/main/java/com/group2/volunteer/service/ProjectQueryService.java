package com.group2.volunteer.service;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.dto.RegistrationRequest;
import com.group2.volunteer.entity.Project;
import java.util.List;

public interface ProjectQueryService {
    List<Project> getAvailableProjects(ProjectSearchCriteria criteria);
    Project getProjectById(Long id);
    void applyToProject(RegistrationRequest request) throws Exception;
}
