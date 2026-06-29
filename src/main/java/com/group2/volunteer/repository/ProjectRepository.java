package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByTitleContainingAndLocationContainingAndStatus(String title, String location, String status);
    // Tìm tất cả dự án theo status (dùng cho admin duyệt)
    List<Project> findByStatus(String status);

    // Tìm tất cả dự án của một Organizer
    List<Project> findByOrganizerId(Long organizerId);

    // Có thể thêm các method khác nếu cần
}
