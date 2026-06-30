package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.status <> 'PENDING' " +
            "AND (:title IS NULL OR p.title LIKE %:title%) " +
            "AND (:location IS NULL OR p.location LIKE %:location%)")
    List<Project> searchVisibleProjects(@Param("title") String title,
                                        @Param("location") String location);

    List<Project> findByStatus(String status);
    List<Project> findByOrganizerId(Long organizerId);

    @Query("SELECT p FROM Project p WHERE p.organizer.id = :organizerId " +
            "AND (:title IS NULL OR p.title LIKE %:title%) " +
            "AND (:location IS NULL OR p.location LIKE %:location%)")
    List<Project> searchProjectsByOrganizer(@Param("title") String title,
                                            @Param("location") String location,
                                            @Param("organizerId") Long organizerId);
}
