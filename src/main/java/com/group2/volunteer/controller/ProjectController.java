package com.group2.volunteer.controller;

import com.group2.volunteer.dto.ProjectDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // 1. Hàm xem TẤT CẢ dự án (Dùng hàm getAllProject() bạn mới viết để giải quyết triệt để lỗi biên dịch)
    @GetMapping("/all")
    public String showAllProjects(Model model) {
        List<Project> projects = projectService.getAllProject();
        model.addAttribute("projects", projects);
        return "project-list"; // Hoặc đổi thành file HTML quản lý của admin/organizer nếu cần
    }

    // 2. Hàm lọc và tìm kiếm dự án đang tuyển (Giữ nguyên của Nguyệt)
    @GetMapping
    public String listProjects(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "location", required = false) String location,
            Model model) {
        List<Project> projects = projectService.getRecruitingProjects(keyword, location);
        model.addAttribute("projects", projects);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        return "project-list";
    }

    // 3. Xem chi tiết dự án (Giữ nguyên của Nguyệt)
    @GetMapping("/detail/{id}")
    public String projectDetail(@PathVariable("id") Long id, Model model) {
        Project project = projectService.getProjectById(id);
        if (project == null) {
            return "redirect:/projects";
        }
        model.addAttribute("project", project);
        return "project-detail";
    }

    // 4. Đăng ký tham gia dự án (Giữ nguyên của Nguyệt)
    @PostMapping("/apply")
    public String applyProject(@RequestParam("projectId") Long projectId) {
        try {
            projectService.applyForProject(projectId);
            return "redirect:/projects/detail/" + projectId + "?success=true";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "redirect:/projects/detail/" + projectId + "?error=" + e.getMessage();
        }
    }
  
    // Hiển thị form tạo dự án (Organizer)
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("projectDTO", new ProjectDTO());
        // Sau này sẽ truyền thêm danh sách category
        return "project/create_project"; // file html
    }

    // Xử lý tạo dự án
    @PostMapping("/create")
    public String createProject(@Valid @ModelAttribute("projectDTO") ProjectDTO dto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        
        // Nếu có lỗi validation từ form, trả về lại form
        if (bindingResult.hasErrors()) {
            return "project/create_project";
        }

        // TODO: Lấy UserID từ Spring Security thay vì gán cứng
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

    // Hiển thị danh sách dự án cho Organizer
    @GetMapping("/organizer")
    public String listOrganizerProjects(Model model) {
        // Tạm thời dùng organizerId = 2 để demo
        Long organizerId = 2L;
        List<Project> projects = projectService.getProjectsByOrganizer(organizerId);
        model.addAttribute("projects", projects);
        return "project/list_projects"; // trang danh sách
    }

    // Admin duyệt dự án: hiển thị danh sách PENDING
    @GetMapping("/admin/approve")
    public String showPendingProjects(Model model) {
        List<Project> pendingProjects = projectService.getPendingProjects();
        model.addAttribute("pendingProjects", pendingProjects);
        return "project/admin_approve"; // file html
    }

    // Admin bấm nút phê duyệt
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
}
