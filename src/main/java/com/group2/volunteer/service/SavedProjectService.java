package com.group2.volunteer.service;

import com.group2.volunteer.entity.SavedProject;

import java.util.List;

public interface SavedProjectService {
    String saveProject(Long userId, Long projectId);
    String unsaveProject(Long userId, Long projectId);
    boolean isProjectSaved(Long userId, Long projectId);
    List<Long> getSavedProjectIds(Long userId);
    List<SavedProject> getSavedProjectsByUserId(Long userId);
}