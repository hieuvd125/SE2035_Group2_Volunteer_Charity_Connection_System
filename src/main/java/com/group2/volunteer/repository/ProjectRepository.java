package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("Select p From Project p Where p.organizer.id = :orgId " +
            "And (:title Is Null Or :title = '' Or Lower(p.title) like Lower(Concat('%', :title, '%'))) " +
            "And (:location Is Null Or :location = '' or lower(p.location) like lower(concat('%', :location, '%'))) "+
            "And (:status Is Null or :status = '' or p.status = :status)")
    Page<Project> findOrganizerProjects(@Param("orgId") long orgId, @Param("title") String title,
                                        @Param("location") String location, @Param("status") String status, Pageable pageable);

    Page<Project> findByStatus(String status, Pageable pageable);
}
