package com.group2.volunteer.service;

import com.group2.volunteer.dto.DonationDTO;
import com.group2.volunteer.entity.Donation;
import com.group2.volunteer.entity.Project;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.repository.DonationRepository;
import com.group2.volunteer.repository.ProjectRepository;
import com.group2.volunteer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DonationServiceImpl implements DonationService{
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    @Override
    public void donate(DonationDTO donationDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy người dùng này"));
        Project project = projectRepository.findById(donationDTO.getProjectId()).orElseThrow(()-> new ResourceNotFoundException("Không tìm thấy dự án"));
        Donation donation = new Donation();
        donation.setUser(user);
        donation.setProject(project);
        donation.setAmount(donationDTO.getAmount());
        if(donationDTO.isAnonymous()){
            donation.setDonorName("Người dùng ẩn danh");

        }else{
            donation.setDonorName(user.getFullName());
        }
        donationRepository.save(donation);

    }

    @Override
    public Double getTotalDonatedAmount(Long projectId) {
        Double total = donationRepository.getTotalDonationAmount(projectId);
        if(total == null){
            return 0.0;
        }
        return total;
    }

    @Override
    public List<Donation> getDonationList(Long projectId) {
        List<Donation> list = donationRepository.findByProjectIdOrderByDonatedAtDesc(projectId);
        return list;
    }
}
