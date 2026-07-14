package com.group2.volunteer.controller;

import com.group2.volunteer.entity.SavedProject;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.UserNotLoggedInException;
import com.group2.volunteer.service.SavedProjectService;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/saved-projects")
@RequiredArgsConstructor
public class SavedProjectController {
    private final SavedProjectService savedProjectService;
@PostMapping("/save")
    public String saveProject(@RequestParam("projectId") Long projectId,
        HttpSession session, RedirectAttributes redirectAttributes) {
    User sessionUser = (User) session.getAttribute("currentUser");
    if (sessionUser == null) {
        throw new UserNotLoggedInException("Người dùng chưa đăng nhập!");


    } else {
        savedProjectService.saveProject(sessionUser.getId(), projectId);
        redirectAttributes.addFlashAttribute("successMessage","Đã lưu dự án thành công");
        return "redirect:/saved-projects/my";
    }

}
@GetMapping("/my")
    public String showSavedProject(HttpSession session, Model model){
    User sessionUser = (User) session.getAttribute("currentUser");
    if (sessionUser == null) {
        throw new UserNotLoggedInException("Người dùng chưa đăng nhập!");


    } else {
        List<SavedProject> list = savedProjectService.getSavedProjectList(sessionUser.getId());
        model.addAttribute("savedList",list);
        return "project/saved_projects";
    }
}
@PostMapping("/unsave")
    public String unsaveProject(@RequestParam("projectId") Long projectId,HttpSession session, RedirectAttributes redirectAttributes){
    User sessionUser = (User) session.getAttribute("currentUser");
    if (sessionUser == null) {
        throw new UserNotLoggedInException("Người dùng chưa đăng nhập!");

    } else {
        savedProjectService.unsaveProject(sessionUser.getId(),projectId);
        redirectAttributes.addFlashAttribute("successMessage","Đã hủy lưu dự án thành công");
        return "redirect:/saved-projects/my";
    }
}

}
