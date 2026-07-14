package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(@RequestParam(required = false) Long userId, Model model) {
        model.addAttribute("userId", userId);

        if (userId != null) {
            User user = userService.getUserById(userId);
            model.addAttribute("user", user);
            model.addAttribute("badgeName", userService.getBadgeName(user.getTotalHours()));
        }

        return "volunteer/profile";
    }
}
