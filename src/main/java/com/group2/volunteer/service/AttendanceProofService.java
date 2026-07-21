package com.group2.volunteer.service;

import com.group2.volunteer.entity.AttendanceProof;
import com.group2.volunteer.entity.ProjectRegistration;

import java.util.List;

public interface AttendanceProofService {
    List<ProjectRegistration> getRegistrationsByVolunteer(Long volunteerId);

    List<ProjectRegistration> getApprovedRegistrationsByVolunteer(Long volunteerId);

    Long countAttendedProjectsByVolunteer(Long volunteerId);

    List<AttendanceProof> getProofsWaitingForVerification();

    List<AttendanceProof> getProofsWaitingForVerificationByProject(Long projectId);

    AttendanceProof submitProof(Long registrationId, Long volunteerId, String reportText, String proofImage);

    ProjectRegistration verifyAttendance(Long proofId);

    ProjectRegistration verifyAttendanceForProject(Long proofId, Long projectId);
}
