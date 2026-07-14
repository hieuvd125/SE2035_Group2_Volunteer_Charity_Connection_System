package com.group2.volunteer.controller;

import com.group2.volunteer.dto.ProjectCreationDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.service.CategoryService;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/organizer/projects")
public class OrganizerProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        String role = (String) session.getAttribute("role");
        if(!"ORGANIZER".equals(role)){
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("projectDTO", new ProjectCreationDTO());
        return "organizer/create_project";
    }

    @PostMapping
    public String createProject(@Valid @ModelAttribute("projectDTO") ProjectCreationDTO dto,
                                BindingResult bindingResult, HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {
        Long organizerId = (Long) session.getAttribute("currentUserId");
        String role = (String) session.getAttribute("role");
        if(!"ORGANIZER".equals(role) || organizerId == null){
            return "redirect:/login";
        }
        if(bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.findAll());
            return "organizer/create_project";
        }

        try{
            projectService.createProject(dto, organizerId);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo dự án thành công!");
            return "redirect:/organizer/projects";
        }catch(InvalidProjectStateException e){
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "organizer/create_project";
        }
    }

    @GetMapping
    public String listOrganizerProjects(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            HttpSession session, Model model){

        Long organizerId = (Long) session.getAttribute("currentUserId");
        String role = (String) session.getAttribute("role");
        if(!"ORGANIZER".equals(role) || organizerId == null){
            return "redirect:/login";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<Project> projects = projectService.getOrganizerProjects(organizerId, title, location, status, pageable);
        model.addAttribute("projects", projects);
        model.addAttribute("title", title);
        model.addAttribute("location", location);
        model.addAttribute("status", status);

        model.addAttribute("statuses", Arrays.asList("PENDING", "RECRUITING", "REJECTED", "COMPLETED"));
        return "organizer/organizer_projects";
    }

    @GetMapping("/{id}")
    public String viewProjectDetails(@PathVariable Long id, Model model, HttpSession session){
        Long currentUserId = (Long) session.getAttribute("currentUserId");
        String role = (String) session.getAttribute("role");

        Project project = projectService.getProjectById(id);

        if ("ORGANIZER".equals(role)) {
            if (project.getOrganizer() == null || !project.getOrganizer().getId().equals(currentUserId)) {
                return "redirect:/organizer/projects";
            }
        } else if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("project", project);
        return "organizer/project_detail";
    }

}
