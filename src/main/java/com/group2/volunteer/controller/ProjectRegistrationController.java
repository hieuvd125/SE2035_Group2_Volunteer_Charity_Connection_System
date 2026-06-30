package com.group2.volunteer.controller;

import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.ProjectRegistrationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/registrations")
public class ProjectRegistrationController {

    private final ProjectRegistrationService registrationService;

    public ProjectRegistrationController(ProjectRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/project/{projectId}")
    public String viewRegistrations(@PathVariable Long projectId, Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            return "redirect:/projects/detail/" + projectId + "?error=Only organizers can view registrations.";
        }

        List<ProjectRegistration> registrations = registrationService.getRegistrationsByProject(projectId);
        long activeRegistrations = registrationService.countActiveRegistrationsByProject(projectId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("activeRegistrations", activeRegistrations);
        model.addAttribute("projectId", projectId);
        return "project/registration_list";
    }

    @PostMapping("/{registrationId}/status")
    public String updateStatus(@PathVariable Long registrationId,
                               @RequestParam String status,
                               @RequestParam Long projectId,
                               HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            return "redirect:/projects/detail/" + projectId + "?error=Only organizers can update registrations.";
        }

        registrationService.updateRegistrationStatus(registrationId, status);
        return "redirect:/registrations/project/" + projectId;
    }
}
