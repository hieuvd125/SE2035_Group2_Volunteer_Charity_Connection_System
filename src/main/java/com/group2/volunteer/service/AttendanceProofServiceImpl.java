package com.group2.volunteer.service;

import com.group2.volunteer.constant.RegistrationStatus;
import com.group2.volunteer.entity.AttendanceProof;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.entity.User;
import com.group2.volunteer.exception.BadRequestException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.repository.AttendanceProofRepository;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import com.group2.volunteer.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendanceProofServiceImpl implements AttendanceProofService {
    private final AttendanceProofRepository attendanceProofRepository;
    private final ProjectRegistrationRepository projectRegistrationRepository;
    private final UserRepository userRepository;

    public AttendanceProofServiceImpl(AttendanceProofRepository attendanceProofRepository,
                                      ProjectRegistrationRepository projectRegistrationRepository,
                                      UserRepository userRepository) {
        this.attendanceProofRepository = attendanceProofRepository;
        this.projectRegistrationRepository = projectRegistrationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ProjectRegistration> getRegistrationsByVolunteer(Long volunteerId) {
        return projectRegistrationRepository.findByVolunteerId(volunteerId);
    }

    @Override
    public List<AttendanceProof> getProofsWaitingForVerification() {
        return attendanceProofRepository.findByRegistration_StatusNot(RegistrationStatus.ATTENDED);
    }

    @Override
    @Transactional
    public AttendanceProof submitProof(Long registrationId, Long volunteerId, String reportText, String proofImage) {
        ProjectRegistration registration = projectRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đăng ký."));

        if (registration.getVolunteer() == null || !registration.getVolunteer().getId().equals(volunteerId)) {
            throw new BadRequestException("Không thể nộp minh chứng cho đơn đăng ký của volunteer khác.");
        }

        if (RegistrationStatus.REJECTED.equals(registration.getStatus())) {
            throw new BadRequestException("Đơn đăng ký đã bị từ chối nên không thể nộp minh chứng.");
        }

        if (RegistrationStatus.ATTENDED.equals(registration.getStatus())) {
            throw new BadRequestException("Đơn đăng ký này đã được chấm công.");
        }

        if ((reportText == null || reportText.isBlank()) && (proofImage == null || proofImage.isBlank())) {
            throw new BadRequestException("Cần nhập báo cáo hoặc đường dẫn ảnh minh chứng.");
        }

        AttendanceProof proof = attendanceProofRepository.findByRegistrationId(registrationId)
                .orElseGet(AttendanceProof::new);
        proof.setRegistration(registration);
        proof.setReportText(reportText);
        proof.setProofImage(proofImage);

        return attendanceProofRepository.save(proof);
    }

    @Override
    @Transactional
    public ProjectRegistration verifyAttendance(Long proofId, Integer confirmedHours) {
        if (confirmedHours == null || confirmedHours <= 0) {
            throw new BadRequestException("Số giờ xác nhận phải lớn hơn 0.");
        }

        AttendanceProof proof = attendanceProofRepository.findById(proofId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy minh chứng."));

        ProjectRegistration registration = proof.getRegistration();
        User volunteer = registration.getVolunteer();

        int oldHours = registration.getConfirmedHours() == null ? 0 : registration.getConfirmedHours();
        int currentTotalHours = volunteer.getTotalHours() == null ? 0 : volunteer.getTotalHours();
        int hoursToAdd = RegistrationStatus.ATTENDED.equals(registration.getStatus())
                ? confirmedHours - oldHours
                : confirmedHours;

        registration.setConfirmedHours(confirmedHours);
        registration.setStatus(RegistrationStatus.ATTENDED);
        volunteer.setTotalHours(Math.max(0, currentTotalHours + hoursToAdd));

        userRepository.save(volunteer);
        return projectRegistrationRepository.save(registration);
    }
}
