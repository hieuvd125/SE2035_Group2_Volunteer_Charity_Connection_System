package com.group2.volunteer.service;

import com.group2.volunteer.dto.DonationDTO;
import com.group2.volunteer.entity.Donation;

import java.util.List;

public interface DonationService {
    void donate(DonationDTO donationDTO, Long userId);
    Double getTotalDonatedAmount(Long projectId);
    List<Donation> getDonationList (Long projectId);
}
