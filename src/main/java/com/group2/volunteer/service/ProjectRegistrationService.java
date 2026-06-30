package com.group2.volunteer.service;

import com.group2.volunteer.entity.ProjectRegistration;
import java.util.List;

public interface ProjectRegistrationService {
    List<ProjectRegistration> getRegistrationsByProject(Long projectId);

    ProjectRegistration updateRegistrationStatus(Long registrationId, String status);
}