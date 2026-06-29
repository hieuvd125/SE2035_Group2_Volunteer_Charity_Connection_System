package com.group2.volunteer.controller;

import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.service.EventUpdateService;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.SavedProjectService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SavedProjectService savedProjectService;

    @Autowired
    private EventUpdateService eventUpdateService;

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
    public String projectDetail(@PathVariable("id") Long id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project);

        Long currentUserId = 4L;
        boolean isSaved = savedProjectService.isProjectSaved(currentUserId, id);
        model.addAttribute("isSaved", isSaved);
        model.addAttribute("currentUserId", currentUserId);

        List<EventUpdate> updates = eventUpdateService.getEventUpdatesByProject(id);
        model.addAttribute("updates", updates);

        return "project/project_detail";
    }
    @GetMapping("/saved")
    public String listSavedProjects(Model model) {
        Long currentUserId = 4L; // TODO: lấy từ session
        List<SavedProject> savedProjects = savedProjectService.getSavedProjectsByUserId(currentUserId);
        model.addAttribute("savedProjects", savedProjects);
        return "project/saved_projects"; // Tạo file saved_projects.html trong templates/project/
    }

    @PostMapping("/apply")
    public String applyProject(@RequestParam("projectId") Long projectId) {
        try {
            projectService.applyForProject(projectId);
            return "redirect:/projects/detail/" + projectId + "?success=true";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "redirect:/projects/detail/" + projectId + "?error=" + e.getMessage();
        }
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectDTO", new ProjectDTO());
        return "project/create_project";
    }

    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("projectDTO") ProjectDTO dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "project/create_project";
        }
        Long organizerId = 2L;
        try {
            Project project = projectService.createProject(dto, organizerId);
            redirectAttributes.addFlashAttribute("successMessage", "Dự án đã được tạo và chờ duyệt!");
            return "redirect:/projects";
        } catch (com.group2.volunteer.exception.BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/projects/create";
        }
    }

    @GetMapping("/organizer")
    public String listOrganizerProjects(Model model) {
        Long organizerId = 2L;
        List<Project> projects = projectService.getProjectsByOrganizer(organizerId);
        model.addAttribute("projects", projects);
        return "project/list_projects";
    }

    @GetMapping("/admin/approve")
    public String showPendingProjects(Model model) {
        List<Project> pendingProjects = projectService.getPendingProjects();
        model.addAttribute("pendingProjects", pendingProjects);
        return "project/admin_approve";
    }

    @PostMapping("/admin/approve/{projectId}")
    public String approveProject(@PathVariable Long projectId, RedirectAttributes redirectAttributes) {
        try {
            projectService.approveProject(projectId);
            redirectAttributes.addFlashAttribute("successMessage", "Dự án đã được duyệt!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/projects/admin/approve";
    }

    @PostMapping("/save")
    public String saveProject(@RequestParam("projectId") Long projectId,
                              @RequestParam("userId") Long userId,
                              RedirectAttributes redirectAttributes) {
        String message = savedProjectService.saveProject(userId, projectId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/";
    }

    @PostMapping("/unsave")
    public String unsaveProject(@RequestParam("projectId") Long projectId,
                                @RequestParam("userId") Long userId,
                                RedirectAttributes redirectAttributes) {
        String message = savedProjectService.unsaveProject(userId, projectId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/";
    }

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
    @GetMapping("/home")
    public String homePage(Model model) {
        Long currentUserId = 4L; // TODO: lấy từ session

        List<Project> projects = projectService.getRecruitingProjects(null, null);
        List<Long> savedProjectIds = savedProjectService.getSavedProjectIds(currentUserId);

        model.addAttribute("projects", projects);
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("savedProjectIds", savedProjectIds);
        return "homepage"; // hoặc "home/homepage" nếu bạn để trong thư mục home
    }
}