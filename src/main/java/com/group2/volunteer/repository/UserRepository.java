package com.group2.volunteer.repository;

import com.group2.volunteer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Có thể thêm các method tìm kiếm tùy chỉnh ở đây nếu cần
    // Ví dụ: User findByUsername(String username);
    Optional<User> findByUsername(String username);
}
