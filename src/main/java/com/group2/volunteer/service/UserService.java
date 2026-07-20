package com.group2.volunteer.service;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.entity.User;

import java.util.List;

public interface UserService {
    User authenticate(LoginDTO loginDTO);

    void register(User user);

    List<User> findAllPendingUsers();

    void updateUserStatus(Long id, String status);
  
    User getUserById(Long userId);

    String getBadgeName(Integer totalHours);
}
