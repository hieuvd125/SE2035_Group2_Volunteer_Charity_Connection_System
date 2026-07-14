package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.SaveProjectException;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.SavedProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SavedProjectServiceImpl implements SavedProjectService{
    private final SavedProjectRepository savedProjectRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Override
    public void saveProject(Long volunteerId, Long projectId) {
    if(savedProjectRepository.existsByVolunteerIdAndProjectId(volunteerId,projectId)){
        throw new SaveProjectException("Bạn đã lưu dự án này rồi");
    }else{
        User volunteer = userRepository.findById(volunteerId).orElseThrow(() -> new  IllegalArgumentException("Không tìm thấy tài khoản trong hệ thống"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án trong hệ thống"));
        SavedProject savedProject = new SavedProject();
        savedProject.setVolunteer(volunteer);
        savedProject.setProject(project);
        savedProjectRepository.save(savedProject);


    }
    }

    @Override
    public void unsaveProject(Long volunteerId, Long projectId) {
        if(!savedProjectRepository.existsByVolunteerIdAndProjectId(volunteerId,projectId)){
            throw new SaveProjectException("Bạn chưa lưu dự án này");
        }else{
            SavedProject savedProject = savedProjectRepository.findByVolunteerIdAndProjectId(volunteerId,projectId).orElseThrow(() -> new IllegalArgumentException("Không có dự án đã lưu để hủy"));
            savedProjectRepository.delete(savedProject);
        }
    }

    @Override
    public List<SavedProject> getSavedProjectList(Long volunteerId) {
        List<SavedProject> list = savedProjectRepository.findByVolunteerId(volunteerId);
        return list;
    }

    @Override
    public boolean isProjectSaved(Long volunteerId, Long projectId) {
        if(savedProjectRepository.existsByVolunteerIdAndProjectId(volunteerId,projectId)){
            return true;
        }
        return false;
    }
}
