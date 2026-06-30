package com.group2.volunteer.service;

import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.BadRequestException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectRegistrationRepository registrationRepository,
                              UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Project> getRecruitingProjects(String keyword, String location) {
        String searchTitle = (keyword != null && !keyword.isEmpty()) ? keyword : null;
        String searchLocation = (location != null && !location.isEmpty()) ? location : null;
        return projectRepository.searchVisibleProjects(searchTitle, searchLocation);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project createProject(ProjectDTO dto, Long organizerId) {
        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Ngày bắt đầu phải sau thời điểm hiện tại");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate().plusHours(1))) {
            throw new BadRequestException("Ngày kết thúc phải sau ngày bắt đầu ít nhất 1 giờ");
        }

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("Organizer không tồn tại"));

        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImageUrl(dto.getImageUrl());
        project.setLocation(dto.getLocation());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setTargetVolunteers(dto.getTargetVolunteers());
        project.setStatus(ProjectStatus.PENDING);
        project.setOrganizer(organizer);

        return projectRepository.save(project);
    }

    @Override
    public Project approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Dự án không tồn tại"));

        if (!ProjectStatus.PENDING.equals(project.getStatus())) {
            throw new BadRequestException("Chỉ có thể duyệt dự án đang ở trạng thái PENDING");
        }

        project.setStatus(ProjectStatus.RECRUITING);
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getPendingProjects() {
        return projectRepository.findByStatus(ProjectStatus.PENDING);
    }

    @Override
    public List<Project> getProjectsByOrganizer(Long organizerId, String keyword, String location) {
        String searchTitle = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String searchLocation = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        return projectRepository.searchProjectsByOrganizer(searchTitle, searchLocation, organizerId);
    }

    @Override
    public List<Project> getAllProject() {
        return projectRepository.searchVisibleProjects(null, null);
    }

    @Override
    public void applyForProject(Long projectId, Long userId) {
        if (registrationRepository.existsByVolunteerIdAndProjectId(userId, projectId)) {
            throw new BadRequestException("Bạn đã đăng ký dự án này rồi");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Dự án không tồn tại"));
        User volunteer = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer không tồn tại"));

        if (!"ROLE_VOLUNTEER".equals(volunteer.getRole())) {
            throw new BadRequestException("Chỉ tình nguyện viên mới có thể tham gia dự án");
        }
        if (!"ACTIVE".equals(volunteer.getStatus())) {
            throw new BadRequestException("Chỉ tài khoản ACTIVE mới có thể tham gia dự án");
        }

        if (!ProjectStatus.PLANNING.equals(project.getStatus()) && !ProjectStatus.RECRUITING.equals(project.getStatus())) {
            throw new BadRequestException("Dự án chỉ cho phép đăng ký ở trạng thái PLANNING hoặc RECRUITING");
        }

        long currentRegistrations = registrationRepository.countByProjectId(projectId);
        if (currentRegistrations >= project.getTargetVolunteers()) {
            throw new BadRequestException("Dự án đã đủ số lượng tình nguyện viên");
        }

        ProjectRegistration registration = new ProjectRegistration();
        registration.setVolunteer(volunteer);
        registration.setProject(project);
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus("PENDING");
        registration.setConfirmedHours(0);
        registrationRepository.save(registration);
    }
}