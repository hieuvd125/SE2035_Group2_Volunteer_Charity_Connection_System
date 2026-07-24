package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long totalVolunteers = userRepository.countByRole("VOLUNTEER");
        long totalOrganizers = userRepository.countByRole("ORGANIZER");
        long totalUsers = totalVolunteers + totalOrganizers; // Hoặc userRepository.count();

        long pendingProjectCount = projectRepository.countByStatus("PENDING");
        long totalProjects = projectRepository.count();

        List<Project> pendingProjectsList = projectRepository.findByStatus("PENDING");

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingProjectCount", pendingProjectCount);
        model.addAttribute("pendingUserCount", 0);
        model.addAttribute("totalCategories", 0);

        model.addAttribute("pendingProjects", pendingProjectsList);

        return "admin/dashboard";
    }
}