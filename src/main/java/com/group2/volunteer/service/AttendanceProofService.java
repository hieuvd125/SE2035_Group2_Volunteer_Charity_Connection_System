package com.group2.volunteer.service;

import com.group2.volunteer.entity.AttendanceProof;
import com.group2.volunteer.entity.ProjectRegistration;

import java.util.List;

public interface AttendanceProofService {
    List<ProjectRegistration> getRegistrationsByVolunteer(Long volunteerId);

    List<AttendanceProof> getProofsWaitingForVerification();

    AttendanceProof submitProof(Long registrationId, Long volunteerId, String reportText, String proofImage);

    ProjectRegistration verifyAttendance(Long proofId, Integer confirmedHours);
}
