package com.group2.volunteer.service;

import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.repository.EventUpdateRepository;
import com.group2.volunteer.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventUpdateServiceImpl implements EventUpdateService {

    @Autowired
    private EventUpdateRepository eventUpdateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    @Transactional
    public EventUpdate createEventUpdate(Long projectId, String title, String content, String imageUrl) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Dự án không tồn tại"));
        EventUpdate eventUpdate = new EventUpdate();
        eventUpdate.setProject(project);
        eventUpdate.setTitle(title);
        eventUpdate.setContent(content);
        eventUpdate.setImageUrl(imageUrl);
        // createdAt sẽ được @PrePersist tự động set
        return eventUpdateRepository.save(eventUpdate);
    }

    @Override
    public List<EventUpdate> getEventUpdatesByProject(Long projectId) {
        return eventUpdateRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }
}