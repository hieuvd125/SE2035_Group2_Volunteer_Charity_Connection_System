package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.dto.RegisterDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.SavedProjectService;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SavedProjectService savedProjectService;

    @GetMapping("/")
    public String showHomepage(Model model, HttpSession session) {
        List<Project> projects = projectService.getAllProject();
        model.addAttribute("projects", projects);

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            model.addAttribute("currentUserId", loggedUser.getId());
            List<Long> savedProjectIds = savedProjectService.getSavedProjectIds(loggedUser.getId());
            model.addAttribute("savedProjectIds", savedProjectIds);
        } else {
            model.addAttribute("currentUserId", null);
            model.addAttribute("savedProjectIds", new ArrayList<>());
        }

        return "common/homepage";
    }

    @GetMapping("/register/volunteer")
    public String regVolunteerForm(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "common/register_volunteer";
    }

    @PostMapping("/register/volunteer")
    public String saveVolunteer(@Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "common/register_volunteer";
        }

        try {
            userService.registerVolunteer(registerDTO);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "common/register_volunteer";
        }

        return "redirect:/login?success=RegisterSuccess";
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

        if ("PENDING".equals(user.getStatus())) {
            model.addAttribute("error", "Your account is not verified!");
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