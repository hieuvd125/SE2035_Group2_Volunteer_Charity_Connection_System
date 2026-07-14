package com.group2.volunteer.repository;

import com.group2.volunteer.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    List<Donation> findByProjectIdOrderByDonatedAtDesc(Long projectId);
    @Query("SELECT SUM(d.amount) from Donation d WHERE d.project.id = :projectId ")
    Double getTotalDonationAmount(@Param("projectId") Long projectId);

}