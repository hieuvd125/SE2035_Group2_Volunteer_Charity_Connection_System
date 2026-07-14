package com.group2.volunteer.service;

import org.springframework.stereotype.Service;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.entity.Project;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Override
    public List<Project> getAvailableProjects(ProjectSearchCriteria criteria) {
        return null;
    }
}