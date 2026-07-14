package com.group2.volunteer.controller;

import com.group2.volunteer.service.AttendanceProofService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AttendanceProofController {
    private final AttendanceProofService attendanceProofService;

    public AttendanceProofController(AttendanceProofService attendanceProofService) {
        this.attendanceProofService = attendanceProofService;
    }

    @GetMapping("/attendance/submit")
    public String showSubmitProofPage(@RequestParam(required = false) Long volunteerId, Model model) {
        model.addAttribute("volunteerId", volunteerId);

        if (volunteerId != null) {
            model.addAttribute("registrations", attendanceProofService.getRegistrationsByVolunteer(volunteerId));
        }

        return "volunteer/submit_proof";
    }

    @PostMapping("/attendance/submit")
    public String submitProof(@RequestParam Long registrationId,
                              @RequestParam(required = false) String reportText,
                              @RequestParam(required = false) String proofImage,
                              RedirectAttributes redirectAttributes) {
        attendanceProofService.submitProof(registrationId, reportText, proofImage);
        redirectAttributes.addFlashAttribute("message", "Nộp minh chứng thành công. Vui lòng chờ Organizer duyệt minh chứng.");
        return "redirect:/attendance/submit";
    }

    @GetMapping("/attendance/verify")
    public String showVerifyAttendancePage(Model model) {
        model.addAttribute("proofs", attendanceProofService.getProofsWaitingForVerification());
        return "organizer/verify_attendance";
    }

    @PostMapping("/attendance/verify/{proofId}")
    public String verifyAttendance(@PathVariable Long proofId,
                                   @RequestParam Integer confirmedHours,
                                   RedirectAttributes redirectAttributes) {
        attendanceProofService.verifyAttendance(proofId, confirmedHours);
        redirectAttributes.addFlashAttribute("message", "Đã duyệt minh chứng và cộng giờ tình nguyện.");
        return "redirect:/attendance/verify";
    }
}
