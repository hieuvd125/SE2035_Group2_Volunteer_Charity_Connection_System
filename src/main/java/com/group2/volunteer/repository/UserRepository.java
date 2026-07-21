package com.group2.volunteer.repository;

import com.group2.volunteer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPass(@Param("username") String username, @Param("password") String password);

    List<User> findByStatus(String status);

    @Query("SELECT u FROM User u WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:role IS NULL OR :role = '' OR u.role = :role) AND " +
            "(:status IS NULL OR :status = '' OR u.status = :status)")
    List<User> getUsersByFilter(@Param("keyword") String keyword,
                                @Param("role") String role,
                                @Param("status") String status);

    long countByRole(String role);
}
