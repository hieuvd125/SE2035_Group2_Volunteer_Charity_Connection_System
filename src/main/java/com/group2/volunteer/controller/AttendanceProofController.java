package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AttendanceProofController {

    private final ProjectRegistrationRepository registrationRepository;

    // Thay thế @Autowired bằng Constructor Injection chuẩn chỉnh
    public AttendanceProofController(ProjectRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User loggedUser = (User) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }

        Integer totalHours = registrationRepository.sumConfirmedHoursByVolunteerId(loggedUser.getId());
        model.addAttribute("totalHours", totalHours != null ? totalHours : 0); // Thêm xử lý tránh NullPointerException nếu chưa có giờ công nào
        model.addAttribute("user", loggedUser);

        String badge = "Thành viên mới";
        if (totalHours != null) {
            if (totalHours >= 50) badge = "Chiến binh Tình nguyện Vàng ";
            else if (totalHours >= 20) badge = "Tình nguyện viên Tích cực ";
            else if (totalHours >= 5) badge = "Đồng đội Đáng tin cậy ";
        }

        model.addAttribute("badge", badge);
        return "common/profile";
    }
}