package com.group2.volunteer.controller;

import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.EventUpdateService;
import com.group2.volunteer.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/projects")
public class EventUpdateController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EventUpdateService eventUpdateService;

    @GetMapping("/detail/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpSession session) {
        // Kiểm tra đăng nhập
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/projects/detail/" + id + "?error=Please login first.";
        }
        // Chỉ ROLE_ORGANIZER mới được đăng
        if (!"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            return "redirect:/projects/detail/" + id + "?error=Only organizers can post updates.";
        }

        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        // Kiểm tra có phải organizer của dự án không
        if (!project.getOrganizer().getId().equals(loggedUser.getId())) {
            return "redirect:/projects/detail/" + id + "?error=You are not the organizer of this project.";
        }

        model.addAttribute("project", project);
        model.addAttribute("eventUpdate", new EventUpdate());
        return "project/event_update_form";
    }

    @PostMapping("/detail/{id}/update")
    public String postUpdate(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(required = false) String imageUrl,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            redirectAttributes.addFlashAttribute("error", "Please login first.");
            return "redirect:/projects/detail/" + id;
        }
        if (!"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Only organizers can post updates.");
            return "redirect:/projects/detail/" + id;
        }

        Project project = projectService.getProjectById(id);
        if (project == null) {
            redirectAttributes.addFlashAttribute("error", "Project not found.");
            return "redirect:/projects/detail/" + id;
        }
        if (!project.getOrganizer().getId().equals(loggedUser.getId())) {
            redirectAttributes.addFlashAttribute("error", "You are not the organizer of this project.");
            return "redirect:/projects/detail/" + id;
        }

        eventUpdateService.createEventUpdate(id, title, content, imageUrl);
        redirectAttributes.addFlashAttribute("message", "Đăng cập nhật thành công!");
        return "redirect:/projects/detail/" + id;
    }
}
