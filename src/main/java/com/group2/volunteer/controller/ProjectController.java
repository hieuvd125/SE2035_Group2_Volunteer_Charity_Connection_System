package com.group2.volunteer.controller;

import com.group2.volunteer.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import com.group2.volunteer.dto.ProjectSearchCriteria;
import com.group2.volunteer.dto.RegistrationRequest;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.ProjectQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Controller
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectQueryService projectQueryService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/homepage")
    public String showHomepage(@ModelAttribute("criteria") ProjectSearchCriteria criteria, Model model) {
        List<Project> projects = projectQueryService.getAvailableProjects(criteria);
        model.addAttribute("projectList", projects);
        model.addAttribute("categories", categoryRepository.findAll());

        if (!model.containsAttribute("successMessage")) {
            model.addAttribute("successMessage", "");
        }
        return "common/homepage";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(@PathVariable Long id, HttpSession session, Model model) {
        Project project = projectQueryService.getProjectById(id);
        Long approvedCount = projectQueryService.getApprovedVolunteerCount(id);
        Long remainingSlot = project.getTargetVolunteers().longValue() - approvedCount;

        model.addAttribute("project", project);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("remainingSlot", remainingSlot);
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null) {
            boolean applied = projectQueryService.hasApplied(id, currentUser.getId());
            model.addAttribute("applied", applied);
        } else {
            model.addAttribute("applied", false);
        }
        return "project/project_detail";
    }

    @PostMapping("/apply")
    public String processApply(@RequestParam("projectId") Long projectId,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập trước khi đăng ký tham gia dự án!");
            return "redirect:/login";
        }

        Long currentUserId = sessionUser.getId();

        try {
            RegistrationRequest request = new RegistrationRequest(projectId, currentUserId);
            projectQueryService.applyToProject(request);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký tham gia thành công, vui lòng đợi duyệt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projects/detail/" + projectId;
        }

        return "redirect:/projects/homepage";
    }
}