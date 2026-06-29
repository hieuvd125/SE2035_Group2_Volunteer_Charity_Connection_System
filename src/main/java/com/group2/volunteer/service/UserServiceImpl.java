package com.group2.volunteer.service;

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
}
