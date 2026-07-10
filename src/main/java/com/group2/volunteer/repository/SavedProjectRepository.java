package com.group2.volunteer.repository;

import com.group2.volunteer.entity.SavedProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedProjectRepository extends JpaRepository<SavedProject, Long> {

}