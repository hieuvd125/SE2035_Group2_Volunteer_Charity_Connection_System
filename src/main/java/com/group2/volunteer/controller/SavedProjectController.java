package com.group2.volunteer.controller;

import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.SavedProjectService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/projects")
public class SavedProjectController {

    @Autowired
    private SavedProjectService savedProjectService;

    private void checkCanSaveProjectAccess(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || (!"ROLE_VOLUNTEER".equals(loggedUser.getRole()) && !"ROLE_ORGANIZER".equals(loggedUser.getRole()))) {
            throw new com.group2.volunteer.exception.BadRequestException("Vui lòng đăng nhập bằng tài khoản Tình nguyện viên hoặc Organizer để thực hiện");
        }
    }

    @GetMapping("/saved")
    public String listSavedProjects(Model model, HttpSession session) {
        checkCanSaveProjectAccess(session);
        User loggedUser = (User) session.getAttribute("loggedUser");
        List<SavedProject> savedProjects = savedProjectService.getSavedProjectsByUserId(loggedUser.getId());
        model.addAttribute("savedProjects", savedProjects);
        model.addAttribute("currentUserId", loggedUser.getId());
        return "project/saved_projects";
    }

    @PostMapping("/save")
    public String saveProject(@RequestParam("projectId") Long projectId,
                              HttpSession session,
                              jakarta.servlet.http.HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            checkCanSaveProjectAccess(session);
            User loggedUser = (User) session.getAttribute("loggedUser");
            String message = savedProjectService.saveProject(loggedUser.getId(), projectId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/unsave")
    public String unsaveProject(@RequestParam("projectId") Long projectId,
                                HttpSession session,
                                jakarta.servlet.http.HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        try {
            checkCanSaveProjectAccess(session);
            User loggedUser = (User) session.getAttribute("loggedUser");
            String message = savedProjectService.unsaveProject(loggedUser.getId(), projectId);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}
