package com.group2.volunteer.controller;

import com.group2.volunteer.entity.Project;
import com.group2.volunteer.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
}