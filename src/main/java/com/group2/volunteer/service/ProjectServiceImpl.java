package com.group2.volunteer.service;

import com.group2.volunteer.dto.ProjectDTO; // chúng ta sẽ tạo DTO sau
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.UserRepository;
import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.constant.RegistrationStatus;
import com.group2.volunteer.exception.BadRequestException;
import com.group2.volunteer.exception.ResourceNotFoundException;
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
    public Project createProject(ProjectDTO dto, Long organizerId) {
        // 1. Validate ngày
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Ngày bắt đầu phải sau thời điểm hiện tại");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate().plusHours(1))) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu ít nhất 1 giờ");
        }

        // 2. Tìm organizer (tạm thời giả định user tồn tại)
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer không tồn tại"));

        // 3. Tạo entity Project
        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImageUrl(dto.getImageUrl());
        project.setLocation(dto.getLocation());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTargetVolunteers(dto.getTargetVolunteers());
        project.setStatus("PENDING"); // mặc định
        project.setOrganizer(organizer);


        return projectRepository.save(project);
    }

    @Override
    public Project approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Dự án không tồn tại"));

        if (!"PENDING".equals(project.getStatus())) {
            throw new BadRequestException("Chỉ có thể duyệt dự án đang ở trạng thái PENDING");
        }

        project.setStatus("RECRUITING");
        return projectRepository.save(project);
    }


    @Override
    public List<Project> getPendingProjects() {
        return projectRepository.findByStatus("PENDING");
    }

    @Override
    public List<Project> getProjectsByOrganizer(Long organizerId) {
        return projectRepository.findByOrganizerId(organizerId);
    }

    @Override
    public List<Project> getAllProject() {

        return projectRepository.findAll();
    }

    @Override
    public void applyForProject(Long projectId, Long userId) {
        if (registrationRepository.existsByVolunteerIdAndProjectId(userId, projectId)) {
            throw new BadRequestException("Bạn đã đăng ký dự án này rồi");
        }

        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFoundException("Dự án không tồn tại"));

        if (!ProjectStatus.RECRUITING.equals(project.getStatus())) {
            throw new BadRequestException("The current project is not open for volunteer recruitment!");
        }

        long currentRegistrations = registrationRepository.countByProjectId(projectId);
        if (currentRegistrations >= project.getTargetVolunteers()) {
            throw new BadRequestException("Dự án đã đủ số lượng tình nguyện viên");
        }

        User volunteer = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Volunteer không tồn tại"));

        ProjectRegistration registration = new ProjectRegistration();
        registration.setVolunteer(volunteer);
        registration.setProject(project);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(RegistrationStatus.PENDING);
        registration.setConfirmedHours(0);
        registrationRepository.save(registration);
    }
}