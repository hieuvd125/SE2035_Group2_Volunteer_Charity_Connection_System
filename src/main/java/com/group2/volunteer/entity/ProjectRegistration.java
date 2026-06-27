package com.group2.volunteer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reg_volunteer"))
    private User volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, foreignKey = @ForeignKey(name = "fk_reg_project"))
    private Project project;

    @Column(name = "registration_date", insertable = false, updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime registrationDate;

    @Column(nullable = false, length = 30)
    private String status = "PENDING";

    @Column(name = "confirmed_hours")
    private Integer confirmedHours = 0;
}
