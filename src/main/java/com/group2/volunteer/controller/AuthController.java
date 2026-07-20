package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.dto.RegisterRequest;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
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
    public String home() {
        return "redirect:/projects/homepage";
    }

    @GetMapping("/login")
    public String login() {
        return "common/login";
    }

    @PostMapping("/login")
    public ModelAndView handleLogin(@RequestParam(name = "email") String email,
                                    @RequestParam(name = "password") String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView();
        User user = userService.authenticate(new LoginDTO(email, password));
        session.setAttribute("user", user);
        redirectAttributes.addFlashAttribute("message", "Đăng nhập thành công.");
        mv.setViewName("redirect:/");
        return mv;
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "common/register";
    }

    @PostMapping("/register")
    public String handleRegister(@ModelAttribute("registerRequest") RegisterRequest request,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "common/register";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setStatus("PENDING");
        user.setTotalHours(0);

        userService.register(user);

        redirectAttributes.addFlashAttribute("message", "Đơn đăng ký tài khoản đã được gửi. Hãy chờ admin phê duyệt.");
        return "redirect:/login";
    }

    @GetMapping("/admin/users/review")
    public String showPendingUsers(Model model) {
        List<User> pendingUsers = userService.findAllPendingUsers();
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/pending_users";
    }

    @GetMapping("/admin/users")
    public String userManagement() {
        return "admin/user_list";
    }

    @PostMapping("/admin/users/approve/{id}")
    public String approveUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.updateUserStatus(id, "ACTIVE");
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt tài khoản thành công!");
        return "redirect:/admin/users/review";
    }

    @PostMapping("/admin/users/reject/{id}")
    public String rejectUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.updateUserStatus(id, "BLOCKED");
        redirectAttributes.addFlashAttribute("message", "Đã từ chối tài khoản!");
        return "redirect:/admin/users/review";
    }
}
