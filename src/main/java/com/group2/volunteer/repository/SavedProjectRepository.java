package com.group2.volunteer.repository;

import com.group2.volunteer.entity.SavedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedProjectRepository extends JpaRepository<SavedProject, Long> {


    List<SavedProject> findByVolunteerId(Long volunteerId);


    boolean existsByVolunteerIdAndProjectId(Long volunteerId, Long projectId);


    Optional<SavedProject> findByVolunteerIdAndProjectId(Long volunteerId, Long projectId);


    long countByProjectId(Long projectId);
}