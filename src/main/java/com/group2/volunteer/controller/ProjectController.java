package com.group2.volunteer.controller;

import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.service.EventUpdateService;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.SavedProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import com.group2.volunteer.entity.User;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final SavedProjectService savedProjectService;
    private final EventUpdateService eventUpdateService;

    public ProjectController(ProjectService projectService,
                             SavedProjectService savedProjectService,
                             EventUpdateService eventUpdateService) {
        this.projectService = projectService;
        this.savedProjectService = savedProjectService;
        this.eventUpdateService = eventUpdateService;
    }

    private void checkOrganizerAccess(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            throw new com.group2.volunteer.exception.BadRequestException("Chỉ Organizer mới có quyền thực hiện hành động này");
        }
    }

    private void checkAdminAccess(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ROLE_ADMIN".equals(loggedUser.getRole())) {
            throw new com.group2.volunteer.exception.BadRequestException("Chỉ Admin mới có quyền thực hiện hành động này");
        }
    }

    private void checkVolunteerAccess(HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null || !"ROLE_VOLUNTEER".equals(loggedUser.getRole())) {
            throw new com.group2.volunteer.exception.BadRequestException("Vui lòng đăng nhập bằng tài khoản Tình nguyện viên để thực hiện");
        }
    }

    @GetMapping("/all")
    public String showAllProjects(Model model) {
        List<Project> projects = projectService.getAllProject();
        model.addAttribute("projects", projects);
        return "project/project-list";
    }

    @GetMapping
    public String listProjects(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            Model model) {
        List<Project> projects = projectService.getRecruitingProjects(keyword, location);
        model.addAttribute("projects", projects);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        return "project/project-list";
    }

    @GetMapping("/detail/{id}")
    public String projectDetail(@PathVariable("id") Long id, Model model, HttpSession session) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project);

        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser != null && ("ROLE_VOLUNTEER".equals(loggedUser.getRole()) || "ROLE_ORGANIZER".equals(loggedUser.getRole()))) {
            Long currentUserId = loggedUser.getId();
            boolean isSaved = savedProjectService.isProjectSaved(currentUserId, id);
            model.addAttribute("isSaved", isSaved);
            model.addAttribute("currentUserId", currentUserId);
        } else {
            model.addAttribute("isSaved", false);
            model.addAttribute("currentUserId", null);
        }

        List<EventUpdate> updates = eventUpdateService.getEventUpdatesByProject(id);
        model.addAttribute("updates", updates);

        return "project/project_detail";
    }

    @PostMapping("/apply")
    public String applyProject(@RequestParam("projectId") Long projectId, HttpSession session) {
        checkVolunteerAccess(session);
        User loggedUser = (User) session.getAttribute("loggedUser");
        try {
            projectService.applyForProject(projectId, loggedUser.getId());
            return "redirect:/projects/detail/" + projectId + "?success=true";
        } catch (Exception e) {
            return "redirect:/projects/detail/" + projectId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        checkOrganizerAccess(session);
        model.addAttribute("projectDTO", new ProjectDTO());
        return "project/create_project";
    }

    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("projectDTO") ProjectDTO dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        checkOrganizerAccess(session);
        if (bindingResult.hasErrors()) {
            return "project/create_project";
        }
        User loggedUser = (User) session.getAttribute("loggedUser");
        Long organizerId = loggedUser.getId();
        try {
            projectService.createProject(dto, organizerId);
            redirectAttributes.addFlashAttribute("successMessage", "Dự án đã được tạo và chờ duyệt!");
            return "redirect:/projects/organizer";
        } catch (com.group2.volunteer.exception.BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projects/create";
        }
    }

    @GetMapping("/organizer")
    public String listOrganizerProjects(Model model, HttpSession session) {
        checkOrganizerAccess(session);
        User loggedUser = (User) session.getAttribute("loggedUser");
        Long organizerId = loggedUser.getId();
        List<Project> projects = projectService.getProjectsByOrganizer(organizerId);
        model.addAttribute("projects", projects);
        return "project/list_projects";
    }

    @GetMapping("/admin/approve")
    public String showPendingProjects(Model model, HttpSession session) {
        checkAdminAccess(session);
        List<Project> pendingProjects = projectService.getPendingProjects();
        model.addAttribute("pendingProjects", pendingProjects);
        return "project/admin_approve";
    }

    @PostMapping("/admin/approve/{projectId}")
    public String approveProject(@PathVariable Long projectId, RedirectAttributes redirectAttributes, HttpSession session) {
        checkAdminAccess(session);
        try {
            projectService.approveProject(projectId);
            redirectAttributes.addFlashAttribute("successMessage", "Dự án đã được duyệt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projects/admin/approve";
    }

    @GetMapping("/detail/{id}/update")
    public String showUpdateForm(@PathVariable Long id, Model model, HttpSession session) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/projects/detail/" + id + "?error=Please login first.";
        }
        if (!"ROLE_ORGANIZER".equals(loggedUser.getRole())) {
            return "redirect:/projects/detail/" + id + "?error=Only organizers can post updates.";
        }

        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
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