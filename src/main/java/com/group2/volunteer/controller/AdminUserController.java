package com.group2.volunteer.controller;

import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/review")
    public String showPendingUsers(Model model) {
        List<User> pendingUsers = userService.findAllPendingUsers();
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/pending_users";
    }

    @PostMapping("/approve/{id}")
    public String approveUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.updateUserStatus(id, "ACTIVE");
        redirectAttributes.addFlashAttribute("message", "Đã phê duyệt tài khoản thành công!");
        return "redirect:/admin/users/review";
    }

    @PostMapping("/reject/{id}")
    public String rejectUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        userService.updateUserStatus(id, "BLOCKED");
        redirectAttributes.addFlashAttribute("message", "Đã từ chối tài khoản!");
        return "redirect:/admin/users/review";
    }

    @GetMapping
    public String listUsers(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String status,
                            Model model) {

        List<User> users = userService.getUsersByFilter(keyword, role, status);

        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        model.addAttribute("role", role);
        model.addAttribute("status", status);

        return "admin/user_list";
    }

    @GetMapping("/detail/{id}")
    public String showUserDetail(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user_detail";
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user_edit";
    }

    @PostMapping("/edit/{id}")
    public String handleUpdateUser(@PathVariable Long id,
                                   @ModelAttribute("user") User userForm,
                                   RedirectAttributes redirectAttributes) {
        userService.updateUserByAdmin(id, userForm);
        redirectAttributes.addFlashAttribute("message", "Cập nhật thông tin người dùng thành công!");
        return "redirect:/admin/users";
    }
}
