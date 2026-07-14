package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return "redirect:/login";
        }

        User user = userService.getUserById(sessionUser.getId());
        model.addAttribute("user", user);
        model.addAttribute("badgeName", userService.getBadgeName(user.getTotalHours()));

        return "volunteer/profile";
    }
}
