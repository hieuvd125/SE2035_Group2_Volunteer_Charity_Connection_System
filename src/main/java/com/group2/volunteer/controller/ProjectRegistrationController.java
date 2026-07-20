package com.group2.volunteer.controller;

import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.service.ProjectRegistrationService;
import com.group2.volunteer.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/organizer/projects/{projectId}/registrations")
@RequiredArgsConstructor
public class ProjectRegistrationController {
    private final ProjectRegistrationService registrationService;
    private final ProjectService projectService;

    @GetMapping
    public String listRegistrations(@PathVariable Long projectId, HttpSession session, Model model) {
        User organizer = getOrganizer(session);
        if (organizer == null) {
            return "redirect:/login";
        }

        List<ProjectRegistration> registrations = registrationService
                .getProjectRegistrations(projectId, organizer.getId());
        model.addAttribute("project", projectService.getProjectById(projectId));
        model.addAttribute("registrations", registrations);
        return "organizer/registration_list";
    }

    @PostMapping("/{registrationId}/approve")
    public String approve(@PathVariable Long projectId,
                          @PathVariable Long registrationId,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User organizer = getOrganizer(session);
        if (organizer == null) {
            return "redirect:/login";
        }

        try {
            registrationService.approveRegistration(projectId, registrationId, organizer.getId());
            redirectAttributes.addFlashAttribute("message", "Applicant approved successfully");
        } catch (InvalidProjectStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectToList(projectId);
    }

    @PostMapping("/{registrationId}/reject")
    public String reject(@PathVariable Long projectId,
                         @PathVariable Long registrationId,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User organizer = getOrganizer(session);
        if (organizer == null) {
            return "redirect:/login";
        }

        try {
            registrationService.rejectRegistration(projectId, registrationId, organizer.getId());
            redirectAttributes.addFlashAttribute("message", "Applicant rejected successfully");
        } catch (InvalidProjectStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return redirectToList(projectId);
    }

    private User getOrganizer(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && "ORGANIZER".equalsIgnoreCase(user.getRole()) ? user : null;
    }

    private String redirectToList(Long projectId) {
        return "redirect:/organizer/projects/" + projectId + "/registrations";
    }

}
