package com.group2.volunteer.controller;

import com.group2.volunteer.service.DonationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @GetMapping("/projects/{projectId}/donate")
    public String showDonateForm(@PathVariable Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("totalDonation", donationService.getTotalDonationByProjectId(projectId));
        return "donation/form";
    }

    @PostMapping("/projects/{projectId}/donate")
    public String donate(@PathVariable Long projectId,
                         @RequestParam String donorName,
                         @RequestParam Double amount) {
        donationService.donate(projectId, donorName, amount);
        return "redirect:/projects/" + projectId + "/donate?success";
    }

    @GetMapping("/projects/{projectId}/donations")
    public String listDonations(@PathVariable Long projectId, Model model) {
        model.addAttribute("projectId", projectId);
        model.addAttribute("donations", donationService.getDonationsByProjectId(projectId));
        model.addAttribute("totalDonation", donationService.getTotalDonationByProjectId(projectId));
        return "donation/list";
    }
}
