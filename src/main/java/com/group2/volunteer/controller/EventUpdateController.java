package com.group2.volunteer.controller;

import com.group2.volunteer.entity.EventUpdate;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.EventUpdateService;
import com.group2.volunteer.service.ProjectQueryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/projects/{projectId}/events")
@RequiredArgsConstructor
public class EventUpdateController {

    private final EventUpdateService eventUpdateService;
    private final ProjectQueryService projectQueryService;

    @GetMapping
    public String showEventUpdates(@PathVariable Long projectId, HttpSession session, Model model) {
        Project project = projectQueryService.getProjectById(projectId);
        List<EventUpdate> updates = eventUpdateService.getEventUpdatesByProjectId(projectId);

        User currentUser = (User) session.getAttribute("user");
        boolean isOrganizer = false;
        if (currentUser != null && "ORGANIZER".equalsIgnoreCase(currentUser.getRole())) {
            if (project.getOrganizer() != null && project.getOrganizer().getId().equals(currentUser.getId())) {
                isOrganizer = true;
            }
        }

        model.addAttribute("project", project);
        model.addAttribute("updates", updates);
        model.addAttribute("isOrganizer", isOrganizer);

        return "project/event_updates";
    }

    @PostMapping("/create")
    public String createEventUpdate(@PathVariable Long projectId,
                                    @RequestParam("title") String title,
                                    @RequestParam("content") String content,
                                    @RequestParam(value = "image", required = false) MultipartFile image,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để thực hiện chức năng này!");
            return "redirect:/login";
        }
        
        Project project = projectQueryService.getProjectById(projectId);
        if (!"ORGANIZER".equalsIgnoreCase(currentUser.getRole()) ||
                project.getOrganizer() == null ||
                !project.getOrganizer().getId().equals(currentUser.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không có quyền đăng tin cho dự án này!");
            return "redirect:/projects/" + projectId + "/events";
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                String uploadDirPath = System.getProperty("user.dir") + File.separator + "uploads";
                File uploadDir = new File(uploadDirPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                File destFile = new File(uploadDir.getAbsolutePath(), fileName);
                image.transferTo(destFile);

                imageUrl = "/uploads/" + fileName;
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi tải lên hình ảnh: " + e.getMessage());
                return "redirect:/projects/" + projectId + "/events";
            }
        }

        try {
            eventUpdateService.createEventUpdate(projectId, title, content, imageUrl);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng tin tức cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi đăng tin: " + e.getMessage());
        }

        return "redirect:/projects/" + projectId + "/events";
    }
}
