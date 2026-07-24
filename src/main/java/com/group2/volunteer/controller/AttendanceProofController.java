package com.group2.volunteer.controller;

import com.group2.volunteer.dto.AttendanceProofRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.InvalidAttendanceProofException;
import com.group2.volunteer.exception.UserNotLoggedInException;
import com.group2.volunteer.service.AttendanceProofService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

        attendanceProofService.validateProofSubmission(request.getRegistrationId(), currentUser.getId());

        if (request.getReportText() == null || request.getReportText().isBlank()) {
            throw new InvalidAttendanceProofException("Cần nhập nội dung báo cáo.");
        }
        if (request.getProofImage() == null || request.getProofImage().isEmpty()) {
            throw new InvalidAttendanceProofException("Cần tải lên ảnh minh chứng.");
        }

        String proofImagePath = saveProofImage(request.getProofImage());

        attendanceProofService.submitProof(request.getRegistrationId(),
                currentUser.getId(),
                request.getReportText(),
                proofImagePath);
        redirectAttributes.addFlashAttribute("message", "Nộp minh chứng thành công. Vui lòng chờ Organizer duyệt.");
        return "redirect:/volunteer/attendance/submit";
    }

    private String saveProofImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new InvalidAttendanceProofException("File minh chứng phải là hình ảnh.");
        }

        try {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String originalName = image.getOriginalFilename() == null ? "image"
                    : Paths.get(image.getOriginalFilename()).getFileName().toString();
            String fileName = UUID.randomUUID() + "_" + originalName;
            image.transferTo(uploadDir.resolve(fileName));
            return "/uploads/" + fileName;
        } catch (IOException e) {
            throw new InvalidAttendanceProofException("Không thể lưu ảnh minh chứng.");
        }
    }
}
