package com.group2.volunteer.service;

import com.group2.volunteer.dto.LoginRequest;
import com.group2.volunteer.dto.VolunteerProfileUpdateRequest;
import com.group2.volunteer.entity.User;

import java.util.List;

public interface UserService {
    User authenticate(LoginRequest loginRequest);

    void register(User user);

    List<User> findAllPendingUsers();

    void updateUserStatus(Long id, String status);
  
    User getUserById(Long userId);

    String getBadgeName(Long attendedProjectCount);

    List<User> getUsersByFilter(String keyword, String role, String status);

    void updateUserByAdmin(Long id, User userForm);

    User updateVolunteerProfile(Long userId, VolunteerProfileUpdateRequest profileRequest);
}
