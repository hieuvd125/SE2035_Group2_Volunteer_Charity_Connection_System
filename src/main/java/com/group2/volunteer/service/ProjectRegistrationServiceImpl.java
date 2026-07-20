package com.group2.volunteer.service;

import com.group2.volunteer.constant.RegistrationStatus;
import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectRegistrationServiceImpl implements ProjectRegistrationService {
    private final ProjectRepository projectRepository;
    private final ProjectRegistrationRepository registrationRepository;

    @Override
    public List<ProjectRegistration> getProjectRegistrations(Long projectId, Long organizerId) {
        validateOrganizerProject(projectId, organizerId);
        return registrationRepository.findByProjectId(projectId);
    }

    @Override
    public Long countProjectRegistrations(Long projectId, Long organizerId) {
        validateOrganizerProject(projectId, organizerId);
        return registrationRepository.countByProjectId(projectId);
    }

    @Override
    public void approveRegistration(Long projectId, Long registrationId, Long organizerId) {
        Project project = validateOrganizerProject(projectId, organizerId);
        ProjectRegistration registration = getRegistration(projectId, registrationId);

        if (!RegistrationStatus.PENDING.equals(registration.getStatus())) {
            throw new InvalidProjectStateException("Registration is not pending");
        }

        Long approvedCount = registrationRepository.countByProjectIdAndStatus(projectId, RegistrationStatus.APPROVED);
        if (project.getTargetVolunteers() != null && approvedCount >= project.getTargetVolunteers()) {
            throw new InvalidProjectStateException("Project has no remaining volunteer slots");
        }

        registration.setStatus(RegistrationStatus.APPROVED);
        registrationRepository.save(registration);

        if (ProjectStatus.RECRUITING.equals(project.getStatus())
                && project.getTargetVolunteers() != null
                && approvedCount + 1 >= project.getTargetVolunteers()) {
            project.setStatus(ProjectStatus.RECRUITMENT_CLOSED);
            projectRepository.save(project);
        }
    }

    @Override
    public void rejectRegistration(Long projectId, Long registrationId, Long organizerId) {
        validateOrganizerProject(projectId, organizerId);
        ProjectRegistration registration = getRegistration(projectId, registrationId);

        if (!RegistrationStatus.PENDING.equals(registration.getStatus())) {
            throw new InvalidProjectStateException("Registration is not pending");
        }

        registration.setStatus(RegistrationStatus.REJECTED);
        registrationRepository.save(registration);
    }

    private Project validateOrganizerProject(Long projectId, Long organizerId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (project.getOrganizer() == null || !project.getOrganizer().getId().equals(organizerId)) {
            throw new InvalidProjectStateException("You are not allowed to manage this project");
        }

        return project;
    }

    private ProjectRegistration getRegistration(Long projectId, Long registrationId) {
        ProjectRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        if (registration.getProject() == null || !registration.getProject().getId().equals(projectId)) {
            throw new InvalidProjectStateException("Registration does not belong to this project");
        }

        return registration;
    }
}
