package com.group2.volunteer.service;

import com.group2.volunteer.entity.EventUpdate;
import java.util.List;

public interface EventUpdateService {
    EventUpdate createEventUpdate(Long projectId, String title, String content, String imageUrl);
    List<EventUpdate> getEventUpdatesByProject(Long projectId);
}