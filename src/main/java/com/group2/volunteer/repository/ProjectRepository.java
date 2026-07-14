package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE p.status = 'RECRUITING' " +
            "AND (:location IS NULL OR p.location LIKE %:location%) " +
            "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Project> searchProjects(@Param("location") String location,
                                 @Param("categoryId") Long categoryId);
}