package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        long totalVolunteers = userRepository.countByRole("VOLUNTEER");
        long totalOrganizers = userRepository.countByRole("ORGANIZER");
        long totalUsers = totalVolunteers + totalOrganizers;

        long pendingUsers = userRepository.countByStatus("PENDING");
        long pendingProjectCount = projectRepository.countByStatus("PENDING");
        long totalProjects = projectRepository.count();
        long totalCategories = categoryRepository.count();

        List<Project> pendingProjectsList = projectRepository.findByStatus("PENDING");

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("pendingProjectCount", pendingProjectCount);
        model.addAttribute("pendingUserCount", pendingUsers);
        model.addAttribute("totalCategories", totalCategories);

        model.addAttribute("pendingProjects", pendingProjectsList);

        return "admin/dashboard";
    }
}