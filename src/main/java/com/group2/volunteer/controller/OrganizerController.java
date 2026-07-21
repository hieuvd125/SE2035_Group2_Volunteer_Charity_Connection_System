package com.group2.volunteer.controller;

import com.group2.volunteer.dto.OrganizerProfileDTO;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/organizer")
public class OrganizerController {

    @Autowired
    private UserService userService;

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
