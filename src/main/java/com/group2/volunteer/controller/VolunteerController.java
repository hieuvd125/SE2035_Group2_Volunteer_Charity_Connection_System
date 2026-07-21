package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.dto.VolunteerProfileUpdateRequest;
import com.group2.volunteer.exception.UserNotLoggedInException;
import com.group2.volunteer.service.UserService;
import com.group2.volunteer.service.AttendanceProofService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class VolunteerController {
    private final UserService userService;
    private final AttendanceProofService attendanceProofService;

    @GetMapping("profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        User user = userService.getUserById(currentUser.getId());
        Long attendedProjectCount = attendanceProofService.countAttendedProjectsByVolunteer(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("attendedProjectCount", attendedProjectCount);
        model.addAttribute("badgeName", userService.getBadgeName(attendedProjectCount));

        return "volunteer/profile";
    }

    @GetMapping("profile/edit")
    public String showEditProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        User user = userService.getUserById(currentUser.getId());
        VolunteerProfileUpdateRequest profileRequest = new VolunteerProfileUpdateRequest();
        profileRequest.setFullName(user.getFullName());
        profileRequest.setPhoneNumber(user.getPhoneNumber());
        profileRequest.setDateOfBirth(user.getDateOfBirth());
        profileRequest.setGender(user.getGender());
        profileRequest.setCity(user.getCity());
        profileRequest.setAddress(user.getAddress());
        profileRequest.setAvatarUrl(user.getAvatarUrl());
        profileRequest.setBio(user.getBio());
        profileRequest.setWebsite(user.getWebsite());
        model.addAttribute("profileRequest", profileRequest);

        return "volunteer/edit_profile";
    }

    @PostMapping("profile/edit")
    public String updateProfile(@ModelAttribute VolunteerProfileUpdateRequest profileRequest,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        User updatedUser = userService.updateVolunteerProfile(currentUser.getId(), profileRequest);
        session.setAttribute("user", updatedUser);
        redirectAttributes.addFlashAttribute("message", "Cập nhật hồ sơ thành công.");
        return "redirect:/profile";
    }

    @GetMapping("my-activities")
    public String showMyActivities(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        User user = userService.getUserById(currentUser.getId());
        model.addAttribute("attendedProjectCount",
                attendanceProofService.countAttendedProjectsByVolunteer(user.getId()));
        model.addAttribute("registrations",
                attendanceProofService.getRegistrationsByVolunteer(user.getId()));

        return "volunteer/my_activities";
    }
}
