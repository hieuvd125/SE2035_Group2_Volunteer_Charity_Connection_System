package com.group2.volunteer.service;

import com.group2.volunteer.dto.ProjectDTO; // chúng ta sẽ tạo DTO sau
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User; // giả sử có entity User
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.UserRepository;
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
    private UserRepository userRepository; // cần để lấy organizer

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
        // Nếu có Category, bạn cần set category sau khi lấy từ DB (tạm thời để null hoặc thêm sau)
        // project.setCategory(...);

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

    // Các method khác nếu cần, ví dụ lấy danh sách PENDING
    @Override
    public List<Project> getPendingProjects() {
        return projectRepository.findByStatus("PENDING");
    }

    @Override
    public List<Project> getProjectsByOrganizer(Long organizerId) {
        return projectRepository.findByOrganizerId(organizerId);
    }
}