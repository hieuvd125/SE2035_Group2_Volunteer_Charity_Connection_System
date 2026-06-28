package com.group2.volunteer.service;

import com.group2.volunteer.entity.Donation;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.exception.BadRequestException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.repository.DonationRepository;
import com.group2.volunteer.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DonationServiceImpl implements DonationService{

    private final DonationRepository donationRepository;
    private final ProjectRepository projectRepository;

    public DonationServiceImpl(DonationRepository donationRepository, ProjectRepository projectRepository) {
        this.donationRepository = donationRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public Donation donate(Long projectId, String donorName, Double amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("Donation amount must be greater than 0");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id = " + projectId));

        Donation donation = new Donation();
        donation.setProject(project);
        donation.setDonorName(donorName);
        donation.setAmount(amount);

        return donationRepository.save(donation);
    }

    @Override
    public Double getTotalDonationByProjectId(Long projectId) {
        return donationRepository.getTotalAmountByProjectId(projectId);
    }

    @Override
    public List<Donation> getDonationsByProjectId(Long projectId) {
        return donationRepository.findByProjectId(projectId);
    }
}
