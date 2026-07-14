package com.group2.volunteer.service;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.dto.RegisterDTO;
import com.group2.volunteer.entity.User;

public interface UserService {
    User authenticate(LoginDTO loginDTO);
    Long register(RegisterDTO registerDTO);
  
    User getUserById(Long userId);

    String getBadgeName(Integer totalHours);
}
