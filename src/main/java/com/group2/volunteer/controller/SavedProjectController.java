package com.group2.volunteer.controller;

import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.SavedProjectService;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/projects/saved")
public class SavedProjectController {

    private final SavedProjectService savedProjectService;

    public SavedProjectController(SavedProjectService savedProjectService) {
        this.savedProjectService = savedProjectService;
    }

    @GetMapping
    public String listSavedProjects(Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";

        List<SavedProject> savedProjects = savedProjectService.getSavedProjectsByUserId(loggedUser.getId());
        model.addAttribute("savedProjects", savedProjects);
        model.addAttribute("currentUserId", loggedUser.getId());
        return "project/saved_projects";
    }

    @PostMapping("/add")
    public String saveProject(@RequestParam("projectId") Long projectId, HttpSession session, HttpServletRequest request) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            savedProjectService.saveProject(loggedUser.getId(), projectId);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/remove")
    public String unsaveProject(@RequestParam("projectId") Long projectId, HttpSession session, HttpServletRequest request) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null) {
            savedProjectService.unsaveProject(loggedUser.getId(), projectId);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}