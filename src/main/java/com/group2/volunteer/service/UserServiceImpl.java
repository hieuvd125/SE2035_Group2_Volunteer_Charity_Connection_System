package com.group2.volunteer.service;

import com.group2.volunteer.dto.RegisterDTO;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkLogin(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if(userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt.get();
        }
        return null;
    }

    @Override
    public void registerVolunteer(RegisterDTO dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại trên hệ thống!");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        user.setRole("ROLE_VOLUNTEER");
        user.setStatus("ACTIVE");

        userRepository.save(user);
    }
}
