package com.group2.volunteer.service;

import com.group2.volunteer.dto.DonationDTO;
import com.group2.volunteer.entity.Donation;

import java.util.List;

public interface DonationService {

    Donation donate(Long projectId, DonationDTO donationDTO);

    Double getTotalDonationByProjectId(Long projectId);

    List<Donation> getDonationsByProjectId(Long projectId);
}
