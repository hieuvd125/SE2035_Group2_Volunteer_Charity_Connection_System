package com.group2.volunteer.repository;

import com.group2.volunteer.entity.AttendanceProof;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceProofRepository extends JpaRepository<AttendanceProof, Long> {
    Optional<AttendanceProof> findByRegistrationId(Long registrationId);

    List<AttendanceProof> findByRegistration_Status(String status);

    List<AttendanceProof> findByRegistration_Project_IdAndRegistration_Status(Long projectId, String status);
}
