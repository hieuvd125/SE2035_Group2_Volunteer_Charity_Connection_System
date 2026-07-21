package com.group2.volunteer.service;

import com.group2.volunteer.dto.LoginRequest;
import com.group2.volunteer.dto.OrganizerProfileDTO;
import com.group2.volunteer.entity.User;

import java.util.List;

public interface UserService {
    User authenticate(LoginRequest loginRequest);

    void register(User user);

    List<User> findAllPendingUsers();

    void updateUserStatus(Long id, String status);
  
    User getUserById(Long userId);

    String getBadgeName(Integer totalHours);

    List<User> getUsersByFilter(String keyword, String role, String status);

    void updateUserByAdmin(Long id, User userForm);

    OrganizerProfileDTO getOrganizerProfileDTO(Long userId);

    void updateOrganizerProfile(Long userId, OrganizerProfileDTO profileDTO);
}
