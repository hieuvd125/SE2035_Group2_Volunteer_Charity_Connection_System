package com.group2.volunteer.controller;

import com.group2.volunteer.entity.ProjectRegistration;
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

    // Xem danh sách đơn đăng ký của một dự án (Cho Organizer)
    @GetMapping("/project/{projectId}")
    public String viewRegistrations(@PathVariable Long projectId, Model model, HttpSession session) {
        List<ProjectRegistration> registrations = registrationService.getRegistrationsByProject(projectId);
        model.addAttribute("registrations", registrations);
        model.addAttribute("projectId", projectId);
        return "project/registration_list";
    }

    // Xử lý duyệt (APPROVED) hoặc Từ chối (REJECTED)
    @PostMapping("/{registrationId}/status")
    public String updateStatus(@PathVariable Long registrationId,
                               @RequestParam String status,
                               @RequestParam Long projectId) {
        registrationService.updateRegistrationStatus(registrationId, status);
        return "redirect:/registrations/project/" + projectId;
    }
}