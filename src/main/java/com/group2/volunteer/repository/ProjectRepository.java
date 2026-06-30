package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // SỬA LẠI: Tìm kiếm linh hoạt theo Vùng miền / Tiêu đề (nếu null hoặc trống thì bỏ qua)
    @Query("SELECT p FROM Project p WHERE p.status = :status " +
            "AND (:title IS NULL OR p.title LIKE %:title%) " +
            "AND (:location IS NULL OR p.location LIKE %:location%)")
    List<Project> searchProjects(@Param("title") String title,
                                 @Param("location") String location,
                                 @Param("status") String status);

    List<Project> findByStatus(String status);
    List<Project> findByOrganizerId(Long organizerId);
}
