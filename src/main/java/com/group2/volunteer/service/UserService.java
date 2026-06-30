package com.group2.volunteer.service;

import com.group2.volunteer.dto.RegisterDTO;
import com.group2.volunteer.entity.User;

public interface UserService {
    User checkLogin(String username, String password);
    void registerVolunteer(RegisterDTO dto);
}
