package com.group2.volunteer.service;

import com.group2.volunteer.constant.RegistrationStatus;
import com.group2.volunteer.constant.ProjectStatus;
import com.group2.volunteer.entity.AttendanceProof;
import com.group2.volunteer.entity.ProjectRegistration;
import com.group2.volunteer.exception.BadRequestException;
import com.group2.volunteer.exception.InvalidAttendanceProofException;
import com.group2.volunteer.exception.ResourceNotFoundException;
import com.group2.volunteer.repository.AttendanceProofRepository;
import com.group2.volunteer.repository.ProjectRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceProofServiceImpl implements AttendanceProofService {
    private final AttendanceProofRepository attendanceProofRepository;
    private final ProjectRegistrationRepository projectRegistrationRepository;

    @Override
    public List<ProjectRegistration> getRegistrationsByVolunteer(Long volunteerId) {
        return projectRegistrationRepository.findByVolunteerIdOrderByRegistrationDateDesc(volunteerId);
    }

    @Override
    public List<ProjectRegistration> getApprovedRegistrationsByVolunteer(Long volunteerId) {
        return projectRegistrationRepository
                .findByVolunteerIdAndStatusAndProject_StatusOrderByRegistrationDateDesc(
                        volunteerId, RegistrationStatus.APPROVED, ProjectStatus.COMPLETED);
    }

    @Override
    public Long countAttendedProjectsByVolunteer(Long volunteerId) {
        return projectRegistrationRepository
                .countByVolunteerIdAndStatus(volunteerId, RegistrationStatus.ATTENDED);
    }

    @Override
    public List<AttendanceProof> getProofsWaitingForVerificationByProject(Long projectId) {
        return attendanceProofRepository
                .findByRegistration_Project_IdAndRegistration_Status(projectId, RegistrationStatus.APPROVED);
    }

    @Override
    public void validateProofSubmission(Long registrationId, Long volunteerId) {
        getValidatedRegistration(registrationId, volunteerId);
    }

    private ProjectRegistration getValidatedRegistration(Long registrationId, Long volunteerId) {
        ProjectRegistration registration = projectRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new InvalidAttendanceProofException(
                        "Không tìm thấy thông tin tham gia dự án."));

        if (registration.getVolunteer() == null
                || !registration.getVolunteer().getId().equals(volunteerId)) {
            throw new InvalidAttendanceProofException(
                    "Không thể nộp minh chứng cho Volunteer khác.");
        }

        if (!RegistrationStatus.APPROVED.equals(registration.getStatus())) {
            throw new InvalidAttendanceProofException(
                    "Chỉ Volunteer đã được duyệt tham gia dự án mới có thể nộp minh chứng.");
        }

        if (registration.getProject() == null
                || !ProjectStatus.COMPLETED.equals(registration.getProject().getStatus())) {
            throw new InvalidAttendanceProofException(
                    "Chỉ có thể nộp minh chứng sau khi dự án hoàn thành.");
        }

        return registration;
    }

    @Override
    @Transactional
    public AttendanceProof submitProof(Long registrationId, Long volunteerId, String reportText, String proofImage) {
        ProjectRegistration registration = getValidatedRegistration(registrationId, volunteerId);

        if (reportText == null || reportText.isBlank()) {
            throw new InvalidAttendanceProofException("Cần nhập nội dung báo cáo.");
        }

        if (proofImage == null || proofImage.isBlank()) {
            throw new InvalidAttendanceProofException("Cần tải lên ảnh minh chứng.");
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
    public ProjectRegistration verifyAttendanceForProject(Long proofId, Long projectId) {
        AttendanceProof proof = attendanceProofRepository.findById(proofId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy minh chứng."));

        ProjectRegistration registration = proof.getRegistration();
        if (projectId != null && (registration.getProject() == null
                || !projectId.equals(registration.getProject().getId()))) {
            throw new BadRequestException("Minh chứng không thuộc dự án này.");
        }
        if (!RegistrationStatus.APPROVED.equals(registration.getStatus())) {
            throw new BadRequestException(
                    "Chỉ có thể xác nhận Volunteer đã được duyệt tham gia dự án.");
        }
        if (registration.getProject() == null
                || !ProjectStatus.COMPLETED.equals(registration.getProject().getStatus())) {
            throw new BadRequestException(
                    "Chỉ có thể xác nhận tham gia sau khi dự án hoàn thành.");
        }
        registration.setStatus(RegistrationStatus.ATTENDED);
        return projectRegistrationRepository.save(registration);
    }
}
