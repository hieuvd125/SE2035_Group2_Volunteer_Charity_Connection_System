package com.group2.volunteer.service;

import com.group2.volunteer.entity.ProjectRegistration;

import java.util.List;

public interface ProjectRegistrationService {
    List<ProjectRegistration> getProjectRegistrations(Long projectId, Long organizerId);
    Long countProjectRegistrations(Long projectId, Long organizerId);
    void approveRegistration(Long projectId, Long registrationId, Long organizerId);
    void rejectRegistration(Long projectId, Long registrationId, Long organizerId);

}
