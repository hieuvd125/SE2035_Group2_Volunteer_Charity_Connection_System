package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByStatus(String status);

    List<Project> findByTitleContainingAndLocationContainingAndStatus(String title, String location, String status);
}
