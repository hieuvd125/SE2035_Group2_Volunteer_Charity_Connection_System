package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.SavedProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SavedProjectServiceImpl implements SavedProjectService {

    private final SavedProjectRepository savedProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public SavedProjectServiceImpl(SavedProjectRepository savedProjectRepository,
                                   UserRepository userRepository,
                                   ProjectRepository projectRepository) {
        this.savedProjectRepository = savedProjectRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public String saveProject(Long userId, Long projectId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));

        if (savedProjectRepository.existsByVolunteerIdAndProjectId(userId, projectId)) {
            return "Bạn đã lưu dự án này rồi!";
        }

        SavedProject savedProject = new SavedProject();
        savedProject.setVolunteer(user);
        savedProject.setProject(project);
        savedProject.setSavedAt(LocalDateTime.now());
        savedProjectRepository.save(savedProject);
        return "Lưu dự án thành công!";
    }

    @Override
    @Transactional
    public String unsaveProject(Long userId, Long projectId) {
        Optional<SavedProject> saved = savedProjectRepository
                .findByVolunteerIdAndProjectId(userId, projectId);
        if (saved.isPresent()) {
            savedProjectRepository.delete(saved.get());
            return "Đã bỏ lưu dự án.";
        }
        return "Bạn chưa lưu dự án này.";
    }

    @Override
    public boolean isProjectSaved(Long userId, Long projectId) {
        return savedProjectRepository.existsByVolunteerIdAndProjectId(userId, projectId);
    }
    @Override
    public List<Long> getSavedProjectIds(Long userId) {
        return savedProjectRepository.findByVolunteerId(userId)
                .stream()
                .map(sp -> sp.getProject().getId())
                .collect(Collectors.toList());
    }
    @Override
    public List<SavedProject> getSavedProjectsByUserId(Long userId) {
        return savedProjectRepository.findByVolunteerId(userId);
    }
}