package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.AttendanceProofService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AttendanceProofController {
    private final AttendanceProofService attendanceProofService;

    @GetMapping("/attendance/submit")
    public String showSubmitProofPage(HttpSession session, Model model) {
        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (!hasRole(user, "VOLUNTEER")) {
            return "redirect:/error/403";
        }

        model.addAttribute("registrations", attendanceProofService.getRegistrationsByVolunteer(user.getId()));
        return "volunteer/submit_proof";
    }

    @PostMapping("/attendance/submit")
    public String submitProof(@RequestParam Long registrationId,
                              @RequestParam(required = false) String reportText,
                              @RequestParam(required = false) String proofImage,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (!hasRole(user, "VOLUNTEER")) {
            return "redirect:/error/403";
        }

        attendanceProofService.submitProof(registrationId, user.getId(), reportText, proofImage);
        redirectAttributes.addFlashAttribute("message", "Nộp minh chứng thành công. Vui lòng chờ Organizer duyệt.");
        return "redirect:/attendance/submit";
    }

    @GetMapping("/attendance/verify")
    public String showVerifyAttendancePage(HttpSession session, Model model) {
        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (!hasRole(user, "ORGANIZER") && !hasRole(user, "ADMIN")) {
            return "redirect:/error/403";
        }

        model.addAttribute("proofs", attendanceProofService.getProofsWaitingForVerification());
        return "organizer/verify_attendance";
    }

    @PostMapping("/attendance/verify/{proofId}")
    public String verifyAttendance(@PathVariable Long proofId,
                                   @RequestParam Integer confirmedHours,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        if (!hasRole(user, "ORGANIZER") && !hasRole(user, "ADMIN")) {
            return "redirect:/error/403";
        }

        attendanceProofService.verifyAttendance(proofId, confirmedHours);
        redirectAttributes.addFlashAttribute("message", "Đã duyệt minh chứng và cộng giờ tình nguyện.");
        return "redirect:/attendance/verify";
    }

    private User getLoggedInUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private boolean hasRole(User user, String role) {
        return user.getRole() != null && user.getRole().equalsIgnoreCase(role);
    }
}
