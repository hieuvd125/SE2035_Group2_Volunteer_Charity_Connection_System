package com.group2.volunteer.repository;

import com.group2.volunteer.entity.SavedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedProjectRepository extends JpaRepository<SavedProject, Long> {

    @Query("SELECT s FROM SavedProject s WHERE s.volunteer.id = :volunteerId")
    List<SavedProject> findByVolunteerId(@Param("volunteerId") Long volunteerId);

    @Query("SELECT COUNT(s) > 0 FROM SavedProject s WHERE s.volunteer.id = :volunteerId AND s.project.id = :projectId")
    boolean existsByVolunteerIdAndProjectId(@Param("volunteerId") Long volunteerId, @Param("projectId") Long projectId);

    @Query("SELECT s FROM SavedProject s WHERE s.volunteer.id = :volunteerId AND s.project.id = :projectId")
    Optional<SavedProject> findByVolunteerIdAndProjectId(@Param("volunteerId") Long volunteerId, @Param("projectId") Long projectId);

    long countByProjectId(Long projectId);
}