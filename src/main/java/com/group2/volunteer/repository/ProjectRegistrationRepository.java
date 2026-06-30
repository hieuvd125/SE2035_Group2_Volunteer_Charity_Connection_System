package com.group2.volunteer.repository;

import com.group2.volunteer.entity.ProjectRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRegistrationRepository extends JpaRepository<ProjectRegistration, Long> {

    boolean existsByVolunteerIdAndProjectId(Long volunteerId, Long projectId);

    long countByProjectIdAndStatus(Long projectId, String status);

    @Query("SELECT COALESCE(SUM(pr.confirmedHours), 0) FROM ProjectRegistration pr " +
            "WHERE pr.volunteer.id = :volunteerId AND pr.status = 'ATTENDED'")
    Integer sumConfirmedHoursByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT pr FROM ProjectRegistration pr " +
            "JOIN FETCH pr.volunteer v " +
            "JOIN FETCH pr.project p " +
            "WHERE p.id = :projectId")
    List<ProjectRegistration> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(pr) FROM ProjectRegistration pr WHERE pr.project.id = :projectId AND pr.status <> 'REJECTED'")
    long countActiveRegistrationsByProjectId(@Param("projectId") Long projectId);

    long countByProjectId(Long projectId);
}
