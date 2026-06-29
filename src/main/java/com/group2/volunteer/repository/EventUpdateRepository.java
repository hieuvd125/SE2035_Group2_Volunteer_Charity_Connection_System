package com.group2.volunteer.repository;

import com.group2.volunteer.entity.EventUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventUpdateRepository extends JpaRepository<EventUpdate, Long> {

    List<EventUpdate> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}