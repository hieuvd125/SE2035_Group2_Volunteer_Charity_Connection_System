package com.group2.volunteer.service;

import com.group2.volunteer.entity.User;

public interface UserService {
    User getUserById(Long userId);

    String getBadgeName(Integer totalHours);
}
