package com.group2.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_proofs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceProof {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false, unique = true, foreignKey = @ForeignKey(name = "fk_proof_registration"))
    private ProjectRegistration registration;

    @Column(name = "proof_image", length = 255)
    private String proofImage;

    @Column(name = "report_text", columnDefinition = "TEXT")
    private String reportText;

    @Column(name = "submitted_at", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime submittedAt;
}
