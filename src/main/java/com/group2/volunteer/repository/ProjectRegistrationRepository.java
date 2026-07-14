package com.group2.volunteer.repository;

import com.group2.volunteer.entity.ProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {
    List<ProjectRegistration> findByVolunteerId(Long volunteerId);

    List<ProjectRegistration> findByStatus(String status);

    Long countByProjectIdAndStatus(Long projectId, String status);

    boolean existsByProjectIdAndVolunteerId(Long projectId, Long volunteerId);
}
