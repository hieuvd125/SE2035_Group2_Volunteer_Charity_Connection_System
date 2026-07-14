package com.group2.volunteer.service;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.SavedProject;

import java.util.List;

public interface SavedProjectService {
    void saveProject(Long volunteerId, Long projectId);
    void unsaveProject(Long volunteerId, Long projectId);
    List<SavedProject> getSavedProjectList(Long volunteerId);
    boolean isProjectSaved(Long volunteerId, Long projectId);
}
