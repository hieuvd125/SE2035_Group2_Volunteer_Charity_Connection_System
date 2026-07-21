package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginRequest;
import com.group2.volunteer.dto.RegisterOrganizerRequest;
import com.group2.volunteer.dto.RegisterVolunteerRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/projects/homepage";
    }

    @GetMapping("/login")
    public String login() {
        return "common/login";
    }

    @PostMapping("/login")
    public String handleLogin(@Valid @RequestParam(name = "username") String username,
                                    @Valid @RequestParam(name = "password") String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User user = userService.authenticate(new LoginRequest(username, password));
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("message", "Đăng nhập thành công.");
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/projects/homepage";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register/volunteer")
    public String showVolunteerRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterVolunteerRequest());
        return "common/register_volunteer";
    }

    @PostMapping("/register/volunteer")
    public String handleRegisterVolunteer(@Valid @ModelAttribute("registerRequest") RegisterVolunteerRequest request,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "common/register_volunteer";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setBio(request.getBio());

        user.setRole("VOLUNTEER");
        user.setStatus("ACTIVE");

        userService.register(user);

        redirectAttributes.addFlashAttribute("message", "Đăng ký tài khoản Tình nguyện viên thành công!");
        return "redirect:/login";
    }

    @GetMapping("/register/organizer")
    public String showOrganizerRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterOrganizerRequest());
        return "common/register_organizer";
    }

    @PostMapping("/register/organizer")
    public String handleRegisterOrganizer(@Valid @ModelAttribute("registerRequest") RegisterOrganizerRequest request,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "common/register_organizer";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setCity(request.getCity());
        user.setAddress(request.getAddress());
        user.setWebsite(request.getWebsite());
        user.setDescription(request.getDescription());

        user.setRole("ORGANIZER");
        user.setStatus("PENDING");

        userService.register(user);

        redirectAttributes.addFlashAttribute("message", "Đơn đăng ký Tổ chức đã gửi. Vui lòng chờ Admin phê duyệt!");
        return "redirect:/login";
    }

}
