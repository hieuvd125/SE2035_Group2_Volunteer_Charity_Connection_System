package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/")
    public String showHomepage(Model model) {
        List<Project> projects =  projectService.getAllProject();
        model.addAttribute("projects", projects);

        return "common/homepage";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "common/login";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute LoginDTO loginDTO, HttpSession httpSession, Model model) {
        User user = userService.checkLogin(loginDTO.getUsername(), loginDTO.getPassword());

        if (user == null) {
            model.addAttribute("error", "Username or password is incorrect!");
            return "common/login";
        }

        if ("BLOCKED".equals(user.getStatus())) {
            model.addAttribute("error", "Your account is blocked!");
            return "common/login";
        }

        httpSession.setAttribute("loggedUser", user);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

}
