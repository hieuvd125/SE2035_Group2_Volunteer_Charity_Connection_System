package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping("/review")
    public String reviewPendingProjects(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        Long currentUserId = user.getId();
        String role = user.getRole();
        if(!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Project> pendingProjects = projectService.getPendingProjects(pageable);
        model.addAttribute("pendingProjects", pendingProjects);
        return "admin/admin_project_review";

    }

    @PostMapping("/{id}/approve")
    public String approveProject(@PathVariable Long id, RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long currentUserId = user.getId();
        String role = user.getRole();
        if(!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            projectService.approveProject(id);
            redirectAttributes.addFlashAttribute("message", "Dự án đã được phê duyệt");
        } catch (InvalidProjectStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/projects/review";
    }

    @PostMapping("/{id}/reject")
    public String rejectProject(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long currentUserId = user.getId();
        String role = user.getRole();
        if(!"ADMIN".equals(role)) {
            return "redirect:/login";
        }
        try {
            projectService.rejectProject(id);
            redirectAttributes.addFlashAttribute("message", "Dự án đã bị từ chối");
        }catch(InvalidProjectStateException e){
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/projects/review";

    }
}