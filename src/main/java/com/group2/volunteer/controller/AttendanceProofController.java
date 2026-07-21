package com.group2.volunteer.controller;

import com.group2.volunteer.dto.AttendanceProofRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.UserNotLoggedInException;
import com.group2.volunteer.service.AttendanceProofService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/volunteer/attendance")
public class AttendanceProofController {
    private final AttendanceProofService attendanceProofService;

    @GetMapping("/submit")
    public String showSubmitProofPage(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        model.addAttribute("registrations",
                attendanceProofService.getApprovedRegistrationsByVolunteer(currentUser.getId()));
        return "volunteer/submit_proof";
    }

    @PostMapping("/submit")
    public String submitProof(@ModelAttribute AttendanceProofRequest request,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }

        attendanceProofService.submitProof(
                request.getRegistrationId(),
                currentUser.getId(),
                request.getReportText(),
                request.getProofImage());
        redirectAttributes.addFlashAttribute("message", "Nộp minh chứng thành công. Vui lòng chờ Organizer duyệt.");
        return "redirect:/volunteer/attendance/submit";
    }

}
