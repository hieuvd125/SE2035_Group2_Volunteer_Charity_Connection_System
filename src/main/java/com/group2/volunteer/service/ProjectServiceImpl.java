package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.UserRepository;
import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.constant.RegistrationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Project> getRecruitingProjects(String keyword, String location) {
        if ((keyword != null && !keyword.isEmpty()) || (location != null && !location.isEmpty())) {
            return projectRepository.findByTitleContainingAndLocationContainingAndStatus(keyword, location, ProjectStatus.RECRUITING);
        }
        return projectRepository.findByStatus(ProjectStatus.RECRUITING);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public List<Project> getAllProject() {
        // Gọi xuống Repository để lấy tất cả dự án trong database lên
        return projectRepository.findAll();
    }

    @Override
    public void applyForProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);

        if (project == null || !ProjectStatus.RECRUITING.equals(project.getStatus())) {
            throw new IllegalArgumentException("The current project is not open for volunteer recruitment!");
        }

        User volunteer = userRepository.findById(4L).orElse(null);
        if (volunteer == null) {
            throw new IllegalStateException("Test volunteer account with ID 4 not found!");
        }

        ProjectRegistration registration = new ProjectRegistration();
        registration.setVolunteer(volunteer);
        registration.setProject(project);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setConfirmedHours(0);
        registrationRepository.save(registration);
    }
}