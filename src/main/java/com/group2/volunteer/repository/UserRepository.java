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

    @Query("FROM User u WHERE u.email = :email AND u.password = :password")
    Optional<User> findByEmailAndPass(@Param("email") String email, @Param("password") String password);

    List<User> findByStatus(String status);
}
