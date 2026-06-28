package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByProjectId(Long projectId);

    @Query("SELECT COALESCE(SUM(d.amount), 0.0) FROM Donation d WHERE d.project.id = :projectId")
    Double getTotalAmountByProjectId(@Param("projectId") Long projectId);
}
