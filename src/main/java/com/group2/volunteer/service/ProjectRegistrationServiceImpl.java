package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectRegistrationServiceImpl implements ProjectRegistrationService {

    private final ProjectRegistrationRepository registrationRepository;
    private final ProjectRepository projectRepository;

    public ProjectRegistrationServiceImpl(ProjectRegistrationRepository registrationRepository,
                                          ProjectRepository projectRepository) {
        this.registrationRepository = registrationRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public List<ProjectRegistration> getRegistrationsByProject(Long projectId) {
        return registrationRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public ProjectRegistration updateRegistrationStatus(Long registrationId, String status) {
        ProjectRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lượt đăng ký này"));

        registration.setStatus(status);
        ProjectRegistration savedReg = registrationRepository.save(registration);

        if ("APPROVED".equals(status)) {
            Project project = registration.getProject();

            long approvedCount = registrationRepository.countByProjectIdAndStatus(project.getId(), "APPROVED");

            if (approvedCount >= project.getTargetVolunteers()) {
                project.setStatus("ONGOING");
                projectRepository.save(project);
            }
        }

        return savedReg;
    }
}
