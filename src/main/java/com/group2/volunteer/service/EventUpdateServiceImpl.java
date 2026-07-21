package com.group2.volunteer.service;

import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.repository.EventUpdateRepository;
import com.group2.volunteer.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventUpdateServiceImpl implements EventUpdateService {

    private final EventUpdateRepository eventUpdateRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<EventUpdate> getEventUpdatesByProjectId(Long projectId) {
        return eventUpdateRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    @Override
    public EventUpdate createEventUpdate(Long projectId, String title, String content, String imageUrl) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dự án trong hệ thống"));

        EventUpdate update = new EventUpdate();
        update.setProject(project);
        update.setTitle(title);
        update.setContent(content);
        update.setImageUrl(imageUrl);
        return eventUpdateRepository.save(update);
    }
}