package com.group2.volunteer.service;

import lombok.RequiredArgsConstructor;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.dto.RegistrationRequest;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryServiceImpl implements ProjectQueryService {

    private final ProjectRepository projectRepository;
    private final ProjectRegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    @Override
    public List<Project> getAvailableProjects(ProjectSearchCriteria criteria) {
        String loc = (criteria.getLocation() == null || criteria.getLocation().isEmpty()) ? null : criteria.getLocation();
        return projectRepository.searchProjects(loc, criteria.getCategoryId());
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án với ID: " + id));
    }

    @Override
    @Transactional
    public void applyToProject(RegistrationRequest request) throws Exception {
        Project project = getProjectById(request.getProjectId());

        if (!"RECRUITING".equals(project.getStatus())) {
            throw new Exception("Dự án hiện chưa mở đăng ký.");
        }

        if (registrationRepository.existsByProjectIdAndVolunteerId(request.getProjectId(), request.getUserId())) {
            throw new Exception("Bạn đã nộp đơn đăng ký tham gia dự án này rồi, không thể nộp lại!");
        }

        Long currentVolunteers = registrationRepository.countByProjectIdAndStatus(request.getProjectId(), "APPROVED");
        if (project.getTargetVolunteers() != null && currentVolunteers >= project.getTargetVolunteers()) {
            throw new Exception("Dự án tình nguyện này đã hết slot đăng ký trống!");
        }

        ProjectRegistration reg = new ProjectRegistration();
        reg.setProject(project);

        User volunteer = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản Tình nguyện viên này!"));
        if (!"VOLUNTEER".equalsIgnoreCase(volunteer.getRole())) {
            throw new Exception("Chỉ tài khoản Tình nguyện viên mới được đăng ký tham gia dự án.");
        }
        if (!"ACTIVE".equalsIgnoreCase(volunteer.getStatus())) {
            throw new Exception("Tài khoản của bạn chưa được kích hoạt hoặc đã bị khóa.");
        }
        reg.setVolunteer(volunteer);

        reg.setStatus("PENDING");
        reg.setConfirmedHours(0);

        registrationRepository.save(reg);
    }
    @Override
    public Long getApprovedVolunteerCount(Long projectId) {

        return registrationRepository.countByProjectIdAndStatus(projectId, "APPROVED");

    }
    @Override
    public boolean hasApplied(Long projectId, Long userId) {

        return registrationRepository
                .existsByProjectIdAndVolunteerId(projectId, userId);

    }
}