package com.group2.volunteer.controller;

import com.group2.volunteer.dto.LoginDTO;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}
