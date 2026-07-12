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
        reg.setVolunteer(volunteer);

        reg.setStatus("PENDING");
        reg.setRegistrationDate(java.time.LocalDateTime.now());
        reg.setConfirmedHours(0);

        registrationRepository.save(reg);
    }
}