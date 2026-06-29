package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String redirecToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "common/login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginDTO loginDTO, HttpSession httpSession, Model model) {
        User user = userService.checkLogin(loginDTO.getUsername(), loginDTO.getPassword());

        if (user != null) {
            httpSession.setAttribute("loggedUser", user);

            return "redirect:/home";
        } else {
            model.addAttribute("error", "Username or password is incorrect!");
            return "common/login";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String showVolunteerHomepage(HttpSession session) {
        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/login";
        }
        return "common/homepage";
    }
}
