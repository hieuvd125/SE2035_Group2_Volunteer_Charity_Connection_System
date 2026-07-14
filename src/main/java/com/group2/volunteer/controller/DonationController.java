package com.group2.volunteer.controller;

import com.group2.volunteer.dto.DonationDTO;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.exception.UserNotLoggedInException;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.service.DonationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/donations")
public class DonationController {
    private final DonationService donationService;
    private final ProjectRepository projectRepository;
    @PostMapping("/donate")
    public String handleDonate(@ModelAttribute DonationDTO donationDTO, HttpSession session, RedirectAttributes redirectAttributes){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }
        donationService.donate(donationDTO,currentUser.getId());
        redirectAttributes.addFlashAttribute("successMessage","Quyên góp thành công, cảm ơn bạn");
        return "redirect:/projects/detail/" + donationDTO.getProjectId();

    }
    @GetMapping("/form")
    public String showDonateForm(@RequestParam("projectId") Long projectId, HttpSession session, Model model){
        User currentUser = (User) session.getAttribute("user");
        if(currentUser == null){
            throw new UserNotLoggedInException("Người dùng chưa đăng nhập");
        }
        Project project = projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy dự án"));
        model.addAttribute("project",project);
        return "donation/donate_form";

    }
    @GetMapping("/list")
    public String showDonationList(@RequestParam("projectId") Long projectId, HttpSession session, Model model){
        User currentUser = (User) session.getAttribute("user");
        Project project = projectRepository.findById(projectId).orElseThrow(()->new ResourceNotFoundException("Không tìm thấy dự án"));
        model.addAttribute("project",project);
        model.addAttribute("list",donationService.getDonationList(projectId));
        model.addAttribute("total",donationService.getTotalDonatedAmount(projectId));
        return "donation/donation_list";
    }

}