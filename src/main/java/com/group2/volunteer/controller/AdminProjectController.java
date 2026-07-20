package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/admin/projects")
public class AdminProjectController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/review")
    public String reviewPendingProjects(
                                        @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                        HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            return "redirect:/login";
        }

        Page<Project> page = projectService.getPendingProjects(
                PageRequest.of(Math.max(pageNumber, 0), PAGE_SIZE));
        model.addAttribute("pendingProjects", page.getContent());
        model.addAttribute("reviewTotalPage", page.getTotalPages());
        model.addAttribute("reviewCurrentPage", page.getNumber());
        return "admin/admin_project_review";

    }

    @PostMapping("/{id}/approve")
    public String approveProject(@PathVariable Long id, RedirectAttributes redirectAttributes,
                                 HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
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
        if (user == null || !"ADMIN".equals(user.getRole())) {
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
