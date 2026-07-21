package com.group2.volunteer.controller;

import com.group2.volunteer.dto.OrganizerProfileDTO;
import com.group2.volunteer.dto.ProjectCreationDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidProjectStateException;
import com.group2.volunteer.service.CategoryService;
import com.group2.volunteer.service.ProjectService;
import com.group2.volunteer.service.ProjectRegistrationService;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/organizer/projects")
public class OrganizerProjectController {

    private static final int PAGE_SIZE = 5;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProjectRegistrationService projectRegistrationService;

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ORGANIZER".equals(user.getRole())) {
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
        User user = (User) session.getAttribute("user");
        if (user == null || !"ORGANIZER".equals(user.getRole())) {
            return "redirect:/login";
        }
        Long currentUserId = user.getId();

        if(bindingResult.hasErrors()){
            model.addAttribute("categories", categoryService.findAll());
            return "organizer/create_project";
        }

        try{
            projectService.createProject(dto, currentUserId);
            redirectAttributes.addFlashAttribute("successMessage", "Tạo dự án thành công!");
            return "redirect:/organizer/projects";
        }catch(InvalidProjectStateException e){
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("errorMessage", e.getMessage());
            return "organizer/create_project";
        }
    }

    @GetMapping
    public String listOrganizerProjects(@RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"ORGANIZER".equals(user.getRole())) {
            return "redirect:/login";
        }
        Long currentUserId = user.getId();

        Page<Project> page = projectService.getOrganizerProjects(
                currentUserId, title, location, status,
                PageRequest.of(Math.max(pageNumber, 0), PAGE_SIZE, Sort.by("id").ascending()));

        model.addAttribute("projects", page.getContent());
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("currentPage", page.getNumber());
        model.addAttribute("filterTitle", title);
        model.addAttribute("filterLocation", location);
        model.addAttribute("filterStatus", status);
        model.addAttribute("statuses", Arrays.asList("PENDING", "PLANNING", "RECRUITING", "RECRUITMENT_CLOSED", "ONGOING", "REJECTED", "COMPLETED"));

        return "organizer/organizer_projects";
    }

    @GetMapping("/{id}")
    public String viewProjectDetails(@PathVariable Long id, Model model, HttpSession session){
        User user = (User) session.getAttribute("user");
        if (user == null || !"ORGANIZER".equals(user.getRole())) {
            return "redirect:/login";
        }
        Long currentUserId = user.getId();
        String role = user.getRole();

        Project project = projectService.getProjectById(id);

        if ("ORGANIZER".equals(role)) {
            if (project.getOrganizer() == null || !project.getOrganizer().getId().equals(currentUserId)) {
                return "redirect:/organizer/projects";
            }
        } else if (!"ADMIN".equals(role)) {
            return "redirect:/login";
        }

        model.addAttribute("project", project);
        model.addAttribute("applicantCount",
                projectRegistrationService.countProjectRegistrations(id, currentUserId));
        return "organizer/project_detail";
    }

    @PostMapping("/{id}/close-recruitment")
    public String closeRecruitment(@PathVariable Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ORGANIZER".equals(user.getRole())) {
            return "redirect:/login";
        }

        try {
            projectService.closeRecruitment(id, user.getId());
            redirectAttributes.addFlashAttribute("message", "Recruitment closed successfully");
        } catch (InvalidProjectStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/organizer/projects/" + id;
    }

    @GetMapping("/profile")
    public String showProfileForm(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || !"ORGANIZER".equals(sessionUser.getRole())) {
            return "redirect:/login";
        }

        OrganizerProfileDTO profileDTO = userService.getOrganizerProfileDTO(sessionUser.getId());
        model.addAttribute("profileDTO", profileDTO);
        return "organizer/organizer_profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profileDTO") OrganizerProfileDTO profileDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null || !"ORGANIZER".equals(sessionUser.getRole())) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            return "organizer/organizer_profile";
        }

        userService.updateOrganizerProfile(sessionUser.getId(), profileDTO);

        sessionUser.setFullName(profileDTO.getFullName());
        session.setAttribute("user", sessionUser);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin tổ chức thành công!");
        return "redirect:/organizer/projects/profile";
    }

}
