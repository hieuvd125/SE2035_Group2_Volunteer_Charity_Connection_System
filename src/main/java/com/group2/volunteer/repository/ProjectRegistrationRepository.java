package com.group2.volunteer.repository;

import com.group2.volunteer.entity.ProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {
    boolean existsByVolunteerIdAndProjectId(Long volunteerId, Long projectId);
    long countByProjectId(Long projectId);
}
