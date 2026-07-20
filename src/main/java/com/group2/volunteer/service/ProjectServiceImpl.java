package com.group2.volunteer.service;

import com.group2.volunteer.dto.ProjectCreationDTO;
import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.entity.Category;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.repository.CategoryRepository;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.entity.Project;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Project createProject(ProjectCreationDTO dto, Long organizerId) {
        if(dto.getEndDate().isBefore(dto.getStartDate()) || dto.getEndDate().isEqual(dto.getStartDate())) {
            throw new InvalidProjectStateException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        User organizer = userRepository.findById(organizerId).orElseThrow(() -> new RuntimeException("Không tìm thấy organizer"));

        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new RuntimeException("Không tìm thấy category"));

        Project project = new Project();
        project.setTitle(dto.getTitle());
        project.setDescription(dto.getDescription());
        project.setImageUrl(dto.getImageUrl());
        project.setLocation(dto.getLocation());
        project.setStartDate(dto.getStartDate().atStartOfDay());
        project.setEndDate(dto.getEndDate().atStartOfDay());
        project.setTargetVolunteers(dto.getTargetVolunteers());
        project.setTargetDonation(dto.getTargetDonation());
        project.setOrganizer(organizer);
        project.setCategory(category);
        project.setStatus("PENDING");

        return projectRepository.save(project);

    }

    @Override
    public void approveProject(Long projectId) {
        Project project = getProjectById(projectId);
        if(!"PENDING".equals(project.getStatus())) {
            throw new InvalidProjectStateException("Dự án không ở trạng thái 'PENDING'");
        }
        project.setStatus("RECRUITING");
        projectRepository.save(project);
    }

    @Override
    public void rejectProject(Long projectId) {
        Project project = getProjectById(projectId);
        if(!"PENDING".equals(project.getStatus())) {
            throw new InvalidProjectStateException("Dự án không ở trạng thái 'PENDING'");
        }
        project.setStatus("REJECTED");
        projectRepository.save(project);

    }

    @Override
    public void closeRecruitment(Long projectId, Long organizerId) {
        Project project = getProjectById(projectId);
        if (project.getOrganizer() == null || !project.getOrganizer().getId().equals(organizerId)) {
            throw new InvalidProjectStateException("You are not allowed to close this project recruitment");
        }
        if (!ProjectStatus.RECRUITING.equals(project.getStatus())) {
            throw new InvalidProjectStateException("Project is not recruiting");
        }
        project.setStatus(ProjectStatus.RECRUITMENT_CLOSED);
        projectRepository.save(project);
    }

    @Override
    public Page<Project> getPendingProjects(Pageable pageable) {
        return projectRepository.findByStatus("PENDING", pageable);
    }

    @Override
    public Page<Project> getOrganizerProjects(Long organizerId, String title, String location, String status, Pageable pageable) {
        if(title != null && title.trim().isEmpty())
            title = null;
        if(location != null && location.trim().isEmpty())
            location = null;
        if(status != null && status.trim().isEmpty())
            status = null;
        return projectRepository.findOrganizerProjects(organizerId, title, location, status, pageable);
    }

    @Override
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));
    }

    @Override
    public List<Project> getAvailableProjects(ProjectSearchCriteria criteria) {
        String title = (criteria.getTitle() == null || criteria.getTitle().trim().isEmpty()) ? null : criteria.getTitle().trim();
        String loc = (criteria.getLocation() == null || criteria.getLocation().trim().isEmpty()) ? null : criteria.getLocation().trim();

        return projectRepository.searchProjects(title, loc, criteria.getCategoryId());
    }
}
